package org.jhta;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.jhta.controller.CourseController;
import org.jhta.controller.UserController;
import org.jhta.domain.LoginUser;
import org.jhta.service.CourseService;
import org.jhta.service.UserService;
import org.jhta.util.LoginUserCodec;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ServerDriver extends AbstractVerticle {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("school-app-jpa");

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        CourseService courseService = new CourseService(emf);
        UserService userService = new UserService(emf);

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        UserController userController = new UserController(userService);
        userController.registerRoutes(vertx, router);

        CourseController courseController = new CourseController(courseService, userService);
        courseController.registerRoutes(vertx, router);

        server.requestHandler(router).listen(8080, result -> {
            if (result.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            } else {
                startPromise.fail(result.cause());
            }
        });
    }

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerDriver());
//        vertx.eventBus().registerDefaultCodec(LoginUser.class, new LoginUserCodec());
    }
}
