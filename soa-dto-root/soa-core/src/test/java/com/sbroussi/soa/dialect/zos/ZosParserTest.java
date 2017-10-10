package com.sbroussi.soa.dialect.zos;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoException;
import com.sbroussi.soa.SoaDtoRequest;
import com.sbroussi.soa.SoaDtoResponse;
import com.sbroussi.soa.test.TestRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Slf4j
public class ZosParserTest {

    @Test
    public void testParseResponse() throws Exception {

        ZosParser zosParser = new ZosParser();


        // build a valid raw response and parse it

        final StringBuilder response = new StringBuilder();
        /*
         * ** Message Header *  8 + 7 = 15
         */
        // Application ID : X(8)
        response.append("@WEB    ");
        // Message length : 9(7)
        response.append("0000143");

        /*
         * ** Header *  8 + 7 + 4 + 4 + 8 + 12 + 4 + 12 = 59
         */
        // Header structure name : X(8)
        response.append("HEADER12");
        // Header length : 9(7)
        response.append("0000044");
        // Flags : X(4)
        response.append("flag");
        // User Host Index : X(4)
        response.append("host");
        // User ID : X(8)
        response.append("userId12");
        // Profile : X(12)
        response.append("userProfile1");
        // session : X(4)
        response.append("fsid");
        // Computer name : X(12)
        response.append("myLaptop    ");

        /*
         * ** Data * 8 + 7 + 8 + 20 + 1 + 5 + 5 + 15 + 15 = 84      59 + 84 : 143    143 + 15 = 158
         */
        // Response name : X(8)
        response.append("RESPONSE");
        // Data Response length : 9(7)
        response.append("0004401");
        // Request name : X(8)
        response.append("request1");
        // Entity : X(20)
        response.append("entity12345678901234");
        // Response block : 9(1)
        response.append("9");
        // Number of response elements : 9(5)
        response.append("00002");
        // Element length : 9(5)
        response.append("00015");

        // Element 1: X(<elementLength>
        response.append("Hello World    ");
        // Element 2: X(<elementLength>
        response.append("Have a nice day");

        final String responseStr = response.toString();

        log.info("expect this response: [" + responseStr + "]");

        // set the RAW response message to be parsed
        SoaDtoRequest jmsRequest = new SoaDtoRequest(new TestRequest());
        jmsRequest.setRawResponse(responseStr);

        // decode
        zosParser.parse(new DtoContext(), jmsRequest);

        final SoaDtoResponse soaDtoResponse = jmsRequest.getSoaDtoResponse();
        assertNotNull(soaDtoResponse);

        assertTrue(soaDtoResponse instanceof ZosSoaDtoResponse);
        ZosSoaDtoResponse zosSoaDtoResponse = (ZosSoaDtoResponse) soaDtoResponse;


        assertNotNull(zosSoaDtoResponse.getTimestampDecoded());
        assertEquals("@WEB", zosSoaDtoResponse.getChannel());

        final ZosResponseHeader zosHeader = zosSoaDtoResponse.getHeader();
        assertEquals("HEADER12", zosHeader.getName());
        assertEquals("flag", zosHeader.getFlags());
        assertEquals(44, zosHeader.getLength());
        assertEquals("host", zosHeader.getUserHostIndex());
        assertEquals("userId12", zosHeader.getUserId());
        assertEquals("userProfile1", zosHeader.getUserProfile());
        assertEquals("fsid", zosHeader.getSessionId());
        assertEquals("myLaptop    ", zosHeader.getUserComputerName());

        final List<ZosResponseData> zosResponseDataList = zosSoaDtoResponse.getZosResponses();
        assertEquals(1, zosResponseDataList.size());

        final ZosResponseData data = zosResponseDataList.get(0);
        assertEquals("RESPONSE", data.getName());
        assertEquals(2, data.getNbElements());
        assertEquals("request1", data.getRequestName());
        assertEquals("entity12345678901234", data.getEntity());
        assertEquals(9, data.getBlock());
        assertEquals(2, data.getNbElements());
        assertEquals("request1", data.getRequestName());
        assertEquals(15, data.getLength());
        final List<String> elements = data.getResponseDataElements();
        assertEquals(2, elements.size());
        assertEquals("Hello World    ", elements.get(0));
        assertEquals("Have a nice day", elements.get(1));


        // cannot decode a 'null' response
        try {
            // set the RAW response message
            jmsRequest.setRawResponse(null);
            zosParser.parse(new DtoContext(), jmsRequest);
            fail("should have throw an exception");
        } catch (final DtoException expectedOK) {
        }

        // cannot decode a response with a length < 74 characters
        try {
            // set the RAW response message
            jmsRequest.setRawResponse("@FORMINGthis is a short response");
            zosParser.parse(new DtoContext(), jmsRequest);
            fail("should have throw an exception");
        } catch (final DtoException expectedOK) {
        }

    }

}
