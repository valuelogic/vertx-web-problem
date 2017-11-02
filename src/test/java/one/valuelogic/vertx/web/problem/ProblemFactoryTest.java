package one.valuelogic.vertx.web.problem;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.Json;
import org.junit.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SuppressWarnings("ConstantConditions")
public class ProblemFactoryTest {

    @Test
    public void shouldMapDefaultProblemTo500IfNoStatus() {
        ThrowableProblem defaultProblem = Problem.builder().build();

        Problem problem = ProblemFactory.create(defaultProblem);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getInstance()).isNull();
        assertThat(problem.getDetail()).isEmpty();
        assertThat(problem.getType()).isEqualTo(Problem.DEFAULT_TYPE);
        assertThat(problem.getParameters().isEmpty());
    }

    @Test
    public void shouldMapDefaultProblemToProblem() {
        ThrowableProblem defaultProblem = Problem.builder().withStatus(Status.BAD_REQUEST).withTitle("Bad Request").build();

        Problem problem = ProblemFactory.create(defaultProblem);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(400);
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getInstance()).isNull();
        assertThat(problem.getDetail()).isNull();
        assertThat(problem.getType()).isEqualTo(Problem.DEFAULT_TYPE);
        assertThat(problem.getParameters().isEmpty());
    }

    @Test
    public void shouldMapFullDefaultProblemToProblem() throws Exception {
        URI instance = URI.create("http://some-uri-identifing-problem");
        URI type = URI.create("http://some-uri-describing-type");
        String details = "Details why service is down";
        ThrowableProblem originalProblem = Problem.builder()
                .withStatus(Status.SERVICE_UNAVAILABLE)
                .withTitle("Service Unavailable")
                .withDetail(details)
                .withInstance(instance)
                .withType(type)
                .build();

        Problem problem = ProblemFactory.create(originalProblem);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(503);
        assertThat(problem.getTitle()).isEqualTo("Service Unavailable");
        assertThat(problem.getInstance()).isEqualTo(instance);
        assertThat(problem.getDetail()).isEqualTo(details);
        assertThat(problem.getType()).isEqualTo(type);
        assertThat(problem.getParameters().isEmpty());
    }

    @Test
    public void shouldReturn400ForInvalidJsonViaVertx() {
        String invalidJson = "{\"testFieldd\" : \"o\"}";
        Throwable throwable = catchThrowable(() -> Json.decodeValue(invalidJson, TestClass.class));

        Problem problem = ProblemFactory.create(throwable);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(400);
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getInstance()).isNull();
        assertThat(problem.getDetail()).isEqualTo("Failed to decode JSON");
        assertThat(problem.getType()).isEqualTo(Problem.DEFAULT_TYPE);
        assertThat(problem.getParameters().isEmpty());
    }

    @Test
    public void shouldReturn400ForInvalidJsonViaJackson() {
        String invalidJson = "{\"testFieldd\" : \"o\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        Throwable throwable = catchThrowable(() -> objectMapper.readValue(invalidJson, TestClass.class));

        Problem problem = ProblemFactory.create(throwable);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(400);
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getInstance()).isNull();
        assertThat(problem.getDetail()).matches("Failed to decode JSON at line: [0-9]+, column: [0-9]+");
        assertThat(problem.getType()).isEqualTo(Problem.DEFAULT_TYPE);
        assertThat(problem.getParameters().isEmpty());
    }

    @Test
    public void shouldReturnInternalServerErrorForNoException() {
        Problem problem = ProblemFactory.create(null);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getInstance()).isNull();
        assertThat(problem.getDetail()).isEqualTo("Internal Server Error");
        assertThat(problem.getType()).isEqualTo(Problem.DEFAULT_TYPE);
        assertThat(problem.getParameters().isEmpty());
    }

    @Test
    public void shouldReturn500ForNotHandledException() {
        Problem problem = ProblemFactory.create(new ArrayIndexOutOfBoundsException(5));

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getInstance()).isNull();
        assertThat(problem.getDetail()).isEqualTo("Internal Server Error");
        assertThat(problem.getType()).isEqualTo(Problem.DEFAULT_TYPE);
        assertThat(problem.getParameters().isEmpty());
    }

    private static class TestClass {
        public String testField;
    }

}