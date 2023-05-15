package org.jhta.controller;

import com.google.gson.Gson;
import io.vertx.core.Vertx;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.jhta.domain.LoginUser;
import org.jhta.domain.Person;
import org.jhta.domain.Student;
import org.jhta.domain.Teacher;
import org.jhta.service.UserService;

public class UserController {
    private final UserService userService;
    private final Gson gson;
    private LoginUser loginUser;
//    public LoginUser getLoginUser() { return loginUser; }

    public UserController(UserService userService) {
        this.userService = userService;
        gson = new Gson();
    }

    public void registerRoutes(Vertx vertx, Router router) {

        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(StaticHandler.create());

        router.route(HttpMethod.GET, "/page/register/student").handler(routingContext -> {
            routingContext.response()
                    .putHeader("Content-Type", "text/html")
                    .sendFile("public/register/student.html");
        });

        router.route(HttpMethod.GET, "/page/register/teacher").handler(routingContext -> {
            routingContext.response()
                    .putHeader("Content-Type", "text/html")
                    .sendFile("public/register/teacher.html");
        });

        router.route(HttpMethod.GET, "/page/login").handler(routingContext -> {
            routingContext.response()
                    .putHeader("Content-Type", "text/html")
                    .sendFile("public/login/login.html");
        });

        router.route(HttpMethod.GET, "/page/dashboard/student").handler(routingContext -> {
            routingContext.response()
                    .putHeader("Content-Type", "text/html")
                    .sendFile("public/dashboard/student.html");
        });

        router.route(HttpMethod.GET, "/page/dashboard/teacher").handler(routingContext -> {
            routingContext.response()
                    .putHeader("Content-Type", "text/html")
                    .sendFile("public/dashboard/teacher.html");
        });

        router.route(HttpMethod.GET, "/login-user").handler(this::handleLoginUser);

        router.route(HttpMethod.POST, "/register/student").handler(this::handleRegisterStudent);
        router.route(HttpMethod.POST, "/register/teacher").handler(this::handleRegisterTeacher);
        router.route(HttpMethod.POST, "/login")
                .handler(BodyHandler.create())
                .handler(this::handleLogin);
    }

    private void handleLoginUser(RoutingContext routingContext) {

        LoginUser sessionUser = routingContext.session().get("loginUser");

        JsonObject json = new JsonObject().put("name", sessionUser.getName());

        routingContext.response()
                .putHeader("Content-Type", "application/json")
                .end(json.encode());
    }

    public void handleLogin(RoutingContext routingContext) {
        JsonObject loginData = routingContext.getBodyAsJson();

        String id = loginData.getString("id");
        String password = loginData.getString("password");

        Person found = userService.processLogin(id, password);
        System.out.println("Login succeeded!");

        this.loginUser = new LoginUser(found.getId(), found.getName(), found.getEmail(), found.getType());

//        EventBus eventBus = routingContext.vertx().eventBus();
//        eventBus.publish("loginUser", this.loginUser);

        routingContext.session().put("loginUser", this.loginUser);
        LoginUser sessionUser = routingContext.session().get("loginUser");

        if ("student".equals(sessionUser.getType())) {
            System.out.println("Directing to dashboard for student..");
            routingContext.response().setStatusCode(201).putHeader("Location", "/page/dashboard/student").end();
        } else if ("teacher".equals(sessionUser.getType())) {
            System.out.println("Directing to dashboard for teacher..");
            routingContext.response().setStatusCode(201).putHeader("Location", "/page/dashboard/teacher").end();
        }
    }

    public void handleRegisterTeacher(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {

            Teacher teacher = gson.fromJson(body.toString(), Teacher.class);
            userService.registerTeacher(teacher);
            System.out.println("Register succeeded!");
            System.out.println("Directing to log in page...");
            routingContext.response().setStatusCode(201).putHeader("Location", "/page/login").end();
        });
    }

    public void handleRegisterStudent(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {

            Student student = gson.fromJson(body.toString(), Student.class);
            userService.registerStudent(student);
            System.out.println("Register succeeded!");
            System.out.println("Directing to log in page...");
            routingContext.response().setStatusCode(201).putHeader("Location", "/page/login").end();
        });
    }
}