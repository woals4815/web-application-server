package util;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionStorage {
    private static Map<String, HttpSession> sessionMap = new HashMap<String, HttpSession>();

    public static void add(HttpSession session) {
        HttpSessionStorage.sessionMap.put(session.getId(), session);
    }

    public static HttpSession get(String id) {
        HttpSession session =  HttpSessionStorage.sessionMap.get(id);
        if (session == null) {
            HttpSession newSession = new HttpSession(id);
            return newSession;
        }
        return session;
    }

    public static void remove(String sessionId) {
        HttpSessionStorage.sessionMap.remove(sessionId);
    }
}
