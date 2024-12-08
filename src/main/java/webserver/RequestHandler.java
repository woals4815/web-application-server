package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;


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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));


            String url = request.getUrl();
            String method = request.getMethod();


            DataOutputStream dos = new DataOutputStream(out);
            int contentLength = 0;
            String cookie = "";
            String accept = "";


            if (url.startsWith("/user/login") && method.equals("POST")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                if (!(checkLoginInfo(HttpRequestUtils.parseQueryString(data)))) {
                    responseResource(out, "/user/login_failed.html", accept);
                    return;
                }
                response302LoginSuccess(dos);
                return;
            }

            if (url.startsWith("/user/list")) {
                if (cookie.equals("")) {
                    responseRedirectLogin(out);
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
                byte[] body = html.toString().getBytes();

                response200Header(newDos, body.length, accept);
                responseBody(newDos, body);
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


    private void responseRedirectLogin(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
        dos.writeBytes("Location: /user/login.html \r\n");
        dos.writeBytes("\r\n");
    }


    private void response302LoginSuccess(
            DataOutputStream dos
    ) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/ \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String accept) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + accept + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean checkLoginInfo(Map<String, String> formValues) {
        String userId = formValues.get("userId");
        String password = formValues.get("password");
        User user = DataBase.findUserById(userId);
        return user != null && user.getPassword().equals(password);
    }

    private void responseResource(
            OutputStream output,
            String url,
            String accept
    ) {
        try {
            log.debug("output stream: {}", url);
            DataOutputStream dos = new DataOutputStream(output);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length, accept);
            responseBody(dos, body);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
