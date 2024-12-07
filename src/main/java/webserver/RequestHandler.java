package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import db.DataBase;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = bufferedReader.readLine();
            String[] tokens = line.split(" ");
            String method = tokens[0];
            String path = tokens[1];

            String resources = path.substring(1);
            DataOutputStream dos = new DataOutputStream(out);

            int contentLength = 0;

            while (!isLineBlank(line)) {
                line = bufferedReader.readLine();
                if (hasContentLength(line)) {
                    contentLength = this.getContentLength(line);
                }
                log.debug("header {}", line);
            }

            if (path.startsWith("/user/login") && method.equals("POST")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                responseLoginResult(dos, checkLoginInfo( HttpRequestUtils.parseQueryString(data) ));
            }


            if (path.startsWith("/user/create")) {
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
                byte[] files = Files.readAllBytes(new File("./webapp/" + resources).toPath());
                response200Header(dos, files.length);
                responseBody(dos, files);
            }



        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private boolean hasContentLength( String line) {
        String[] headerKeyValue = line.split(":");
        return headerKeyValue[0].equals("Content-Length");
    }
    private boolean isLineBlank(String line) {
        return line == null || line.equals("");
    }

    private int getContentLength(String line){
        String[] tokens = line.split(":");
        return Integer.parseInt(tokens[1].trim());
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

    private void responseLoginResult(DataOutputStream dos, boolean loginSuccess) {
        try {
            if(loginSuccess) {
                dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
                dos.writeBytes("Location: /index.html \r\n");
                dos.writeBytes("Set-Cookie: " + "logined=true \r\n");
                dos.writeBytes("\r\n");
            } else {
                byte[] body =Files.readAllBytes(new File("./webapp/user/login_failed.html").toPath());
                dos.writeBytes("Set-Cookie: " + "logined=false\r\n");
                response200Header(dos, body.length);
                responseBody(dos, body);
                dos.writeBytes("\r\n");
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
