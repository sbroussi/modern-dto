package com.sbroussi.maven.soa.catalog;


import com.google.common.collect.Lists;
import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.scan.DtoCatalogScanner;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Generates the SOA catalog.
 */
@Mojo(name = "report", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class CatalogReportMojo extends AbstractMavenReport {

    /**
     * Name of the report to generate.
     */
    @Parameter(
            property = "outputName",
            defaultValue = "soa-catalog-report",
            required = true
    )
    private String outputName;

    /**
     * Reporting output directory.
     */
    @Parameter(
            property = "project.reporting.outputDirectory",
            required = true
    )
    private File outputDirectory;

    /**
     * The output directory for the generated files.
     */
    @Parameter(
            property = "catalogOutputDirectory",
            defaultValue = "${project.reporting.outputDirectory}/soa-catalog",
            required = true
    )
    private File catalogOutputDirectory;

    /**
     * The output encoding used.
     */
    @Parameter(
            alias = "encoding",
            defaultValue = "UTF-8",
            required = false
    )
    private String encoding;


    /**
     * TRUE to auto-detect all DTOs by reading files 'config/soa/dto.packages' present in the classpath.
     * Defaults to TRUE.
     */
    @Parameter(
            alias = "autodetect",
            defaultValue = "true",
            required = false
    )
    private String autodetect;

    /**
     * The list of Java packages that contain DTOs requests.
     * <p>
     * The list is comma separated ('com.acme.beans, com.app1.beans, com.app2.beans').
     */
    @Parameter(
            alias = "scanPackages",
            defaultValue = "",
            required = false
    )
    private String scanPackages;

    /**
     * Current Maven project.
     */
    @Parameter(
            defaultValue = "${project}",
            required = true,
            readonly = true
    )
    private MavenProject project;

    /**
     * This plugin's descriptor.
     */
    @Parameter(
            defaultValue = "${plugin}",
            required = true,
            readonly = true
    )
    private PluginDescriptor plugin;


    @Component
    private Renderer siteRenderer;

    public File getCatalogOutputDirectory() {
        return catalogOutputDirectory;
    }

    public String getEncoding() {
        return encoding;
    }

    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    @Override
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    @Override
    protected MavenProject getProject() {
        return project;
    }

    @Override
    public String getName(Locale locale) {
        return getBundle(locale).getString("report.soa.catalog.name");
    }

    @Override
    public String getDescription(Locale locale) {
        return getBundle(locale).getString("report.soa.catalog.description");
    }

    @Override
    public String getOutputName() {
        return outputName;
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }


    List<URL> getProjectClasspathElementURLs() {
        try {
            List<URL> urls = Lists.newArrayList();

            for (Object root : getProject().getCompileSourceRoots()) {
                URL url = new File((String) root).toURI().toURL();
                urls.add(url);
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Source root: " + url);
                }
            }

            for (Object root : getProject().getCompileClasspathElements()) {
                URL url = new File((String) root).toURI().toURL();
                urls.add(url);
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Compile classpath: " + url);
                }
            }

            return urls;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    ClassLoader getClassLoader() {
        try {
            ClassRealm realm = plugin.getClassRealm();

            // need to add the project classpath elements to the
            // active class realm in order for them to be accessible
            // during site generation.

            for (URL url : getProjectClasspathElementURLs()) {
                realm.addURL(url);
            }

            return realm;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        getLog().info("generate catalog...");

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {

            // force the context classloader to use the new class
            // realm which contains both plugin classpath elements
            // and project classpath elements.

            ClassLoader classLoader = getClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);

            // list of packages containing DTOs separated by comma: 'com.acme, com.acme.beans'
            final List<String> packagesList = ((scanPackages == null) || (scanPackages.length() == 0))
                    ? new ArrayList<String>()
                    : Arrays.asList(scanPackages.split("[\\s]*,[\\s]*"));

            // scan DTOs
            DtoCatalog dtoCatalog = new DtoCatalog();
            DtoCatalogScanner scanner = new DtoCatalogScanner(dtoCatalog);
            scanner.scanPackages(Boolean.valueOf(autodetect), packagesList);

            final String now = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

            // generate SOA Catalog
            CatalogGenerator generator = new CatalogGenerator(
                    getLog(),
                    now,
                    (project == null) ? null : project.getName(),
                    (project == null) ? null : project.getVersion(),
                    dtoCatalog,
                    getOutputDirectory(),
                    getEncoding());
            generator.generate();

            fillReportPage(dtoCatalog, now);


        } catch (Exception ex) {
            throw new MavenReportException(ex.getMessage(), ex);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Fill the 'Maven report' page,
     */
    private void fillReportPage(final DtoCatalog dtoCatalog, final String now) {
        Sink sink = getSink();


        sink.head();
        sink.title();
        sink.text("SOA Catalog");
        sink.title_();
        sink.head_();

        sink.body();

        sink.section1();
        sink.sectionTitle1();
        sink.text("SOA Catalog");
        sink.sectionTitle1_();

        sink.text("Browse the ");
        sink.link("./soa-catalog/index.html");
        sink.text("SOA Catalog");
        sink.link_();
        sink.text(".");

        sink.paragraph();
        sink.text("The SOA Catalog");
        if (project != null) {
            sink.text(" of project '");
            sink.bold();
            sink.text(project.getName());
            sink.bold_();
            sink.text("' version '");
            sink.bold();
            sink.text(project.getVersion());
            sink.bold_();
            sink.text("'");
        }
        sink.text(" contains:");

        sink.lineBreak();
        sink.text("- " + dtoCatalog.getRequests().size() + " requests");
        sink.lineBreak();
        sink.text("- " + dtoCatalog.getResponses().size() + " responses");
        sink.paragraph_();

        sink.paragraph();
        sink.text("Generated " + now);
        sink.paragraph_();

        sink.section1_();


        sink.body_();
    }

    public ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle("soa-catalog-report", locale, this.getClass().getClassLoader());
    }


}
