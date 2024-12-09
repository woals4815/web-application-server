package util;

import java.util.Map;

public class HttpCookie {

    private Map<String, String> cookies;

    public HttpCookie(String value) {
        this.cookies = HttpRequestUtils.parseCookies(value);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
