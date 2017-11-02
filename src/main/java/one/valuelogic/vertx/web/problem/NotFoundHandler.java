package one.valuelogic.vertx.web.problem;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import one.valuelogic.vertx.web.problem.impl.NotFoundHandlerImpl;

@VertxGen
public interface NotFoundHandler extends Handler<RoutingContext> {

    static NotFoundHandler create() {
        return new NotFoundHandlerImpl();
    }
}