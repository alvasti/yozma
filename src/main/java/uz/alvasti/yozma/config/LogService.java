package uz.alvasti.yozma.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class LogService {


    private static final Logger logger = LogManager.getLogger(LogService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();


    public void logComingRequestAndFillThreadContext(MultiReadHttpServletRequest request) {

        Boolean used = false;
        try {

            Map<String, String> forThreadContextMap = new HashMap<>();

            Map<String, String> header = new HashMap<>();
            StringMapMessage message = new StringMapMessage();

            Iterator<String> iterator = request.getHeaderNames().asIterator();
            while (iterator.hasNext()) {
                String element = iterator.next();
                header.put(element, request.getHeader(element));
            }

            String logId = "";
            if (header.containsKey("x-correlation-id")) {
                logId = header.get("x-correlation-id");
                forThreadContextMap.put("x-correlation-id", logId);
            }

//            forThreadContextMap.put("USERNAME", UserHolder.phone() );
//            forThreadContextMap.put("USERID", UserHolder.userId() );
//            forThreadContextMap.put("DEVICE-ID", UserHolder.getRequestInfoThreadLocal().getDeviceId());

//            message.put("USERNAME", UserHolder.phone() + ":" + UserHolder.userId());
            message.put("REQUEST", "REQUEST");
            message.put("LOGID", logId);
            message.put("METHOD", request.getMethod());
            message.put("URI", request.getRequestURI());
//            message.put("INFO", UserHolder.getRequestInfoThreadLocal().toString());

            message.put("HEADERS", header.toString());
            message.put("BODY", getRequestText(request));

            ThreadContext.putAll(forThreadContextMap);
            logger.info(message);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void logOutgoingRequestAndFillThreadContext(MultiReadHttpServletRequest request, ContentCachingResponseWrapper response) {

        try {


            Map<String, String> header = new HashMap<>();
            StringMapMessage message = new StringMapMessage();

            for (String element : response.getHeaderNames()) {
                header.put(element, request.getHeader(element));
            }

            String logId = "";

            if (header.containsKey("x-correlation-id")) {
                logId = header.get("x-correlation-id");
            }

//            message.put("USERNAME", UserHolder.phone() + ":" + UserHolder.userId());
            message.put("REQUEST", "RESPONSE");
            message.put("LOGID", logId);
            message.put("METHOD", request.getMethod());
            message.put("URI", request.getRequestURI());
//            message.put("INFO", UserHolder.getRequestInfoThreadLocal().toString());
            message.put("BODY", objectMapper.readValue(getResponseText(response), ObjectNode.class).toString());

            logger.info(message);

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String getResponseText(ContentCachingResponseWrapper response) {

        ObjectNode node = objectMapper.createObjectNode();

        node.put("status", response.getStatus());
        if (response.getContentAsByteArray().length > 0) {
            ObjectReader reader = objectMapper.reader();
            try {
                node.set("body", reader.readTree(new ByteArrayInputStream(response.getContentAsByteArray())));
            } catch (IOException e) {
                String body = new String(response.getContentAsByteArray());
                node.put("body", body.trim()
                        .replaceAll(" +", " ")
                        .replaceAll("\n", ""));
            }
        }

        return node.toString();
    }

    private String getRequestText(MultiReadHttpServletRequest request) throws IOException {
        return IOUtils.toString(request.getInputStream());
    }

}
