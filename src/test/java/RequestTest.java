import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.jhta.ServerDriver;
import org.jhta.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class RequestTest {

    private static EntityManagerFactory emf;
    private static Gson gson;

    public RequestTest() {
        this.gson = new Gson();
        this.emf = Persistence.createEntityManagerFactory("school-app-jpa");
    }

    @BeforeEach
    public void setUp(Vertx vertx, VertxTestContext testContext) {

        vertx.deployVerticle(new ServerDriver())
                .onSuccess(success -> testContext.completeNow())
                .onFailure(failure -> testContext.failNow(failure));
    }

    @Test
    public void testHandleRegisterStudent(Vertx vertx, VertxTestContext testContext) {

        JsonObject payload = new JsonObject()
                .put("id", "toquinho")
                .put("password", "toquinho1")
                .put("name", "Toquinho")
                .put("phone", "722-162-9186")
                .put("email", "toquiho1@school.com")
                .put("type", "student");

        vertx.createHttpClient()
                .request(HttpMethod.POST, 8080, "127.0.0.1", "/register/student")
                .compose(request -> {
                    request.putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                    return request.send(Json.encodeToBuffer(payload));
                })
                .compose(response -> response.body())
                .onComplete(result -> {
                    if (result.succeeded()) {
                        EntityManager em = emf.createEntityManager();
                        em.getTransaction().begin();

                        Student student = em.find(Student.class, "toquinho");
                        assertEquals("Toquinho", student.getName());

                        em.createNativeQuery("delete from student where id = 'toquinho'").executeUpdate();
                        em.createNativeQuery("delete from person where id = 'toquinho'").executeUpdate();

                        em.getTransaction().commit(); em.close();

                        testContext.completeNow();
                    } else {
                        testContext.failNow(result.cause());
                    }
                });
    }
}
