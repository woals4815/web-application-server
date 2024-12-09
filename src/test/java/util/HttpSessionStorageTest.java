package util;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpSessionStorageTest {

    private static final Logger log = LoggerFactory.getLogger(HttpSessionStorageTest.class);
    private HttpSession session1 = new HttpSession("1");
    private HttpSession session2 = new HttpSession("2");

    @Before
    public void setUp() throws Exception {
        HttpSessionStorage.add(session1);
        HttpSessionStorage.add(session2);
    }

    @Test
    public void add() {
        HttpSession session3 = new HttpSession("3");
        HttpSessionStorage.add(session3);
        assertEquals(session3, HttpSessionStorage.get(session3.getId()));
    }

    @Test
    public void get() {
        assertEquals(session1, HttpSessionStorage.get(session1.getId()));
    }

    @Test
    public void remove() {
        /**
         * HttpSessionStorage get은 없으면 다시 생성해서 넣어버리는데 어떻게 remove를 테스트하지?
         * 1. 직접 sessionMap을 검증
         * 클래스의 sessionMap에 직접 접근해서 확인. -> 패키지 프라이빗 접근 수준의 메서드나 accessor 추가, reflection을 사용해서 처리
         * 2. mocking
         * get method를 모킹 해서 실제 동작대신 null을 반환하도록 하게 만든다.->근데 그렇게 하면 의미가 있남?
         */
        HttpSessionStorage.remove(session1.getId());
        Map<String, HttpSession> sessionMap = getSessionMapViaReflection();
        assertFalse(sessionMap.containsKey("1"));
    }

    private Map<String, HttpSession> getSessionMapViaReflection() {
        try {
            Field field = HttpSessionStorage.class.getDeclaredField("sessionMap");
            field.setAccessible(true);
            return (Map<String, HttpSession>) field.get(null); // Static 필드 접근
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}