package com.sbroussi.maven.soa.catalog;

import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.catalog.DtoCatalogExtended;
import com.sbroussi.dto.catalog.DtoDatatypeBean;
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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Generate old catalog (migrated from ing-components/ing-soa-catalog).
 */
public class CatalogGenerator implements URIResolver {

    private final Log log;
    private final String now;
    private final String projectName;
    private final String projectVersion;
    private final String projectInfosHtml;

    private final DtoCatalog dtoCatalog;
    private final String outputDirectoryRoot;
    private final String outputDirectory;
    private final String encoding;

    public CatalogGenerator(final Log log,
                            final String now,
                            final String projectName,
                            final String projectVersion,
                            final DtoCatalog dtoCatalog,
                            final String outputDirectory,
                            final String encoding) {
        this.log = log;
        this.now = now;
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.dtoCatalog = dtoCatalog;
        this.outputDirectoryRoot = outputDirectory;
        this.outputDirectory = outputDirectory + "/soa-catalog";
        this.encoding = encoding;

        this.projectInfosHtml = (projectName != null)
                ? "Project '<strong>" + projectName + "</strong>' version '<strong>" + projectVersion + "</strong>'<hr/>"
                : "";

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

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setURIResolver(this);

            // read main lists
            final Map<String, Set<DtoRequestBean>> apps = catalogExtended.getRequestsByApplicationId();
            final Map<String, DtoRequestBean> requests = catalogExtended.getRequestBeans();
            final Map<String, DtoResponseBean> responses = catalogExtended.getResponseBeans();
            final Map<String, DtoDatatypeBean> datatypes = catalogExtended.getDatatypes();
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
            context.put("projectInfosHtml", projectInfosHtml);
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
            Set<DtoDatatypeBean> sortedDatatypes = new TreeSet<DtoDatatypeBean>();
            sortedDatatypes.addAll(datatypes.values());
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("projectInfosHtml", projectInfosHtml);
            context.put("datatypes", sortedDatatypes);
            outputFileHtml = new File(outputDirectory, "html/datatypes-list.html");
            createFile(outputFileHtml, templateDatatypesList, context);

            // Datatypes
            final Template templateDatatype = Velocity.getTemplate("/templates/datatype.html.vm");
            for (final DtoDatatypeBean datatype : datatypes.values()) {

                String filename = getDatatypeFilename(datatype.getName());

                context = new VelocityContext();
                context.put("generator", this);
                context.put("now", now);
                context.put("projectInfosHtml", projectInfosHtml);
                context.put("bean", datatype);

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
            context.put("projectInfosHtml", projectInfosHtml);
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
                context.put("projectInfosHtml", projectInfosHtml);
                context.put("applicationId", applicationId);
                context.put("requests", appRequests);

                outputFileHtml = new File(outputDirectory, "html/" + filename + ".html");

                createFile(outputFileHtml, templateApplication, context);
            }

            // List of Requests (Compact)
            Set<DtoRequestBean> sortedRequests = new TreeSet<DtoRequestBean>();
            sortedRequests.addAll(requests.values());
            final Template templateRequestsListCompact = Velocity.getTemplate("/templates/requests-list-compact.html.vm");
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("projectInfosHtml", projectInfosHtml);
            context.put("requests", sortedRequests);
            outputFileHtml = new File(outputDirectory, "html/requests-list-compact.html");
            createFile(outputFileHtml, templateRequestsListCompact, context);

            // List of Requests
            final Template templateRequestsList = Velocity.getTemplate("/templates/requests-list.html.vm");
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("projectInfosHtml", projectInfosHtml);
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
                context.put("projectInfosHtml", projectInfosHtml);
                context.put("bean", bean);

                outputFileHtml = new File(outputDirectory, "html/" + filename + ".html");

                createFile(outputFileHtml, templateRequest, context);
            }

            // List of Responses (Compact)
            final Template templateResponsesListCompact = Velocity.getTemplate("/templates/responses-list-compact.html.vm");
            Set<DtoResponseBean> sortedResponses = new TreeSet<DtoResponseBean>();
            sortedResponses.addAll(responses.values());
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("projectInfosHtml", projectInfosHtml);
            context.put("responses", sortedResponses);
            outputFileHtml = new File(outputDirectory, "html/responses-list-compact.html");
            createFile(outputFileHtml, templateResponsesListCompact, context);

            // List of Responses
            final Template templateResponsesList = Velocity.getTemplate("/templates/responses-list.html.vm");
            context = new VelocityContext();
            context.put("generator", this);
            context.put("now", now);
            context.put("projectInfosHtml", projectInfosHtml);
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
                context.put("projectInfosHtml", projectInfosHtml);
                context.put("bean", bean);

                outputFileHtml = new File(outputDirectory, "html/" + filename + ".html");

                createFile(outputFileHtml, templateResponse, context);
            }


            // Static resources of SOA Catalog
            copyResource("/www/soa-catalog/css/catalog.css", new File(outputDirectory, "css/catalog.css"));
            copyResource("/www/soa-catalog/img/logo.jpg", new File(outputDirectory, "img/logo.jpg"));
            copyResource("/www/soa-catalog/banner.html", new File(outputDirectory, "banner.html"));
            copyResource("/www/soa-catalog/index.html", new File(outputDirectory, "index.html"));

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

    public String escapeHtml(final String input) {
        if (input == null) {
            return "";
        }
        String html = input.replaceAll("&", "&amp;");
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll(">", "&gt;");
        html = html.replaceAll("\"", "&quote;");
        html = html.replaceAll("'", "&apos;");
        return html;
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
