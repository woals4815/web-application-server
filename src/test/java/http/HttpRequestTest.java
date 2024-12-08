package http;

import constants.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpRequestTest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestTest.class);
    private String testFilePath = "src/test/resources";

    private HttpRequest request;


    @Test
    public void testGetMethod() throws IOException {
        HttpRequest request1 = new HttpRequest(getInputStream("/Http_GET.txt"));
        assertEquals(HttpMethod.GET, request1.getMethod());
    }

    @Test
    public void testGetUrl() throws IOException {
        HttpRequest request1 = new HttpRequest(getInputStream("/Http_GET.txt"));
        assertEquals("/user/create?userId=javajigi&password=password&name=jaemin", request1.getUrl());
    }

    @Test
    public void testGetheader() throws IOException {
        HttpRequest request1 = new HttpRequest(getInputStream("/Http_GET.txt"));
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("HOST", "localhost:8080");
        expectedHeaders.put("Connection", "keep-alive");
        expectedHeaders.put("Accept", "*/*");
        assertEquals(expectedHeaders, request1.getHeaders());
    }

    @Test
    public void testGetHeaderAccept() throws IOException {
        HttpRequest request1 = new HttpRequest(getInputStream("/Http_GET.txt"));
        assertEquals("localhost:8080", request1.getHeaders().get("HOST"));
    }

    @Test
    public void testGetParamter() throws IOException {
        HttpRequest request1 = new HttpRequest(getInputStream("/Http_GET.txt"));
        Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("userId", "javajigi");
        expectedParams.put("password", "password");
        expectedParams.put("name", "jaemin");
        assertEquals(expectedParams, request1.getParams());
    }

    @Test
    public void testGetParamterUserId() throws IOException {
        HttpRequest request1 = new HttpRequest(getInputStream("/Http_GET.txt"));
        assertEquals("javajigi", request1.getParams().get("userId"));
    }

    @Test
    public void testPOSTBody() throws IOException {
        HttpRequest request1 = new HttpRequest(getInputStream("/Http_POST.txt"));
        Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("userId", "minseok");
        expectedParams.put("password", "password");
        expectedParams.put("name", "minseok");
        Map<String,String> body = request1.getBody();
        assertEquals(expectedParams, body);
    }

    private InputStream getInputStream(String path) throws IOException {
        log.debug("file path. {}", path);
        return new FileInputStream(new File(testFilePath + path));
    }
}