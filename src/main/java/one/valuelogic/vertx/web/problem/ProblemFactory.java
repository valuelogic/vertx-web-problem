package one.valuelogic.vertx.web.problem;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.DecodeException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.zalando.problem.*;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Optional;

import static com.nurkiewicz.typeof.TypeOf.whenTypeOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class ProblemFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemFactory.class);

    private ProblemFactory() {
    }

    static Problem create(Throwable e, @Nullable String path) {
        return whenTypeOf(e)
            .is(JsonProcessingException.class).thenReturn(ex -> buildProblem(path, Status.BAD_REQUEST, ex.getMessage()))
            .is(DecodeException.class).thenReturn(ex -> buildProblem(path, Status.BAD_REQUEST, ex.getMessage()))
            .is(DefaultProblem.class).thenReturn(dProblem -> defaultToProblem(dProblem, path))
            .orElse(ex -> createInternalError(ex, path));
    }

    private static ThrowableProblem buildProblem(@Nullable String pathOrNull, StatusType statusType, String details) {
        Optional<String> path = Optional.ofNullable(pathOrNull);
        ProblemBuilder problemBuilder = Problem.builder()
                .withStatus(statusType)
                .withTitle(statusType.getReasonPhrase())
                .withDetail(details);

        path.ifPresent(s -> problemBuilder.withInstance(URI.create(s)));

        return problemBuilder.build();
    }

    private static ThrowableProblem createInternalError(Throwable ex, String path) {
        LOG.error("Internal server error when handling path: " + path, ex);
        if (nonNull(ex)) {
            return buildProblem(path, Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        else {
            return buildProblem(path, Status.INTERNAL_SERVER_ERROR, Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    private static ThrowableProblem defaultToProblem(DefaultProblem defaultProblem, String path) {
        if (isNull(defaultProblem.getStatus())) {
            return buildProblem(path, Status.INTERNAL_SERVER_ERROR, defaultProblem.getMessage());
        }

        return buildProblem(path, defaultProblem.getStatus(), defaultProblem.getMessage());
    }
}
