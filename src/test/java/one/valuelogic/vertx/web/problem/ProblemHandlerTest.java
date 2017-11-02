package one.valuelogic.vertx.web.problem;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(VertxUnitRunner.class)
public class ProblemHandlerTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        vertx.deployVerticle(ExampleVerticle.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void shouldCreateProblemHandler() {
        assertThat(ProblemHandler.create()).isNotNull();
    }

    @Test
    public void shouldReturnNotFoundForFakeUrl() {
        given()
            .port(ExampleVerticle.HTTP_PORT)
        .when()
            .get("/test-error")
        .then()
            .assertThat()
                .contentType("application/problem+json")
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Failed to decode JSON"));
    }
}