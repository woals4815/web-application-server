package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private InputStream in;
    private BufferedReader br;
    private String method;
    private String url;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> params = new HashMap<String, String>();


    public HttpRequest(InputStream in) {
        try {
            this.in = in;
            this.br = new BufferedReader(new InputStreamReader(this.in));

            String line = br.readLine();
            String[] tokens = line.split(" ");
            this.method = tokens[0];
            this.url = tokens[1];
            setParameter(this.url);
            this.setHeader();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setHeader(){
        String line;
        try {
            while((line = br.readLine()) != null && !line.isEmpty()) {
                if (line == null) return;
                String[] headerKeyValue = line.split(":", 2);
                headers.put(headerKeyValue[0], headerKeyValue[1].trim());
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    private void setParameter(String url){
        String params = url.split(Pattern.quote("?"))[1];
        this.params = HttpRequestUtils.parseQueryString(params);
    }

    public void showLog(){
        try {
            String line = br.readLine();
            log.debug("line {}", line);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    public String getMethod() {
        return this.method;
    }
    public String getUrl() {
        return this.url;
    }
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getParams() {
        return this.params;
    }
}
