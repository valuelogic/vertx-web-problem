package one.valuelogic.vertx.web.problem.impl;

import io.vertx.ext.web.RoutingContext;
import one.valuelogic.vertx.web.problem.NotFoundHandler;

import static org.zalando.problem.Problem.valueOf;
import static org.zalando.problem.Status.NOT_FOUND;

public class NotFoundHandlerImpl implements NotFoundHandler {
    @Override
    public void handle(RoutingContext context) {
        context.fail(valueOf(NOT_FOUND));
    }
}