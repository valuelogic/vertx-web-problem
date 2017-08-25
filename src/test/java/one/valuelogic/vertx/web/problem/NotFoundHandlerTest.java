package one.valuelogic.vertx.web.problem;


import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
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
public class NotFoundHandlerTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        //vertx.exceptionHandler(context.exceptionHandler());
        vertx.deployVerticle(ExampleVerticle.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void shouldCreateNotFoundHandler() {
        NotFoundHandler handler = NotFoundHandler.create();
        assertThat(handler).isNotNull();
    }

    @Test
    public void shouldReturnNotFoundForBadMethod(TestContext context) {
        final Async async = context.async();
        given()
            .port(ExampleVerticle.HTTP_PORT)
        .when()
            .contentType("application/json")
            .post("/test-get")
        .then()
            .assertThat()
            .contentType("application/problem+json")
            .body("title", equalTo("Not Found"))
            .body("status", equalTo(404));
        async.complete();
    }

    @Test
    public void shouldReturnNotFoundForFakeUrl() {
        given()
            .port(ExampleVerticle.HTTP_PORT)
        .when()
            .get("/fakeUrl")
        .then()
            .assertThat()
            .contentType("application/problem+json")
            .body("title", equalTo("Not Found"))
            .body("status", equalTo(404));
    }

}