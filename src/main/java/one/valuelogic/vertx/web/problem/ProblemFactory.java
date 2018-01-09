package one.valuelogic.vertx.web.problem;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.DecodeException;
import org.zalando.problem.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nurkiewicz.typeof.TypeOf.whenTypeOf;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;

public class ProblemFactory {

    private static Map<Integer, StatusType> toStatusType = Arrays.stream(Status.values())
            .collect(Collectors.toMap(Status::getStatusCode, identity()));

    private ProblemFactory() {
    }

    public static Problem create(Throwable throwable, int statusCode) {
        return whenTypeOf(throwable)
                .is(JsonProcessingException.class).thenReturn(ex -> exceptionToProblem(toStatusType.getOrDefault(statusCode, Status.BAD_REQUEST), getJacksonProcessingExceptionMessage(ex)))
                .is(DecodeException.class).thenReturn(exceptionToProblem(toStatusType.getOrDefault(statusCode, Status.BAD_REQUEST), "Failed to decode JSON"))
                .is(DefaultProblem.class).thenReturn(ProblemFactory::defaultToProblem)
                .orElse(Problem.valueOf(toStatusType.getOrDefault(statusCode, Status.INTERNAL_SERVER_ERROR)));
    }

    private static ThrowableProblem exceptionToProblem(StatusType statusType, String details) {
        return Problem.builder()
                .withStatus(statusType)
                .withTitle(statusType.getReasonPhrase())
                .withDetail(details).build();
    }

    private static String getJacksonProcessingExceptionMessage(JsonProcessingException ex) {
        JsonLocation loc = ex.getLocation();
        return String.format("Failed to decode JSON at line: %s, column: %s", loc.getLineNr(), loc.getColumnNr());
    }

    private static ThrowableProblem defaultToProblem(DefaultProblem defaultProblem) {
        if (isNull(defaultProblem.getStatus())) {
            return exceptionToProblem(Status.INTERNAL_SERVER_ERROR, defaultProblem.getMessage());
        }

        return defaultProblem;
    }
}
