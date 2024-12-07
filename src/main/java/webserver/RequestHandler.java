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
            String protocol = tokens[2];

            log.debug("method: {}, path: {}, protocol: {}", method, path, protocol);

            String resources = path.substring(1);
            DataOutputStream dos = new DataOutputStream(out);

            log.debug("request line {}", line);

            String contentLength = "";

            while (line != null && !line.equals("")) {
                line = bufferedReader.readLine();
                String[] headerKeyValue = line.split(":");
                if (headerKeyValue[0].equals("Content-Length")) {
                    contentLength = headerKeyValue[1].trim();
                }
                log.debug("header {}", line);
            }


            if (path.startsWith("/user/create")) {
                String data = IOUtils.readData(bufferedReader, Integer.parseInt( contentLength ));
                Map<String, String> keyValue = HttpRequestUtils.parseQueryString(data);

                User newUser = new User(
                        keyValue.get("userId"),
                        keyValue.get("password"),
                        keyValue.get("name"),
                        keyValue.get("email")
                );

                byte[] files = Files.readAllBytes(new File("./webapp/index.html").toPath());
                resposne302Header(dos, files.length);
                responseBody(dos, files);
            } else {
                byte[] files = Files.readAllBytes(new File("./webapp/" + resources).toPath());
                response200Header(dos, files.length);
                responseBody(dos, files);
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

    private void resposne302Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Location: /index.html\r\n");
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
