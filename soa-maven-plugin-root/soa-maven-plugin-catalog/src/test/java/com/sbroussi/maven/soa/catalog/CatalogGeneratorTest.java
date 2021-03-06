package com.sbroussi.maven.soa.catalog;

import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.scan.DtoCatalogScanner;
import com.sbroussi.dto.test.TestRequest;
import com.sbroussi.dto.test.testA.TestRequestA;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Test: CatalogGenerator.
 */
public class CatalogGeneratorTest {

    @Test
    public void testGenerateCatalog() throws Exception {

        final String outputDirectory = System.getProperty("java.io.tmpdir") + "/soa-catalog";
        final String encoding = "UTF-8";
        final boolean autodetect = false;

        // list of packages containing DTOs
        final List<String> packagesList = asList(
                TestRequest.class.getPackage().getName(), //    "com.sbroussi.dto.test"
                TestRequestA.class.getPackage().getName(), //   "com.sbroussi.dto.test.testA"
                Test.class.getPackage().getName() //            "org.junit" in "junit.jar"
        );

        // scan DTOs
        final DtoCatalog dtoCatalog = new DtoCatalog();
        final DtoCatalogScanner scanner = new DtoCatalogScanner(dtoCatalog);
        scanner.scanPackages(autodetect, packagesList);

        // generate SOA Catalog
        Log log = new SystemStreamLog();
        final String now = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        final CatalogGenerator generator = new CatalogGenerator(
                log,
                now,
                "test-project",
                "test-version",
                dtoCatalog,
                outputDirectory,
                encoding);
        generator.generate();

    }
}
