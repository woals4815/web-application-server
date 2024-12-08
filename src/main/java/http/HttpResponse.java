package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private OutputStream out;
    private DataOutputStream dos;

    private Map<String, String> header = new HashMap<String, String>();

    public HttpResponse(
            OutputStream out
    ) {
        this.out = out;
        this.dos = new DataOutputStream(out);
    }

    public void forward(String url) {
        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + url ).toPath());

            if (url.contains("css")) {
                this.addHeader("Content-Type", "text/css");
            } else if (url.contains("js")) {
                this.header.put("Content-Type", "text/javascript");
            } else {
                this.header.put("Content-Type", "text/html;charset=utf-8");
            }
            header.put("Content-Length", String.valueOf(body.length));
            response200Header();
            responseBody(body);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String url) {
        try {
            header.put("Location", url);
            resposneRedirectHeader();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void writeHeader() throws IOException {
        Set<String> headerKeys = header.keySet();
        for(String headerKey : headerKeys) {
            dos.writeBytes(headerKey + ": " + header.get(headerKey) + "\r\n");
        }
    }

    private void response200Header() throws IOException {
        this.dos.writeBytes("HTTP/1.1 200 OK\r\n");
        writeHeader();
        this.dos.writeBytes("\r\n");
    }

    private void resposneRedirectHeader() throws  IOException {
        this.dos.writeBytes("HTTP/1.1 302 Redirect\r\n");
        writeHeader();
        dos.writeBytes("\r\n");
    }

    private void responseBody(byte[] body)  {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }

    public Map<String, String> getHeaders(){
        return this.header;
    }
}
