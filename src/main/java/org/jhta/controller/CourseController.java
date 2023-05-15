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
import org.jhta.domain.*;
import org.jhta.service.CourseService;
import org.jhta.service.UserService;
import org.jhta.subselect.RegisteredCourse;
import org.jhta.subselect.RegisteredStudent;

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

        router.route(HttpMethod.GET, "/registered-students").handler(this::handleRegisteredStudents);
        router.route(HttpMethod.GET, "/registered-courses").handler(this::handleRegisteredCourses);
        router.route(HttpMethod.GET, "/available-courses").handler(this::handleAvailableCourses);
        router.route(HttpMethod.GET, "/opened-up-courses").handler(this::handleOpenedUpCourses);

        router.route(HttpMethod.POST, "/apply")
                .handler(BodyHandler.create())
                .handler(this::handleApply);

        router.route(HttpMethod.POST, "/abort-registration")
                .handler(BodyHandler.create())
                .handler(this::handleAbort);

        router.route(HttpMethod.POST, "/open-up")
                .handler(BodyHandler.create())
                .handler(this::handleOpenUp);

        router.route(HttpMethod.POST, "/close-course")
                .handler(BodyHandler.create())
                .handler(this::handleClose);
    }

    private void handleClose(RoutingContext routingContext) {

        JsonObject closeData = routingContext.getBodyAsJson();

        Long courseId = closeData.getInteger("courseId").longValue();

        Course course = courseService.getCourseById(courseId);
        courseService.deleteCourse(course);

        routingContext.response().setStatusCode(201).end();
    }

    private void handleAbort(RoutingContext routingContext) {

        JsonObject abortData = routingContext.getBodyAsJson();

        Long registrationId = abortData.getInteger("registrationId").longValue();

        Registration registration = courseService.getRegistrationById(registrationId);
        courseService.deleteRegistration(registration);

        routingContext.response().setStatusCode(201).end();
    }

    private void handleOpenUp(RoutingContext routingContext) {

        JsonObject openUpData = routingContext.getBodyAsJson();

        String courseName = openUpData.getString("course-name");
        int courseQuota = openUpData.getInteger("course-quota");
        Teacher teacher = userService.getTeacherById(this.loginUser.getId());

        Course course = new Course(courseName, courseQuota, teacher);

        courseService.openUpCourse(course);

        routingContext.response().setStatusCode(201).end();
    }

    private void handleOpenedUpCourses(RoutingContext routingContext) {

        List<Course> courses = courseService.getCoursesByTeacherId(this.loginUser.getId());

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

    private void handleRegisteredStudents(RoutingContext routingContext) {

        List<RegisteredStudent> registeredStudents = courseService.getRegisteredStudentsByTeacherId(this.loginUser.getId());

        JsonArray jsonArray = new JsonArray();
        for (RegisteredStudent registeredStudent : registeredStudents) {
            if (registeredStudent !=null) {
                JsonObject jsonObject = new JsonObject()
                        .put("course_name", registeredStudent.getCourse_name())
                        .put("course_status", registeredStudent.getCourse_status())
                        .put("student_id", registeredStudent.getStudent_id());

                jsonArray.add(jsonObject);
            }
        }

        routingContext.response()
                .putHeader("Content-Type", "application/json")
                .end(jsonArray.encode());
    }

    private void handleRegisteredCourses(RoutingContext routingContext) {

        List<RegisteredCourse> registeredCourses = courseService.getRegisteredCoursesByStudentId(this.loginUser.getId());

        JsonArray jsonArray = new JsonArray();
        for (RegisteredCourse registeredCourse : registeredCourses) {
            if (registeredCourse != null) {
                JsonObject jsonObject = new JsonObject()
                        .put("id", registeredCourse.getId())
                        .put("student_id", registeredCourse.getStudent_id())
                        .put("registeredDate", new Date(registeredCourse.getRegisteredDate().getTime()))
                        .put("cancelled", registeredCourse.getCancelled())
                        .put("course_name", registeredCourse.getCourse_name());

                jsonArray.add(jsonObject);
            }
        }

        routingContext.response()
                .putHeader("Content-Type", "application/json")
                .end(jsonArray.encode());
    }

    private void handleApply(RoutingContext routingContext) {

        JsonObject applyData = routingContext.getBodyAsJson();

        Long courseId = applyData.getInteger("courseId").longValue();
        Course course = courseService.getCourseById(courseId);
        Student student = userService.getStudentById(this.loginUser.getId());

        Registration registration = new Registration(student, course);

        courseService.applyCourse(registration);

        routingContext.response().setStatusCode(201).end();
    }

    private void handleAvailableCourses(RoutingContext routingContext) {

        List<Course> courses = courseService.getCoursesByStatus("available");

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
