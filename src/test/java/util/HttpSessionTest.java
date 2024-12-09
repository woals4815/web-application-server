package util;

import model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpSessionTest {

    private HttpSession session;
    private User user = new User(
            "userId",
            "password",
            "name",
            "email"
    );
    @Before
    public void setUp() throws Exception {
        session = new HttpSession();
        session.setAttribute("user", user);
    }

    @Test
    public void getId() {
        assertTrue(String.class.isInstance(session.getId()));
    }

    @Test
    public void getAttributes() {
        assertEquals(user, session.getAttributes().get("user"));
    }

    @Test
    public void setAttribute() {
        session.setAttribute("hi", "hello");
        assertEquals("hello", session.getAttributes().get("hi"));
    }

    @Test
    public void removeAttribute() {
        session.setAttribute("hi", "hello");
        assertEquals("hello", session.getAttributes().get("hi"));
        session.removeAttribute("hi");
        assertNull(session.getAttributes().get("hi"));
    }

    @Test
    public void invalidate() {
        session.invalidate();
        Map<String, Object> expected = new HashMap<String, Object>();
        assertEquals(expected, session.getAttributes());
    }
}