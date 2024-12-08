package http;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestLineTest {

    private RequestLine requestLine;
    @Before
    public void setUp() throws Exception {
        requestLine = new RequestLine("GET /index.html HTTP/1.1");
    }

    @Test
    public void getMethod() {
        assertEquals("GET", requestLine.getMethod());
    }

    @Test
    public void getUrl() {
        assertEquals("/index.html", requestLine.getUrl());
    }

    @Test
    public void getParams() {
        RequestLine requestLine = new RequestLine("GET /user/create?username=hello HTTP/1.1");
        Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("username", "hello");
        assertEquals(expectedParams, requestLine.getParams());
    }
}