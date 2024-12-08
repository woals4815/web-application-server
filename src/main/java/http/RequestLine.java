package http;

import constants.HttpMethod;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestLine {
    private HttpMethod method;
    private String url;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String line) {
        String[] tokens = line.split(" ");
        if (tokens.length < 3) {
            throw new IllegalArgumentException(line + "");
        }
        method = HttpMethod.valueOf(tokens[0]);
        url = tokens[1];
        setParameter(url);
    }

    private void setParameter(String url){
        String[] params = url.split(Pattern.quote("?"));
        if (params.length == 2) {
            this.params = HttpRequestUtils.parseQueryString(params[1]);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }
    public String getUrl() {
        return url;
    }
    public Map<String, String> getParams() {
        return params;
    }
}
