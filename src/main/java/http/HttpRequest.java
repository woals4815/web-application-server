package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
    private Map<String, String> body = new HashMap<String, String>();


    public HttpRequest(InputStream in) {
        try {
            this.in = in;
            this.br = new BufferedReader(new InputStreamReader(this.in, "UTF-8"));

            String line = br.readLine();
            log.debug("line is. {}", line);
            if (line == null) return;

            String[] tokens = line.split(" ");
            this.method = tokens[0];
            this.url = tokens[1];
            setParameter(this.url);

            line = br.readLine();

            while(line != null && !line.isEmpty()) {
                String[] headerKeyValue = line.split(":", 2);
                headers.put(headerKeyValue[0].trim(), headerKeyValue[1].trim());
                line = br.readLine();
            }
            if ((this.getMethod()).equals("POST")) {
                this.setBody();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setBody() throws Exception {
        String contentLength = headers.get("Content-Length");
        String data = IOUtils.readData(br, Integer.parseInt(contentLength));
        body = HttpRequestUtils.parseQueryString(data);
    }

    private void setParameter(String url){
        String[] params = url.split(Pattern.quote("?"));
        if (params.length == 2) {
            this.params = HttpRequestUtils.parseQueryString(params[1]);
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

    public Map<String, String> getBody() {
        return this.body;
    }
}
