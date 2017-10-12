package com.sbroussi.dto.scan;

import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.test.MyBean;
import com.sbroussi.dto.test.MyBeanResponse;
import com.sbroussi.dto.test.TestRequest;
import com.sbroussi.dto.test.TestResponse;
import com.sbroussi.dto.test.testA.TestRequestA;
import com.sbroussi.dto.test.testB.TestRequestB;
import com.sbroussi.dto.test.testB.TestResponseB;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Collection;

import static java.util.Arrays.asList;

@Slf4j
public class DtoCatalogScannerTest {

    @Test
    public void testScanPackagesInJAR() {
        DtoCatalog dtoCatalog = new DtoCatalog();
        DtoCatalogScanner scanner = new DtoCatalogScanner(dtoCatalog);

        // scan a classes in one JAR
        scanner.scanPackage(Test.class.getPackage().getName()); // "org.junit" in "junit.jar"
        Assert.assertEquals(0, dtoCatalog.getRequests().size());
        Assert.assertEquals(0, dtoCatalog.getResponses().size());

        // check that the JAR containing the "org.junit" package is found in the classpath
        // url= jar:file:/USERDATA/HOMEasus/stephane/.m2/repository/junit/junit/4.12/junit-4.12.jar!/
        Collection<URL> packageUrls = scanner.getPackageUrls();
        Assert.assertTrue(packageUrls.size() > 0); // at least 1 JAR
        boolean foundJAR = false;
        for (URL url : packageUrls) {
            String urlStr = url.toExternalForm();
            if (urlStr.contains("/junit")) {
                log.debug("found JUNIT JAR file: " + urlStr);
                foundJAR = true;
                break;
            }
        }
        if (!foundJAR) {
            Assert.fail("JUNIT JAR not found: " + packageUrls);
        }

    }

    @Test
    public void testScanPackagesInFolder() {
        DtoCatalog dtoCatalog = new DtoCatalog();
        DtoCatalogScanner scanner = new DtoCatalogScanner(dtoCatalog);

        // The classes 'MyBean' and 'MyBeanResponse' are in the same project
        // These classes are found in the FileSystem as 'File' by IntelliJ and Maven (target/classes)

        // scan
        scanner.scanPackage(MyBean.class.getPackage().getName()); // "com.sbroussi.dto.test"

        Assert.assertNotNull(dtoCatalog.getRequests().get(MyBean.class.getName()));
        Assert.assertNotNull(dtoCatalog.getResponses().get(MyBeanResponse.class.getName()));
    }

    @Test
    public void testScanPackagesInFolderAndJar() {
        DtoCatalog dtoCatalog = new DtoCatalog();
        DtoCatalogScanner scanner = new DtoCatalogScanner(dtoCatalog);

        // the classes 'TestRequestA' and 'TestResponseA' are in JAR file 'soa-dto-api-test.jar'

        // scan
        scanner.scanPackages(asList(
                MyBean.class.getPackage().getName(), //         "com.sbroussi.dto.test"
                TestRequest.class.getPackage().getName(), //    "com.sbroussi.dto.test"
                TestRequestA.class.getPackage().getName(), //   "com.sbroussi.dto.test.testA"
                Test.class.getPackage().getName() //            "org.junit" in "junit.jar"
        ));

        Assert.assertNotNull(dtoCatalog.getRequests().get(MyBean.class.getName()));
        Assert.assertNotNull(dtoCatalog.getResponses().get(MyBeanResponse.class.getName()));

        Assert.assertNotNull(dtoCatalog.getRequests().get(TestRequest.class.getName()));
        Assert.assertNotNull(dtoCatalog.getResponses().get(TestResponse.class.getName()));

        // TODO: class not found when running with MAVEN test, it works with IntelliJ
        //Assert.assertNotNull(dtoCatalog.getRequests().get(TestRequestA.class.getName()));
        //Assert.assertNotNull(dtoCatalog.getResponses().get(TestResponseA.class.getName()));

        // folder '"com.sbroussi.dto.test.testB' was not included: not scanned
        Assert.assertNull(dtoCatalog.getRequests().get(TestRequestB.class.getName()));
        Assert.assertNull(dtoCatalog.getResponses().get(TestResponseB.class.getName()));
    }
}
