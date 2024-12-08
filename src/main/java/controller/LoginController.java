package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.Map;

public class LoginController extends AbstractController {
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        if (!(checkLoginInfo(request.getBody()))) {
            response.forward("/user/login_failed.html");
            return;
        }
        response.addHeader("Set-Cookie", "logined=true; Path=/");
        response.sendRedirect("/index.html");
    }
    private boolean checkLoginInfo(Map<String, String> formValues) {
        String userId = formValues.get("userId");
        String password = formValues.get("password");
        User user = DataBase.findUserById(userId);
        return user != null && user.getPassword().equals(password);
    }
}
