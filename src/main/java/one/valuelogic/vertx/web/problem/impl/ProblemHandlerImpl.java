package one.valuelogic.vertx.web.problem.impl;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import one.valuelogic.vertx.web.problem.ProblemFactory;
import one.valuelogic.vertx.web.problem.ProblemHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

public class ProblemHandlerImpl implements ProblemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemHandlerImpl.class);

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
        Problem problem = ProblemFactory.create(context.failure());
        if (isServerProblem(problem)) {
            LOG.error("Server error when handling path: " + context.request().path(), context.failure());
        }
        write(context.response(), problem);
    }

    private static boolean isServerProblem(Problem problem) {
        return problem.getStatus() == null ||
                (problem.getStatus().getStatusCode() >= 500 && problem.getStatus().getStatusCode() < 600);
    }

    private static void write(HttpServerResponse response, Problem problem) {
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
}
