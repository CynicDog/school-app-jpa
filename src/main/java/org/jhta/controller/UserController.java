package org.jhta.controller;

import com.google.gson.Gson;
import io.vertx.core.Vertx;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import org.jhta.domain.Student;
import org.jhta.domain.Teacher;
import org.jhta.service.UserService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserController {
    private final UserService userService;
    private final Gson gson;

    public UserController() {
        EntityManagerFactory emf =Persistence.createEntityManagerFactory("school-app-jpa");
        userService = new UserService(emf);
        gson = new Gson();
    }

    public void registerRoutes(Vertx vertx, Router router) {

        router.route().handler(StaticHandler.create());

        router.route(HttpMethod.GET, "/page/register/student").handler(routingContext -> {
            routingContext.response()
                    .putHeader("content-type", "text/html")
                    .sendFile("public/register/student.html");
        });

        router.route(HttpMethod.POST, "/register/student").handler(this::handleRegisterStudent);
        router.route(HttpMethod.POST, "/register/teacher").handler(this::handleRegisterTeacher);
        router.route(HttpMethod.POST, "/login").handler(this::handleLogin);
    }

    public void handleLogin(RoutingContext routingContext) {
    }

    public void handleRegisterTeacher(RoutingContext routingContext) {
    }

    public void handleRegisterStudent(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {

            Student student = gson.fromJson(body.toString(), Student.class);
            userService.registerStudent(student);

            routingContext.response().setStatusCode(201).end();
        });
    }
}