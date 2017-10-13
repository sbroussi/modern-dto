package com.sbroussi.maven.soa.catalog;

import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.catalog.DtoCatalogExtended;
import com.sbroussi.dto.catalog.DtoRequestBean;
import com.sbroussi.dto.catalog.DtoResponseBean;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Generate old catalog (migrated from ing-components/ing-soa-catalog).
 */
public class CatalogGenerator implements URIResolver {

    private final Log log;
    private final DtoCatalog dtoCatalog;
    private final List<String> packagesList;
    private final String outputDirectory;
    private final String encoding;

    public CatalogGenerator(final Log log,
                            final DtoCatalog dtoCatalog,
                            final List<String> packagesList,
                            final String outputDirectory,
                            final String encoding) {
        this.log = log;
        this.dtoCatalog = dtoCatalog;
        this.packagesList = packagesList;
        this.outputDirectory = outputDirectory;
        this.encoding = encoding;

    }

    public void generate() throws MavenReportException {

        log.info("Generate catalog to folder: [" + outputDirectory
                + "] for packages " + packagesList);

        try {

            // load definitions of all DTOs
            final DtoCatalogExtended catalogExtended = new DtoCatalogExtended(dtoCatalog);
            catalogExtended.refresh();


            Velocity.setProperty("parser.pool.size", "1");
            Velocity.setProperty("resource.loader", "class");
            Velocity.setProperty("runtime.references.strict", "true");
            Velocity.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
            Velocity.init();

            final String now = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setURIResolver(this);

            // Request Details
            final Map<String, DtoRequestBean> requests = catalogExtended.getRequestBeans();
            final Map<String, DtoResponseBean> responses = catalogExtended.getResponseBeans();

            log.info(String.format("Generate catalog for %d request(s)", requests.size()));
            final Template templateRequest = Velocity.getTemplate("/templates/request.xml.vm");
            for (final DtoRequestBean bean : requests.values()) {

                String requestFilename = getRequestFilename(bean.getDtoClass());

                VelocityContext context = new VelocityContext();
                context.put("generator", this);
                context.put("dtoCatalog", dtoCatalog);
                context.put("createdAt", now);
                context.put("bean", bean);

                File outputFileHtml = new File(outputDirectory, "html/" + requestFilename + ".html");

                createFile(outputFileHtml, templateRequest, context);
            }


            // Static resources
            copyResource("/www/css/catalog.css", new File(outputDirectory, "css/catalog.css"));
            copyResource("/www/img/logo.jpg", new File(outputDirectory, "img/logo.jpg"));
            copyResource("/www/js/jquery-1.7.min.js", new File(outputDirectory, "js/jquery-1.7.min.js"));
            copyResource("/www/banner.html", new File(outputDirectory, "banner.html"));
            copyResource("/www/welcome.html", new File(outputDirectory, "welcome.html"));
            copyResource("/www/index.html", new File(outputDirectory, "index.html"));

        } catch (Exception ex) {
            throw new MavenReportException(ex.getMessage(), ex);
        }
    }


    private void createFile(final File outputFile, final Template template, final VelocityContext context) throws IOException {
        log.debug("generate: " + outputFile.getName());
        Files.createParentDirs(outputFile);

        Closer closer = Closer.create();
        try {
            Writer writer = closer.register(Files.newWriter(outputFile, Charset.forName(encoding)));
            template.merge(context, writer);
        } catch (Throwable ex) {
            throw closer.rethrow(ex);
        } finally {
            closer.close();
        }
    }

    public String getRequestFilename(Class clazz) {
        return getRequestFilename(clazz.getName());
    }

    public String getRequestFilename(String className) {
        return "request_" + className;
    }

    public String getResponseFilename(Class clazz) {
        return getResponseFilename(clazz.getName());
    }

    public String getResponseFilename(String className) {
        return "response_" + className;
    }


    private void copyResource(final String resource, final File outputFile) throws IOException {
        Files.createParentDirs(outputFile);
        Resources.asByteSource(getResource(resource)).copyTo(Files.asByteSink(outputFile));
    }

    private Source asSource(final String resource) {
        try {
            return new StreamSource(Resources.asByteSource(getResource(resource)).openBufferedStream());
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private URL getResource(final String resource) {
        return Resources.getResource(this.getClass(), resource);
    }

    @Override
    public Source resolve(final String href, final String base) throws TransformerException {
        return asSource("/xsl/" + href);
    }
}
