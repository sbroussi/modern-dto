package com.sbroussi.maven.soa.catalog;

import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.catalog.DtoCatalogExtended;
import com.sbroussi.dto.catalog.DtoFieldBean;
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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Generate old catalog (migrated from ing-components/ing-soa-catalog).
 */
public class CatalogGenerator implements URIResolver {

    private final Log log;
    private final DtoCatalog dtoCatalog;
    private final String outputDirectory;
    private final String encoding;

    public CatalogGenerator(final Log log,
                            final DtoCatalog dtoCatalog,
                            final String outputDirectory,
                            final String encoding) {
        this.log = log;
        this.dtoCatalog = dtoCatalog;
        this.outputDirectory = outputDirectory;
        this.encoding = encoding;

    }

    public void generate() throws MavenReportException {

        log.info("Generate catalog to folder: [" + outputDirectory + "]");

        try {

            // load definitions of all DTOs
            final DtoCatalogExtended catalogExtended = new DtoCatalogExtended(dtoCatalog);
            catalogExtended.scan();


            Velocity.setProperty("parser.pool.size", "1");
            Velocity.setProperty("resource.loader", "class");
            Velocity.setProperty("runtime.references.strict", "true");
            Velocity.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
            Velocity.init();

            final String now = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setURIResolver(this);

            // read main lists
            final Map<String, Set<DtoRequestBean>> apps = catalogExtended.getRequestsByApplicationId();
            final Map<String, DtoRequestBean> requests = catalogExtended.getRequestBeans();
            final Map<String, DtoResponseBean> responses = catalogExtended.getResponseBeans();
            final Map<String, Set<DtoFieldBean>> datatypes = catalogExtended.getFieldsByDatatypes();
            final Set<String> packagesList = catalogExtended.getPackages();
            log.info("Generate catalog for [" + requests.size() + "] requests");
            log.info("Generate catalog for [" + responses.size() + "] responses");
            log.info("Generate catalog for [" + datatypes.size() + "] DataTypes");
            log.info("Generate catalog for [" + apps.size() + "] applications");
            log.info("Generate catalog for [" + packagesList.size() + "] java packages");

            // Welcome page 'index.html'
            final Template templateWelcome = Velocity.getTemplate("/templates/index.html.vm");
            VelocityContext context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("nbRequests", requests.size());
            context.put("nbResponses", responses.size());
            context.put("nbDataTypes", datatypes.size());
            context.put("nbApplications", apps.size());
            context.put("nbPackages", packagesList.size());
            context.put("packagesList", packagesList);
            File outputFileHtml = new File(outputDirectory, "html/index.html");
            createFile(outputFileHtml, templateWelcome, context);


            // List of Datatypes
            final Template templateDatatypesList = Velocity.getTemplate("/templates/datatypes-list.html.vm");
            Set<String> sortedDatatypes = new TreeSet<String>();
            sortedDatatypes.addAll(datatypes.keySet());
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("datatypes", sortedDatatypes);
            outputFileHtml = new File(outputDirectory, "html/datatypes-list.html");
            createFile(outputFileHtml, templateDatatypesList, context);

            // Datatypes
            final Template templateDatatype = Velocity.getTemplate("/templates/datatype.html.vm");
            for (final Map.Entry<String, Set<DtoFieldBean>> entry : datatypes.entrySet()) {

                String datatype = entry.getKey();
                Set<DtoFieldBean> fields = entry.getValue();

                String filename = getDatatypeFilename(datatype);

                context = new VelocityContext();
                context.put("generator", this);
                context.put("now", now);
                context.put("datatype", datatype);
                context.put("fields", fields);

                outputFileHtml = new File(outputDirectory, "html/" + filename + ".html");

                createFile(outputFileHtml, templateDatatype, context);
            }

            // List of Applications
            final Template templateApplicationsList = Velocity.getTemplate("/templates/applications-list.html.vm");
            Set<String> sortedApps = new TreeSet<String>();
            sortedApps.addAll(apps.keySet());
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("applications", sortedApps);
            outputFileHtml = new File(outputDirectory, "html/applications-list.html");
            createFile(outputFileHtml, templateApplicationsList, context);

            // Applications
            final Template templateApplication = Velocity.getTemplate("/templates/application.html.vm");
            for (final Map.Entry<String, Set<DtoRequestBean>> entry : apps.entrySet()) {

                String applicationId = entry.getKey();
                Set<DtoRequestBean> appRequests = entry.getValue();

                String filename = getApplicationFilename(applicationId);

                context = new VelocityContext();
                context.put("generator", this);
                context.put("now", now);
                context.put("applicationId", applicationId);
                context.put("requests", appRequests);

                outputFileHtml = new File(outputDirectory, "html/" + filename + ".html");

                createFile(outputFileHtml, templateApplication, context);
            }

            // List of Requests
            final Template templateRequestsList = Velocity.getTemplate("/templates/requests-list.html.vm");
            Set<DtoRequestBean> sortedRequests = new TreeSet<DtoRequestBean>();
            sortedRequests.addAll(requests.values());
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("requests", sortedRequests);
            outputFileHtml = new File(outputDirectory, "html/requests-list.html");
            createFile(outputFileHtml, templateRequestsList, context);

            // Requests
            final Template templateRequest = Velocity.getTemplate("/templates/request.html.vm");
            for (final DtoRequestBean bean : requests.values()) {

                String filename = getRequestFilename(bean.getDtoClassname());

                context = new VelocityContext();
                context.put("generator", this);
                context.put("now", now);
                context.put("bean", bean);

                outputFileHtml = new File(outputDirectory, "html/" + filename + ".html");

                createFile(outputFileHtml, templateRequest, context);
            }

            // List of Responses
            final Template templateResponsesList = Velocity.getTemplate("/templates/responses-list.html.vm");
            Set<DtoResponseBean> sortedResponses = new TreeSet<DtoResponseBean>();
            sortedResponses.addAll(responses.values());
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("responses", sortedResponses);
            outputFileHtml = new File(outputDirectory, "html/responses-list.html");
            createFile(outputFileHtml, templateResponsesList, context);

            // Responses
            final Template templateResponse = Velocity.getTemplate("/templates/response.html.vm");
            for (final DtoResponseBean bean : responses.values()) {

                String filename = getResponseFilename(bean.getDtoClassname());

                context = new VelocityContext();
                context.put("generator", this);
                context.put("now", now);
                context.put("bean", bean);

                outputFileHtml = new File(outputDirectory, "html/" + filename + ".html");

                createFile(outputFileHtml, templateResponse, context);
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

    public String getDatatypeFilename(final String className) {
        return "datatype_" + className.replace('$', '.');
    }

    public String getRequestFilename(final String className) {
        return "request_" + className.replace('$', '.');
    }


    public String getResponseFilename(final String className) {
        return "response_" + className.replace('$', '.');
    }

    public String getApplicationFilename(final String applicationId) {
        return "app_" + applicationId.replace(' ', '_');
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
