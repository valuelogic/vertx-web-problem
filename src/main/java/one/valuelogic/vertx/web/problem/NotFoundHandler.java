package one.valuelogic.vertx.web.problem;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.zalando.problem.Problem.valueOf;

public class NotFoundHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {
        context.fail(valueOf(NOT_FOUND));
    }

    public static NotFoundHandler create() {
        return new NotFoundHandler();
    }
}