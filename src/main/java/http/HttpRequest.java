package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private InputStream in;
    private BufferedReader br;
    public HttpRequest(InputStream in) {
        this.in = in;
        this.br = new BufferedReader(new InputStreamReader(in));
    }

    public void showLog(){
        try {
            String line = br.readLine();
            log.debug("line {}", line);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}
