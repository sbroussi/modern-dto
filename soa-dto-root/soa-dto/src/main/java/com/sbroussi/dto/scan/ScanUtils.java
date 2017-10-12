package com.sbroussi.dto.scan;

/**
 * From: https://github.com/ronmamo/reflections.
 */
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

}
