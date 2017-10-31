package one.valuelogic.vertx.web.problem;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

import static org.zalando.problem.Problem.valueOf;
import static org.zalando.problem.Status.NOT_FOUND;

public class NotFoundHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {
        context.fail(valueOf(NOT_FOUND));
    }

    public static NotFoundHandler create() {
        return new NotFoundHandler();
    }
}