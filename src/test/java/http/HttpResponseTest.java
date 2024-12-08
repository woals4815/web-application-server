package http;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpResponseTest {


    private String filePath = "src/test/resources/";
    private HttpResponse response;

    @Test
    public void testFoward() throws Exception {
         this.response = new HttpResponse(
                this.createOutputStream("Http_forward.txt")
        );
        response.forward("/index.html");
    }
    @Test
    public void testRedirect() throws Exception {
        this.response = new HttpResponse(
                this.createOutputStream("Http_redirect.txt")
        );
        response.sendRedirect("/index.html");
    }
    @Test
    public void testCookie() throws Exception {
        this.response = new HttpResponse(
                this.createOutputStream("Http_Cookie.txt")
        );
        response.addHeader("Set-Cookie", "logined=true");
        response.sendRedirect("/index.html");
    }

    private OutputStream createOutputStream(String path) throws FileNotFoundException {
        return new FileOutputStream(new File(filePath + path));
    }
}