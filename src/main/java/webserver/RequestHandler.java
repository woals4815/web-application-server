package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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
            String protocol = tokens[2];

            log.debug("method: {}, path: {}, protocol: {}", method, path, protocol);

            String resources = path.substring(1);
            DataOutputStream dos = new DataOutputStream(out);

            if (path.contains("/user/create")) {
                String forms = path.split(Pattern.quote("?"))[1];
                Map<String, String> keyValue =  HttpRequestUtils.parseQueryString(forms);
                User newUser = new User(
                        keyValue.get("userId"),
                        keyValue.get("password"),
                        keyValue.get("name"),
                        keyValue.get("email")
                );
                log.debug(newUser.toString());
                response200Header(dos, newUser.toString().length());
                responseBody(dos, newUser.toString().getBytes());
            }

            if (resources.isEmpty()) {
                byte[] files = Files.readAllBytes(new File("./webapp/index.html").toPath());
                response200Header(dos, files.length);
                responseBody(dos, files);
            } else {
                log.debug("resources: {}", resources);
                byte[] files = Files.readAllBytes(new File("./webapp/" + resources).toPath());
                response200Header(dos, files.length);
                responseBody(dos, files);
            }

            log.debug("request line {}", line);
            while (line != null && !line.equals("")) {
                line = bufferedReader.readLine();
                log.debug("header {}", line);
            }



        } catch (IOException e) {
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
