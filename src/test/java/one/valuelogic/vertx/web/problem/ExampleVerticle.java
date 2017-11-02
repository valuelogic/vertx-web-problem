package one.valuelogic.vertx.web.problem;

import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.zalando.problem.ProblemModule;

public class ExampleVerticle extends AbstractVerticle {

    public final static int HTTP_PORT = 9988;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        configureJsonMapper();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route().failureHandler(one.valuelogic.vertx.web.problem.reactivex.ProblemHandler.create());
        router.get("/test-get").handler(context -> context.response().end("ok"));
        router.get("/test-error").handler(context -> { throw new DecodeException("testing decode error"); });
        router.route().last().handler(one.valuelogic.vertx.web.problem.reactivex.NotFoundHandler.create());

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(HTTP_PORT)
                .subscribe(s -> startFuture.complete(), startFuture::fail);
    }

    private void configureJsonMapper() {
        Json.mapper.registerModule(new ProblemModule());
    }
}
