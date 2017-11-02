package one.valuelogic.vertx.web.problem;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import one.valuelogic.vertx.web.problem.impl.ProblemHandlerImpl;

@VertxGen
public interface ProblemHandler extends Handler<RoutingContext> {
    static ProblemHandler create() {
        return new ProblemHandlerImpl();
    }
}
