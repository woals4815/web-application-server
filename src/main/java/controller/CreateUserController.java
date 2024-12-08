package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.Map;

public class CreateUserController extends AbstractController {
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> keyValue = request.getBody();
        User newUser = new User(
                keyValue.get("userId"),
                keyValue.get("password"),
                keyValue.get("name"),
                keyValue.get("email")
        );
        DataBase.addUser(newUser);
        response.sendRedirect("/index.html");
    }
}
