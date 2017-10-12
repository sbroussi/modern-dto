package com.sbroussi.dto.scan;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Classpath entry: a Folder.
 * <p>
 * Contains resources stored in a Folder of the File System.
 * <p>
 * url = file:/HOME/project/target/test-classes/
 */
public class ScannerDirectory implements UrlScanner {


    // -------------------- from interface UrlScanner
    @Override
    public boolean matches(final URL url) {
        if (url.getProtocol().equals("file")) {
            final File file = getFile(url);
            return file != null && file.isDirectory();
        }
        return false;
    }


    @Override
    public List<String> getClassNames(final URL url, final List<String> basePackages) {

        final File parentFolder = getFile(url);


        final List<String> result = new ArrayList<String>();


        // parent folder is:
        //         /HOME/project/target/test-classes
        // we must scan all subfolders corresponing the the searched packages:
        //         /HOME/project/target/test-classes/com/acme/beans
        //         /HOME/project/target/test-classes/com/beans
        //         /...
        for (final String basePackage : basePackages) {
            final String subFolder = ScanUtils.folderName(basePackage);
            final File folder = new File(parentFolder + "/" + subFolder);
            if (folder.exists() && folder.isDirectory()) {

                final File[] files = folder.listFiles();
                if (files != null) {
                    for (final File f : files) {
                        if (!f.isDirectory()) {
                            // found 'MyBean.class' in folder /HOME/project/target/test-classes/com/acme/beans/MyBean.class
                            if (f.getName().endsWith(".class")) {
                                // build: com.acme.beans.MyBean
                                final String className = ScanUtils.className(basePackage + "." + f.getName());
                                result.add(className);
                            }
                        }
                    }
                }
            }

        }


        return result;
    }


    // --------------- utility method

    /**
     * try to get {@link File} from url
     */
    private static File getFile(final URL url) {
        File file;
        String path;

        try {
            path = url.toURI().getSchemeSpecificPart();
            if ((file = new File(path)).exists()) return file;
        } catch (URISyntaxException e) {
        }

        try {
            path = URLDecoder.decode(url.getPath(), "UTF-8");
            if (path.contains(".jar!")) path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
            if ((file = new File(path)).exists()) return file;

        } catch (UnsupportedEncodingException e) {
        }

        try {
            path = url.toExternalForm();
            if (path.startsWith("jar:")) path = path.substring("jar:".length());
            if (path.startsWith("wsjar:")) path = path.substring("wsjar:".length());
            if (path.startsWith("file:")) path = path.substring("file:".length());
            if (path.contains(".jar!")) path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
            if ((file = new File(path)).exists()) return file;

            path = path.replace("%20", " ");
            if ((file = new File(path)).exists()) return file;

        } catch (Exception e) {
        }

        return null;

    }
}

