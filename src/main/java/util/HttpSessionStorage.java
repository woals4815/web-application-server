package util;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionStorage {
    private static Map<String, HttpSession> sessionMap = new HashMap<String, HttpSession>();

    public static void add(HttpSession session) {
        HttpSessionStorage.sessionMap.put(session.getId(), session);
    }

    public static HttpSession get(String id) {
        return HttpSessionStorage.sessionMap.get(id);
    }

    public static void remove(String sessionId) {
        HttpSessionStorage.sessionMap.remove(sessionId);
    }
}
