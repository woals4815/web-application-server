package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.io.DataOutputStream;
import java.util.Collection;

public class UserListController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String cookie = request.getHeaders().get("Cookie");
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
        response.forwardBody(html.toString());
        return;

    }
}
