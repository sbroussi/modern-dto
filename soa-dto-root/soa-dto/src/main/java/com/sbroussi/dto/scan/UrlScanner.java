package com.sbroussi.dto.scan;

import java.net.URL;
import java.util.List;

/**
 * A file system folder or a JAR file containing java classes (*,class).
 * <p>
 * From: https://github.com/ronmamo/reflections.
 */
public interface UrlScanner {

    /**
     * @param url the URL to scan
     * @return TRUE if the UrlScanner can analyse the content of this URL
     */
    boolean matches(URL url);

    /**
     * @param url          the URL to scan
     * @param basePackages the list of java packages to scan
     * @return the list of full qualified names of Classes found (for example: 'com.acme.bean.MyBean')
     */
    List<String> getClassNames(URL url, final List<String> basePackages);

}
