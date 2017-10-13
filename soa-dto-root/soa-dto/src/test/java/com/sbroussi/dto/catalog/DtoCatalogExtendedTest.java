package com.sbroussi.dto.catalog;

import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.DtoException;
import com.sbroussi.dto.scan.DtoCatalogScanner;
import com.sbroussi.dto.test.MyBean;
import com.sbroussi.dto.test.MyBeanWithResponse;
import com.sbroussi.dto.test.testA.TestRequestA;
import com.sbroussi.dto.test.testA.TestResponseA;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DtoCatalogExtendedTest {

    @Test
    public void testGetDtoRequestBean() {
        final DtoCatalog dtoCatalog = new DtoCatalog();
        final DtoCatalogScanner scanner = new DtoCatalogScanner(dtoCatalog);
        scanner.scanPackages(true, null); // read 'config/soa/dto.packages'

        // load definitions of all DTOs
        final DtoCatalogExtended catalogExtended = new DtoCatalogExtended(dtoCatalog);
        catalogExtended.refresh();

        // bean WITHOUT expected response
        Class requestClazz = dtoCatalog.getRequests().get(MyBean.class.getName());
        assertNotNull(requestClazz);
        DtoRequestBean bean = catalogExtended.getDtoRequestBean(requestClazz);
        assertNotNull(bean);
        assertEquals(MyBean.class.getName(), bean.getClassname());
        assertEquals(MyBean.class, bean.getDtoClass());
        assertEquals("MyBean", bean.getName());
        assertTrue(bean.getExpectedResponses().length == 0);
        assertTrue(bean.getExpectedResponseBeans().isEmpty());

        // bean WITH expected response
        requestClazz = dtoCatalog.getRequests().get(TestRequestA.class.getName());
        assertNotNull(requestClazz);
        bean = catalogExtended.getDtoRequestBean(requestClazz);
        assertNotNull(bean);
        assertEquals(TestRequestA.class.getName(), bean.getClassname());
        assertEquals(TestRequestA.class, bean.getDtoClass());
        assertEquals("TESTREQA", bean.getName());
        assertTrue(bean.getExpectedResponses().length == 1);
        assertTrue(bean.getExpectedResponseBeans().size() == 1);
        DtoResponseBean response = bean.getExpectedResponseBeans().get(0);
        assertNotNull(response);
        assertEquals(TestResponseA.class, response.getDtoClass());
        assertEquals("TESTREPA", response.getName());

        // read comments of the response 'TestResponseA'
        assertEquals(3, response.getDtoComment().notes().length);
        assertEquals(2, response.getDtoComments().size()); // first empty line has be ignored
        assertEquals("TestResponseA: comment line 1", response.getFirstDtoComment());
    }

    @Test(expected = DtoException.class)
    public void testGetDtoRequestBeanInvalid() {

        final DtoCatalog dtoCatalog = new DtoCatalog();
        final DtoCatalogExtended catalogExtended = new DtoCatalogExtended(dtoCatalog);

        // DtoException: Request class [java.lang.String] has no annotation '@DtoRequest'
        catalogExtended.getDtoRequestBean(String.class);
    }

    @Test(expected = DtoException.class)
    public void testGetDtoResponseBeanInvalid() {

        final DtoCatalog dtoCatalog = new DtoCatalog();
        final DtoCatalogExtended catalogExtended = new DtoCatalogExtended(dtoCatalog);

        // DtoException: Response class [java.lang.String] has no annotation '@DtoResponse'
        catalogExtended.getDtoResponseBean(String.class);
    }


    public void testGetRequestsByApplicationId() {
        final DtoCatalog dtoCatalog = new DtoCatalog();
        final DtoCatalogScanner scanner = new DtoCatalogScanner(dtoCatalog);
        scanner.scanPackages(true, null); // read 'config/soa/dto.packages'

        // load definitions of all DTOs
        final DtoCatalogExtended catalogExtended = new DtoCatalogExtended(dtoCatalog);
        catalogExtended.refresh();

        // 2 requests define: usedByApplications = {"app-test-A"}
        List<Class<?>> apps = catalogExtended.getRequestsByApplicationId().get("app-test-A");
        assertNotNull(apps);
        assertEquals(2, apps.size());
        assertTrue(apps.contains(MyBean.class));
        assertTrue(apps.contains(MyBeanWithResponse.class));


        // 1 request 'MyBeanWithResponse' defines: usedByApplications = {"app-test-B"}
        apps = catalogExtended.getRequestsByApplicationId().get("app-test-B");
        assertNotNull(apps);
        assertEquals(1, apps.size());
        assertTrue(apps.contains(MyBeanWithResponse.class));

        // no request defines: usedByApplications = {"app-test-C"}
        apps = catalogExtended.getRequestsByApplicationId().get("app-test-C");
        assertNotNull(apps);
        assertEquals(0, apps.size());

    }
}
