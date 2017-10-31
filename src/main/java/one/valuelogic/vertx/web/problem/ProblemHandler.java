package one.valuelogic.vertx.web.problem;

import io.vertx.core.Handler;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

public class ProblemHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemHandler.class);
    private static final String INTERNAL_SERVER_ERROR = "{" +
        "\"title\":\"" +
        Status.INTERNAL_SERVER_ERROR.getReasonPhrase() +
        "\"," +
        "\"status\":\"" +
        Status.INTERNAL_SERVER_ERROR.getStatusCode() +
        "\"" +
        "}";

    @Override
    public void handle(RoutingContext context) {
        Problem problem = ProblemFactory.create(context.failure(), context.request().path());
        write(context.response(), problem);
    }

    public static void write(HttpServerResponse response, Problem problem) {
        int statusCode = problem.getStatus() != null ? problem.getStatus().getStatusCode() : Status.OK.getStatusCode();

        response.setChunked(true).putHeader("Content-Type", "application/problem+json");

        try {
            response
                .setStatusCode(statusCode)
                .write(Json.encode(problem));
        } catch (EncodeException e) {
            LOG.error("Error while writing problem to JSON", e);
            response
                .setStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .write(INTERNAL_SERVER_ERROR);
        } finally {
            response.end();
        }
    }

    public static Handler<RoutingContext> create() {
        return new ProblemHandler();
    }
}
