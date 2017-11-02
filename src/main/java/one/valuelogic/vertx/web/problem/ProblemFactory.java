package one.valuelogic.vertx.web.problem;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.DecodeException;
import org.zalando.problem.*;

import static com.nurkiewicz.typeof.TypeOf.whenTypeOf;
import static java.util.Objects.isNull;

public class ProblemFactory {
    private ProblemFactory() {}

    public static Problem create(Throwable throwable) {
        return whenTypeOf(throwable)
                .is(JsonProcessingException.class).thenReturn(ex -> exceptionToProblem(Status.BAD_REQUEST, getJacksonProcessingExceptionMessage(ex)))
                .is(DecodeException.class).thenReturn(ex -> exceptionToProblem(Status.BAD_REQUEST, "Failed to decode JSON"))
                .is(DefaultProblem.class).thenReturn(ProblemFactory::defaultToProblem)
                .orElse((t) -> createInternalError());
    }

    private static ThrowableProblem exceptionToProblem(StatusType statusType, String details) {
        ProblemBuilder problemBuilder = Problem.builder()
                .withStatus(statusType)
                .withTitle(statusType.getReasonPhrase())
                .withDetail(details);

        return problemBuilder.build();
    }

    private static String getJacksonProcessingExceptionMessage(JsonProcessingException ex) {
        JsonLocation loc = ex.getLocation();
        return "Failed to decode JSON at line: " + loc.getLineNr() + ", column: " + loc.getColumnNr();
    }

    private static ThrowableProblem defaultToProblem(DefaultProblem defaultProblem) {
        if (isNull(defaultProblem.getStatus())) {
            return exceptionToProblem(Status.INTERNAL_SERVER_ERROR, defaultProblem.getMessage());
        }

        return defaultProblem;
    }

    private static ThrowableProblem createInternalError() {
        return exceptionToProblem(Status.INTERNAL_SERVER_ERROR, Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
