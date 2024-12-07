package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import javax.xml.crypto.Data;

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = bufferedReader.readLine();
            String[] tokens = line.split(" ");
            String method = tokens[0];
            String path = tokens[1];

            DataOutputStream dos = new DataOutputStream(out);
            int contentLength = 0;
            String cookie = "";
            log.debug("request line: {}", line);


            while (!isLineBlank(line)) {
                line = bufferedReader.readLine();
                if (hasContentLength(line)) {
                    contentLength = this.getContentLength(line);
                }
                if (hasCookie(line)) {
                    cookie = getCookie(line);
                }
            }

            if (path.startsWith("/user/login") && method.equals("POST")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                if (!(checkLoginInfo(HttpRequestUtils.parseQueryString(data)))) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }
                response302LoginSuccess(dos);
            }

            if (path.startsWith("/user/list")) {
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

                response200Header(newDos, body.length);
                responseBody(newDos, body);
                return;
            }


            if (path.startsWith("/user/create") && method.equals("POST")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                Map<String, String> keyValue = HttpRequestUtils.parseQueryString(data);

                User newUser = new User(
                        keyValue.get("userId"),
                        keyValue.get("password"),
                        keyValue.get("name"),
                        keyValue.get("email")
                );
                DataBase.addUser(newUser);
                resposne302Header(dos, "/index.html");
            } else {
                log.debug("-----------------last--------------------- path is {}", path);
                responseResource(out, path);
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
    private boolean hasContentLength( String line) {
        String[] headerKeyValue = line.split(":");
        return headerKeyValue[0].equals("Content-Length");
    }
    private boolean hasCookie(String line) {
        String[] headerKeyValue = line.split(":");
        return headerKeyValue[0].equals("Cookie");
    }
    private boolean isLineBlank(String line) {
        return line == null || line.equals("");
    }

    private String getCookie(String line) {
        String[] tokens = line.split(":");
        return tokens[1].trim();
    }

    private int getContentLength(String line){
        String[] tokens = line.split(":");
        return Integer.parseInt(tokens[1].trim());
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

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLoginResult(OutputStream out, boolean loginSuccess) {
        try {
            DataOutputStream dos = new DataOutputStream(out);
            if(loginSuccess) {
                dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
                dos.writeBytes("Set-Cookie: logined=true \r\n");
                dos.writeBytes("Location: /index.html \r\n");
                dos.writeBytes("\r\n");
            } else {
                responseResource(dos, "/user/login_failed.html");
            }
        }catch (Exception e) {
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
            String url
    ) {
        try {
            log.debug("output stream: {}", url);
            DataOutputStream dos = new DataOutputStream(output);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    private void resposne302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        }catch (Exception e) {
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
