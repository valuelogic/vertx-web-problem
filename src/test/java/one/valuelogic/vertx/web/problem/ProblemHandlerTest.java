package one.valuelogic.vertx.web.problem;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProblemHandlerTest {
    @Test
    public void shouldCreateProblemHandler() {
        Handler<RoutingContext> contextHandler = ProblemHandler.create();

        assertThat(contextHandler).isNotNull();
    }
}