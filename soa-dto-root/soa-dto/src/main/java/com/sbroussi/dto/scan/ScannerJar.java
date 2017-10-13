package com.sbroussi.dto.scan;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Classpath entry: a JAR or ZIP file.
 * <p>
 * Contains resources stored in a ZIP or JAR file.
 * <p>
 * url = jar:file:/HOME/.m2/repository/com/sbroussi/soa-dto-api-test/2017.10.1/soa-dto-api-test-2017.10.1.jar!/com/sbroussi/dto/test/testA
 */
public class ScannerJar implements UrlScanner {

    // -------------------- from interface UrlScanner
    @Override
    public boolean matches(final URL url) {
        return "jar".equals(url.getProtocol()) || "zip".equals(url.getProtocol()) || "wsjar".equals(url.getProtocol());
    }

    @Override
    public List<String> getClassNames(final URL url, final List<String> basePackages) {

        final ZipFile jarFile;
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                jarFile = ((JarURLConnection) urlConnection).getJarFile();
            } else {
                throw new IOException("unexpected JAR urlConnection [" + urlConnection + "]");
            }
        } catch (Exception e) {
            throw new RuntimeException("cannot open URL [" + url + "]", e);
        }


        final List<String> folderNames = new ArrayList<String>(basePackages.size());
        for (final String p : basePackages) {
            folderNames.add(ScanUtils.folderName(p));
        }

        final List<String> result = new ArrayList<String>();
        final Enumeration<? extends ZipEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                // read: com/acme/beans/MyBean.class
                String name = entry.getName();
                if (name.endsWith(".class")) {

                    // clean entry name stored in the JAR
                    name = name.replace('\\', '/'); // Windows / Unix
                    if (name.startsWith("/")) {
                        name = name.substring(1);
                    }

                    for (final String folder : folderNames) {
                        // test if    : 'com/acme/beans/MyBean.class'
                        // starts with: 'com/acme/beans'
                        if (name.startsWith(folder)) {
                            // but reject sub-packages
                            // for package: 'com/acme/beans'
                            // accept     : 'com/acme/beans/MyBean.class'
                            // reject     : 'com/acme/beans/subPackage/MyBean2.class'
                            final String endOfName = name.substring(folder.length() + 1);
                            if (!endOfName.contains("/")) {
                                result.add(ScanUtils.className(name));
                                break;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }


}

