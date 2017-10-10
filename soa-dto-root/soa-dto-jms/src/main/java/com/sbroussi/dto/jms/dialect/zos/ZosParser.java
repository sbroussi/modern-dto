package com.sbroussi.dto.jms.dialect.zos;

import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoParser;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.dto.annotations.DtoResponse;
import com.sbroussi.dto.error.INFO;
import com.sbroussi.dto.jms.DtoJmsRequest;
import com.sbroussi.dto.jms.JmsException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ZosParser {

    /**
     * Log the technical 'INFO' response with TRACE level.
     */
    private String INFO_responseName = INFO.class.getAnnotation(DtoResponse.class).name();


    /**
     * Parse the JMS response and build the list of DTO Java objects,
     *
     * @param dtoContext the DTO context
     * @param request    the JMS request
     */
    public void parse(final DtoContext dtoContext, final DtoJmsRequest request) {

        //
        // step 1: decode the z/OS response
        //         split and extract the RAW 'String' parts of the response
        //
        ZosDtoJmsResponse jmsResponseBean = parseRawResponse(dtoContext.getDtoCatalog(), request);


        //
        // step 2: decode the JMS response and build the list of DTO Java objects
        //
        jmsResponseBean.setDtoResponsesMap(decodecodeJmsToDto(dtoContext, request));

    }


    private ZosDtoJmsResponse parseRawResponse(final DtoCatalog dtoCatalog, final DtoJmsRequest request) {

        final DtoRequest annotation = request.getDtoRequestAnnotation();
        final String requestName = (annotation == null) ? null : annotation.name();

        final ZosDtoJmsResponse jmsResponseBean = new ZosDtoJmsResponse();
        jmsResponseBean.setTimestampDecoded(System.currentTimeMillis());
        request.setDtoJmsResponse(jmsResponseBean);

        final String response = request.getRawResponse();


        // test the minimal length of a response
        final int minimalLength = 8 + 7 + 8 + 7 + 4 + 4 + 8 + 12 + 4 + 12;
        final int realResponseLength = (response == null) ? 0 : response.length();
        if (realResponseLength < minimalLength) {
            throw new JmsException("Invalid response message length [" + realResponseLength
                    + "] of request [" + requestName + "] (real length is [" + realResponseLength
                    + "] and minimal length is [" + minimalLength + "])!");
        }


        // Current indices of the response header parsing
        int startIndex = 0;
        int endIndex = 0;

        /*
         * ** Global information *
         */

        // Application ID : X(8)
        endIndex += 8;
        final String channel = response.substring(startIndex, endIndex);
        jmsResponseBean.setChannel(channel.trim());

        startIndex = endIndex;

        // Message length : 9(7)
        endIndex += 7;
        final int responseLength = Integer.valueOf(response.substring(startIndex, endIndex));
        startIndex = endIndex;
        final int realMessageLength = response.length() - startIndex;
        if (realMessageLength != responseLength) {
            String msg = "Invalid response message length [" + responseLength + "] of request [" + requestName + "] (real length is ["
                    + realMessageLength + "])!";
            // read the extra characters at the end of the message
            if (realMessageLength > responseLength) {
                final String extraChars = response.substring(response.length() - (realMessageLength - responseLength));
                msg = msg + " detected [" + extraChars.length() + "] extra characters: [" + extraChars + "]";
            }
            log.error(msg);
        }

        /*
         * ** Header *
         */
        final ZosResponseHeader zosHeader = new ZosResponseHeader();
        jmsResponseBean.setHeader(zosHeader);

        // Header structure name : X(8)
        endIndex += 8;
        final String headerStructureName = response.substring(startIndex, endIndex);
        zosHeader.setName(headerStructureName);
        startIndex = endIndex;

        // Header length : 9(7)
        endIndex += 7;
        final int headerLength = Integer.valueOf(response.substring(startIndex, endIndex));
        zosHeader.setLength(headerLength);
        startIndex = endIndex;

        // Flags : X(4)
        endIndex += 4;
        final String flags = response.substring(startIndex, endIndex);
        zosHeader.setFlags(flags);
        startIndex = endIndex;

        // User Host Index : X(4)
        endIndex += 4;
        final String userHostIndex = response.substring(startIndex, endIndex);
        zosHeader.setUserHostIndex(userHostIndex);
        startIndex = endIndex;

        // User ID : X(8)
        endIndex += 8;
        final String userId = response.substring(startIndex, endIndex);
        zosHeader.setUserId(userId);
        startIndex = endIndex;

        // Profile : X(12)
        endIndex += 12;
        final String userProfile = response.substring(startIndex, endIndex);
        zosHeader.setUserProfile(userProfile);
        startIndex = endIndex;

        // session : X(4)
        endIndex += 4;
        final String session = response.substring(startIndex, endIndex);
        zosHeader.setSessionId(session);
        startIndex = endIndex;

        // Computer name : X(12)
        endIndex += 12;
        final String userComputerName = response.substring(startIndex, endIndex);
        zosHeader.setUserComputerName(userComputerName);
        startIndex = endIndex;

        /*
         * ** Data *
         */
        // Following the header is the data
        // There may be several occurrences of ZosResponseData
        final Map<String, ZosResponseData> zosResponseDatas = new HashMap<String, ZosResponseData>();

        boolean hasMoreData = endIndex < realResponseLength;
        while (hasMoreData) {

            // Response name : X(8)
            endIndex += 8;
            final String responseName = response.substring(startIndex, endIndex).trim();

            // See if there is already a ZosResponseData with the same response name
            ZosResponseData zosResponseData = zosResponseDatas.get(responseName);
            if (null == zosResponseData) {
                zosResponseData = new ZosResponseData();
                zosResponseData.setName(responseName);

                zosResponseDatas.put(responseName, zosResponseData);
            }

            // NOTE Beware : each unique member value of zosResponseData is overwritten
            // if there are more than one zosResponseData with the same name, except length
            // and nbElements

            startIndex = endIndex;

            // Data Response length : 9(7)
            endIndex += 7;
            final int dataResponseLength = Integer.valueOf(response.substring(startIndex, endIndex));
            zosResponseData.setLength(zosResponseData.getLength() + dataResponseLength);
            startIndex = endIndex;

            // Request name : X(8)
            endIndex += 8;
            final String msgRequestName = response.substring(startIndex, endIndex);
            zosResponseData.setRequestName(msgRequestName);
            startIndex = endIndex;

            // Entity : X(20)
            endIndex += 20;
            final String entity = response.substring(startIndex, endIndex);
            zosResponseData.setEntity(entity);
            startIndex = endIndex;

            // Response block : 9(1)
            endIndex += 1;
            final int block = Integer.valueOf(response.substring(startIndex, endIndex));
            zosResponseData.setBlock(block);
            startIndex = endIndex;

            // Number of response elements : 9(5)
            endIndex += 5;
            final int nbElements = Integer.valueOf(response.substring(startIndex, endIndex));
            zosResponseData.setNbElements(zosResponseData.getNbElements() + nbElements);
            startIndex = endIndex;

            // Element length : 9(5)
            endIndex += 5;
            final int elementLength = Integer.valueOf(response.substring(startIndex, endIndex));
            zosResponseData.setLength(elementLength);
            startIndex = endIndex;

            if (log.isDebugEnabled()) {
                log.debug("Read response [" + responseName + "]: " + nbElements + " records of " + elementLength + " chars ("
                        + dataResponseLength + " chars with header)");
            }

            for (int i = 0; i < nbElements; i++) {
                // Element : X(<elementLength>)
                endIndex += elementLength;
                if (response.length() < endIndex) {
                    final String msg = "cannot parse response [" + responseName + "] of request [" + requestName
                            + "] want to read position [" + endIndex + "] but length is [" + response.length() + "]";
                    throw new StringIndexOutOfBoundsException(msg);
                }

                final String responseDataElement = response.substring(startIndex, endIndex);
                zosResponseData.getResponseDataElements().add(responseDataElement);
                if (responseDataElement.length() > 0) {
                    if (responseName.equals(INFO_responseName)) {
                        // log the technical 'INFO' response with TRACE level
                        if (log.isTraceEnabled()) {
                            log.trace("Record [" + responseDataElement + "]");
                        }
                    } else {
                        // dump ALL the response element in DEBUG level (do not truncate)
                        if (log.isDebugEnabled()) {
                            log.debug("Record [" + responseDataElement + "]");
                        }
                    }
                }
                startIndex = endIndex;
            }

            // Is this the end ?
            hasMoreData = endIndex < response.length();
        }

        // Add the datas to the response
        // ('responseDataElement' are merged for all parts having the same 'responseName')

        for (final ZosResponseData zosResponseData : zosResponseDatas.values()) {
            final String responseName = DtoUtils.strNN(zosResponseData.getName()).trim();
            final int nbResponses = zosResponseData.getNbElements();
            if (log.isDebugEnabled()) {
                log.debug("Request [" + requestName + "] returned [" + nbResponses + "] response [" + responseName + "]");
            }
            jmsResponseBean.getZosResponses().add(zosResponseData);
        }


        // check that the responses are all documented as 'expected' in the '@DtoRequest' annotation
        final Class dtoClass = request.getRequestDto().getClass();
        Map<String, Class> expectedResponsesMap = dtoCatalog.getExpectedResponsesByRequest().get(dtoClass);
        if (expectedResponsesMap != null) {
            final Set<String> expectedResponseNames = expectedResponsesMap.keySet();

            // if one response say '*', the request will accept all responses (DEBUGREQ)
            if (!expectedResponseNames.contains("*")) {
                for (final ZosResponseData zosResponseData : jmsResponseBean.getZosResponses()) {
                    final String responseName = zosResponseData.getName();

                    if (!expectedResponseNames.contains(responseName)) {
                        // response not expected for this request
                        throw new JmsException("request [" + requestName + "] received unexpected response ["
                                + responseName + "]; Update the '@DtoRequest' annotation of the DTO ["
                                + request.getRequestDto().getClass().getName()
                                + "] and add 'expectedResponses={...., \"" + responseName + "\"'}");

                    }
                }
            }
        }
        return jmsResponseBean;

    }


    private Map<String, List<Object>> decodecodeJmsToDto(final DtoContext dtoContext, final DtoJmsRequest request) {

        final DtoParser dtoParser = dtoContext.getDtoParser();

        final ZosDtoJmsResponse jmsResponseBean = (ZosDtoJmsResponse) request.getDtoJmsResponse();

        DtoCatalog dtoCatalog = dtoContext.getDtoCatalog();
        // check that the responses are all documented as 'expected' in the '@DtoRequest' annotation
        final Class dtoClass = request.getRequestDto().getClass();
        dtoCatalog.scanDto(dtoClass);

        Map<String, Class> expectedResponsesMap = dtoCatalog.getExpectedResponsesByRequest().get(dtoClass);

//            for (final ZosResponseData zosResponseData : jmsResponseBean.getZosResponses()) {
//                final String responseName = zosResponseData.getName();
//
//                for (final String rawDto : zosResponseData.getResponseDataElements()) {
//
//
//                    Object dto = dtoParser.parse(rawDto);
//                    if (dto != null) {
//
//                    }
//
//
//                }
//
//            }
//        }


        if ((jmsResponseBean == null) || (jmsResponseBean.getZosResponses() == null)) {
            // nothing to return
            return new HashMap<String, List<Object>>(0);
        }

        final int nbResponseNames = jmsResponseBean.getZosResponses().size();

        // LinkedHashMap, the iteration order is, the order in which keys were inserted
        Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>(nbResponseNames);


        /* **
         * Decode data
         * **/
        for (final ZosResponseData zosResponseData : jmsResponseBean.getZosResponses()) {

            final String responseName = zosResponseData.getName();

            // get the class name: 'xsd response name' -> 'java class name'
            final Class<?> clazz = expectedResponsesMap.get(responseName);
            if (clazz == null) {
                log.error("no DTO found with responseName [" + responseName
                        + "], define a new '@DtoResponse' class for this DTO");
                // skip to the next record
                continue;
            }

            final int nbDtos = zosResponseData.getResponseDataElements().size();

            final ArrayList<Object> dtosList = new ArrayList<Object>(nbDtos);
            result.put(responseName, dtosList);

            for (final String dtoAsString : zosResponseData.getResponseDataElements()) {

                final String dtoClassName = clazz.getName();


                final Object dto = dtoParser.parse(clazz, dtoAsString);
                dtosList.add(dto);
            }
        }

        return result;
    }
}
