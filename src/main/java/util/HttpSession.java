package util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {
    private String sessionId;
    private Map<String, Object> attributes = new HashMap<String, Object>();

    public HttpSession() {
        this.sessionId = UUID.randomUUID().toString();
    }

    public String getId(){
        return sessionId;
    }

    public Map<String, Object> getAttributes(){
        return attributes;
    }

    public void setAttribute(String name, Object value){
        attributes.put(name, value);
    }

    public void removeAttribute(String name){
        attributes.remove(name);
    }

    public void invalidate(){
        attributes.clear();
    }
}
