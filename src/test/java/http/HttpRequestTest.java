package http;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpRequestTest {

    private String testFilePath = "src/test/resources";

    private HttpRequest request;

    @Before
    public void setUp() throws Exception {
        InputStream in = new FileInputStream(new File(testFilePath + "/Http_GET.txt"));
        this.request = new HttpRequest(in);
    }

    @Test
    public void testShowLog() {
        request.showLog();
    }

    @Test
    public void testGetMethod(){
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void testGetUrl(){
        assertEquals("/user/create?userId=javajigi&password=passwrod&name=jaemin", request.getUrl());
    }

    @Test
    public void testGetheader(){
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("HOST", "localhost:8080");
        expectedHeaders.put("Connection", "keep-alive");
        expectedHeaders.put("Accept", "*/*");
        assertEquals(expectedHeaders, request.getHeaders());
    }

    @Test
    public void testGetHeaderAccept() {
        assertEquals("localhost:8080", request.getHeaders().get("HOST"));
    }

}