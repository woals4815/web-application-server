package util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpSessionStorageTest {

    private HttpSession session1 = new HttpSession();
    private HttpSession session2 = new HttpSession();

    @Before
    public void setUp() throws Exception {
        HttpSessionStorage.add(session1);
        HttpSessionStorage.add(session2);
    }

    @Test
    public void add() {
        HttpSession session3 = new HttpSession();
        HttpSessionStorage.add(session3);
        assertEquals(session3, HttpSessionStorage.get(session3.getId()));
    }

    @Test
    public void get() {
        assertEquals(session1, HttpSessionStorage.get(session1.getId()));
    }

    @Test
    public void remove() {
        HttpSessionStorage.remove(session1.getId());
        assertNull(HttpSessionStorage.get(session1.getId()));
    }
}