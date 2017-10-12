package com.sbroussi.dto.scan;

import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will find all DTOs that are in the classpath ('@DtoRequest' or '@DtoResponse').
 * <p>
 * Is is recommended to keep one instance of this class in your application,
 * <p>
 * Note: This is NOT required to run standard applications.
 * The 'scan' method is implemented to help 'catalog generation', 'website generation'
 * or 'development' tooling (DEBUGREQ).
 */
@Slf4j
@Getter
@Setter
public class DtoCatalogScanner {

    private final DtoCatalog dtoCatalog;

    /**
     * The URL to all scanned packages.
     */
    Collection<URL> packageUrls = new ArrayList<URL>();


    /**
     * Constructor.
     *
     * @param dtoCatalog the 'DtoCatalog' to register all detected DTOs.
     */
    public DtoCatalogScanner(final DtoCatalog dtoCatalog) {
        this.dtoCatalog = dtoCatalog;

    }


    /**
     * This method scan all the classes in the Classpath to detect annotations '@DtoRequest' or '@DtoResponse'.
     *
     * @param basePackage the java packages to scan
     */
    public void scanPackage(final String basePackage) {

        List<String> basePackages = new ArrayList<String>(1);
        basePackages.add(basePackage);
        scanPackages(basePackages);
    }

    /**
     * This method scan all the classes in the Classpath to detect annotations '@DtoRequest' or '@DtoResponse'.
     * <p>
     * Performance: please specify a limited number of packages to avoid scanning all the classes loaded in the JVM.
     *
     * @param basePackages the list of java packages to scan
     */
    public void scanPackages(final List<String> basePackages) {
        final ClassLoader classLoader = DtoUtils.getDefaultClassLoader();

        // from Spring: org.springframework.context.annotation.ClassPathBeanDefinitionScanner
        //  resourcePattern="**/*.class";

        final long start = System.currentTimeMillis();

        for (final String basePackage : basePackages) {

            if (log.isInfoEnabled()) {
                log.info("scan package [" + basePackage + "]");
            }

            final String resourceName = ScanUtils.folderName(basePackage);
            final Collection<URL> urls = findResource(classLoader, resourceName);
            if (!urls.isEmpty()) {
                packageUrls.addAll(urls);
            }
        }

        UrlScannerFactory factory = new UrlScannerFactory();

        for (final URL url : packageUrls) {
            final UrlScanner container = factory.create(url);
            List<String> classNames = (container == null) ? null : container.getClassNames(url, basePackages);
            if (classNames != null) {
                for (final String className : classNames) {

                    try {

                        final Class<?> clazz = classLoader.loadClass(className);
                        final DtoRequest dtoRequest = clazz.getAnnotation(DtoRequest.class);
                        if (dtoRequest != null) {
                            if (dtoCatalog.scanDtoRequest(clazz)) {
                                log.debug("found DtoRequest [" + className + "]");
                            }

                        } else {
                            final DtoResponse dtoResponse = clazz.getAnnotation(DtoResponse.class);
                            if (dtoResponse != null) {
                                if (dtoCatalog.scanDtoResponse(clazz)) {
                                    log.debug("found DtoResponse [" + className + "]");
                                }

                            }
                        }
                    } catch (Throwable t) {
                        if (log.isDebugEnabled()) {
                            log.debug("cannot load class [" + className + "]; " + t.getMessage());
                        }
                    }
                }
            }


        }


        final long duration = System.currentTimeMillis() - start;
        log.info("scan finished; [" + duration + " ms] to scan ["
                + basePackages.size() + "] packages and ["
                + packageUrls.size() + "] URLs to detect ["
                + dtoCatalog.getRequests().size() + "] requests and ["
                + dtoCatalog.getResponses().size() + "] responses");

    }


    /**
     * @return the list of URLs, not null
     */
    private static Collection<URL> findResource(final ClassLoader classLoader, final String resourceName) {
        // from https://github.com/ronmamo/reflections
        final List<URL> result = new ArrayList<URL>();
        try {
            final Enumeration<URL> urls = classLoader.getResources(resourceName);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();

                if (log.isDebugEnabled()) {
                    log.debug("resource [" + resourceName + "] found in URL [" + url.toExternalForm() + "]");
                }

                int index = url.toExternalForm().lastIndexOf(resourceName);
                if (index != -1) {
                    // Add old url as contextUrl to support exotic url handlers
                    result.add(new URL(url, url.toExternalForm().substring(0, index)));
                } else {
                    result.add(url);
                }
            }
        } catch (Exception e) {
            log.error("error while scanning [" + resourceName + "]", e);

        }
        return distinctUrls(result);
    }


    private static Collection<URL> distinctUrls(final Collection<URL> urls) {
        // from http://michaelscharf.blogspot.co.il/2006/11/javaneturlequals-and-hashcode-make.html
        Map<String, URL> distinct = new LinkedHashMap<String, URL>(urls.size());
        for (final URL url : urls) {
            distinct.put(url.toExternalForm(), url);
        }
        return distinct.values();
    }

}
