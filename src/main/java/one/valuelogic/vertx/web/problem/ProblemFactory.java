package one.valuelogic.vertx.web.problem;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.DecodeException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.ThrowableProblem;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

import static com.nurkiewicz.typeof.TypeOf.whenTypeOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

class ProblemFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemFactory.class);

    private ProblemFactory() {
    }

    private static ThrowableProblem buildProblem(String path, Response.StatusType status, String details) {
        return buildProblem(Optional.ofNullable(path), status, details);
    }

    private static ThrowableProblem buildProblem(Optional<String> path, Response.StatusType status, String details) {
        ProblemBuilder problemBuilder = Problem.builder()
            .withStatus(status)
            .withTitle(status.getReasonPhrase())
            .withDetail(details);

        if (path.isPresent()) {
            problemBuilder.withInstance(URI.create(path.get()));
        }

        return problemBuilder.build();
    }

    static Problem create(Throwable e, @Nullable String path) {
        return whenTypeOf(e)
            .is(JsonProcessingException.class).thenReturn(ex -> buildProblem(path, BAD_REQUEST, ex.getMessage()))
            .is(DecodeException.class).thenReturn(ex -> buildProblem(path, BAD_REQUEST, ex.getMessage()))
            .is(DefaultProblem.class).thenReturn(dProblem -> defaultToProblem(dProblem, path))
            .orElse(ex -> createInternalError(ex, path));
    }

    private static ThrowableProblem createInternalError(Throwable ex, String path) {
        LOG.error("Internal server error when handling path: " + path, ex);
        if (nonNull(ex)) {
            return buildProblem(path, INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        else {
            return buildProblem(path, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    private static ThrowableProblem defaultToProblem(DefaultProblem defaultProblem, String path) {
        if (isNull(defaultProblem.getStatus())) {
            return buildProblem(path, INTERNAL_SERVER_ERROR, defaultProblem.getMessage());
        }

        return buildProblem(path, defaultProblem.getStatus(), defaultProblem.getMessage());
    }
}
