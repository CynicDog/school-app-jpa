package org.jhta.controller;

import com.google.gson.Gson;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.jhta.domain.Course;
import org.jhta.domain.LoginUser;
import org.jhta.domain.Registration;
import org.jhta.domain.Student;
import org.jhta.service.CourseService;
import org.jhta.service.UserService;
import org.jhta.subselect.RegisteredCourse;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final Gson gson;
    private LoginUser loginUser;

    public CourseController(Vertx vertx) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("school-app-jpa");
        courseService = new CourseService(emf);
        userService = new UserService(emf);

        gson = new Gson();

        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("loginUser", message -> {
            LoginUser found = (LoginUser) message.body();
            this.loginUser = found;
        });
    }

    public void registerRoutes(Vertx vertx, Router router) {

        router.route().handler(StaticHandler.create());

        router.route(HttpMethod.GET, "/registered-courses").handler(this::handleRegisteredCourses);
        router.route(HttpMethod.GET, "/courses").handler(this::handleCourses);
        router.route(HttpMethod.POST, "/apply")
                .handler(BodyHandler.create())
                .handler(this::handleApply);
    }

    private void handleRegisteredCourses(RoutingContext routingContext) {

        List<RegisteredCourse> registeredCourses = courseService.lookUpRegisteredCoursesByStudentId(this.loginUser.getId());

        JsonArray jsonArray = new JsonArray();
        for (RegisteredCourse registeredCourse : registeredCourses) {
            JsonObject jsonObject = new JsonObject()
                    .put("id", registeredCourse.getId())
                    .put("student_id", registeredCourse.getStudent_id())
                    .put("registeredDate", new Date(registeredCourse.getRegisteredDate().getTime()))
                    .put("cancelled", registeredCourse.getCancelled())
                    .put("course_name", registeredCourse.getCourse_name());

            jsonArray.add(jsonObject);
        }

        routingContext.response()
                .putHeader("Content-Type", "application/json")
                .end(jsonArray.encode());
    }

    private void handleApply(RoutingContext routingContext) {

        JsonObject applyData = routingContext.getBodyAsJson();

        Long courseId = (Long) applyData.getInteger("courseId").longValue();
        Course course = courseService.getCourseById(courseId);
        Student student = userService.getStudentById(this.loginUser.getId());

        Registration registration = new Registration(student, course);

        courseService.applyCourse(registration);

        routingContext.response().setStatusCode(201).end();
    }

    private void handleCourses(RoutingContext routingContext) {

        List<Course> courses = courseService.lookUpCourses("available");

        JsonArray jsonArray = new JsonArray();
        for (Course course : courses) {
            JsonObject jsonObject = new JsonObject()
                    .put("id", course.getId())
                    .put("name", course.getName())
                    .put("quota", course.getQuota())
                    .put("regCount", course.getRegCount())
                    .put("status", course.getStatus());

            jsonArray.add(jsonObject);
        }

        routingContext.response()
                .putHeader("Content-Type", "application/json")
                .end(jsonArray.encode());
    }
}
