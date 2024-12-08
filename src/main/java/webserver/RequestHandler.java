package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request =  new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String url = request.getUrl();
            String method = request.getMethod();

            String cookie = request.getHeaders().get("Cookie");

            if (url.startsWith("/user/login") && method.equals("POST")) {
                if (!(checkLoginInfo(request.getBody()))) {
                    response.forward("/user/login_failed.html");
                    return;
                }
                response.addHeader("Set-Cookie", "logined=true; Path=/");
                response.sendRedirect("/index.html");
                return;
            }

            if (url.startsWith("/user/list")) {
                if (cookie.equals("")) {
                    response.sendRedirect("/user/login.html");
                    return;
                }

                Collection<User> users = DataBase.findAll();
                StringBuilder html = new StringBuilder();

                html.append("<table table-hover>");
                for(User user : users) {
                    html.append("<tr>");
                    html.append("<td>").append(user.getUserId()).append("</td>");
                    html.append("<td>").append(user.getName()).append("</td>");
                    html.append("<td>").append(user.getEmail()).append("</td>");
                    html.append("</tr>");
                }
                html.append("</table>");

                DataOutputStream newDos = new DataOutputStream(out);
                response.forwardBody(html.toString());
                return;
            }


            if (url.startsWith("/user/create") && method.equals("POST")) {
                Map<String, String> keyValue = request.getBody();
                User newUser = new User(
                        keyValue.get("userId"),
                        keyValue.get("password"),
                        keyValue.get("name"),
                        keyValue.get("email")
                );
                DataBase.addUser(newUser);
                response.sendRedirect("/index.html");
            } else {
                response.forward(url);
            }
        } catch (IOException e) {
            log.debug("error");
            log.error(e.getMessage());
        }
    }

    private boolean checkLoginInfo(Map<String, String> formValues) {
        String userId = formValues.get("userId");
        String password = formValues.get("password");
        User user = DataBase.findUserById(userId);
        return user != null && user.getPassword().equals(password);
    }

}
