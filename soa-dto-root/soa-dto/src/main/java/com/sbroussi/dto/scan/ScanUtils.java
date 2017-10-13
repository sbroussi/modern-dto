package com.sbroussi.dto.scan;

import com.sbroussi.dto.DtoException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * From: https://github.com/ronmamo/reflections.
 */
@Slf4j
public class ScanUtils {

    /**
     * @param packageName the name of the class (for example: 'com.acme.beans')
     * @return the package name as a 'folder' (for example: 'com/acme/beans'),
     */
    public static String folderName(final String packageName) {
        if (packageName != null) {
            String resourceName = packageName.replace(".", "/");
            resourceName = resourceName.replace("\\", "/");
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }
            if (resourceName.endsWith("/")) {
                resourceName = resourceName.substring(0, resourceName.length() - 1);
            }
            return resourceName;
        }
        return null;
    }

    /**
     * @param resourceName the class name as a 'resource' (for example: 'com/acme/beans/MyBean.class'),
     * @return the name of the class (for example: 'com.acme.beans.MyBean')
     */
    public static String className(final String resourceName) {
        if (resourceName != null) {
            String className = resourceName.replace("/", ".");
            className = className.replace("\\", ".");
            if (className.startsWith(".")) {
                className = className.substring(1);
            }
            if (className.endsWith(".class")) {
                className = className.substring(0, className.length() - 6);
            }
            return className;
        }
        return null;
    }


    /**
     * Read all files 'config/soa/dto.packages' present in the classpath to scan specified packages,
     * <p>
     * Syntax of files 'config/soa/dto.packages' :
     * <p>
     * -  DTO convention: the file name is always 'config/soa/dto.packages' in all the JARs
     * <p>
     * - enter the list of java packages that contain @DtoRequest or @DtoResponse classes
     * <p>
     * - the list of packages can be separated by CR/LF or comma ','
     * <p>
     * - comment lines starting with '#' are ignored
     */
    public static List<String> autodetectPackages(final ClassLoader classLoader) {
        final ArrayList<String> packages = new ArrayList<String>();
        try {
            Enumeration<URL> urls = classLoader.getResources("config/soa/dto.packages");
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    packages.addAll(readDtoPackages(urls.nextElement()));
                }
            }

        } catch (Throwable t) {
            throw new DtoException("cannot autodetect DTO packages", t);
        }


        return packages;
    }

    /**
     * read file 'config/soa/dto.packages'.
     */
    private static List<String> readDtoPackages(final URL url) throws Exception {
        final ArrayList<String> packages = new ArrayList<String>();

        // DTO convention: the file name is always 'config/soa/dto.packages' in all the JARs
        // enter the list of java packages that contain @DtoRequest or @DtoResponse classes
        // the list of packages can be separated by CR/LF or comma ','
        // comment lines starting with '#' are ignored

        String text = (url == null) ? null : ScanUtils.readTextFile(url, "ISO-8859-1", "\n");
        if (text != null) {

            // clean CR/LF
            text = text.replaceAll("\\r\\n", "\n");
            text = text.replaceAll("\\r", "\n");

            // split the lines
            final String[] lines = text.split("[\\s]*\\n[\\s]*");
            for (final String l : lines) {
                final String line = l.trim();

                // ignore empty lines and comments
                if ((line.length() == 0) || (line.startsWith("#"))) {
                    continue;
                }

                // split 'comma' separated list 'a, b'
                final String[] entries = line.split("[\\s]*,[\\s]*");
                for (final String entry : entries) {
                    final String value = entry.trim();
                    if (value.length() > 0) {
                        packages.add(value);
                    }
                }
            }
            if (log.isInfoEnabled()) {
                log.info("DTO auto-detection: found [" + url + "] defining [" + packages.size()
                        + "] packages: " + packages);
            }
        }
        return packages;
    }

    public static String readTextFile(final URL url, final String encoding, final String crlf) throws Exception {
        if (url == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(4 * 1024);
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine).append(crlf);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return sb.toString();
    }

}
