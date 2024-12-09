package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import constants.HttpMethod;
import controller.*;
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
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String url = request.getUrl();

            Controller controller= RequestMapping.getController(url);

            if (request.getCookie().getCookies().get("JSESSIONID") == null) {
                response.addHeader("Set-Cookie", String.format("JSESSIONID=%s; Path=/", UUID.randomUUID()));
            }

            if (controller != null) {
                controller.service(request, response);
            } else {
                Controller forwardController = new ForwardController();
                forwardController.service(request, response);
            }
        } catch (IOException e) {
            log.debug("error");
            log.error(e.getMessage());
        }
    }
}
