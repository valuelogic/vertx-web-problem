package one.valuelogic.vertx.web.problem;

import io.vertx.core.Future;
import io.vertx.core.json.Json;

import io.vertx.rxjava.core.AbstractVerticle;

import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import org.zalando.problem.ProblemModule;

public class ExampleVerticle extends AbstractVerticle {

    public final static int HTTP_PORT = 9988;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        configureJsonMapper();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route().failureHandler(ProblemHandler.create());
        router.get("/test-get").handler(context -> context.response().end("ok"));
        router.route().last().handler(NotFoundHandler.create());

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(HTTP_PORT)
                .subscribe(s -> {
                    startFuture.complete();
                }, t -> {
                    startFuture.fail(t);
                });

    }


      private void configureJsonMapper() {
        Json.mapper.registerModule(new ProblemModule());
    }
}
