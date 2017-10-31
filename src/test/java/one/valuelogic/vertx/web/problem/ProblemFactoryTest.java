package one.valuelogic.vertx.web.problem;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("ConstantConditions")
public class ProblemFactoryTest {

    @Test
    public void shouldMapDefaultProblemTo500IfNoStatus() {
        ThrowableProblem defaultProblem = Problem.builder().build();
        Problem problem = ProblemFactory.create(defaultProblem, "/api/test");

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getInstance().toString()).isEqualTo("/api/test");
    }

    @Test
    public void shouldMapDefaultProblemToProblem() {
        ThrowableProblem defaultProblem = Problem.builder().withStatus(Status.BAD_REQUEST).withTitle("Bad request").build();
        Problem problem = ProblemFactory.create(defaultProblem, "/api/test");

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(400);
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getInstance().toString()).isEqualTo("/api/test");
    }

    @Test
    public void shouldReturn400ForInvalidDefinitionException() {
        TestClass testClass = new TestClass();

        JsonMappingException mappingException = JsonMappingException.wrapWithPath(new Throwable(), testClass, "testField");
        Problem problem = ProblemFactory.create(mappingException, "/api/test");

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(400);
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getInstance().toString()).isEqualTo("/api/test");
    }

    @Test
    public void shouldReturnInternalServerException() {
        Problem problem = ProblemFactory.create(null, "/api/test");

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getInstance().toString()).isEqualTo("/api/test");
    }

    @Test
    public void shouldReturnInternalServerExceptionOnNulls() {
        Problem problem = ProblemFactory.create(null, null);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
    }

    @Test
    public void shouldReturn500ForNotDefinedException() {
        Problem problem = ProblemFactory.create(new ArrayIndexOutOfBoundsException(), null);

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getInstance()).isNull();
    }

    @Test
    public void shouldReturnProblemWithoutPath() {
        ThrowableProblem defaultProblem = Problem.builder().withStatus(Status.BAD_REQUEST).withTitle("Bad request").build();
        Problem problem = ProblemFactory.create(defaultProblem, null);

        assertThat(problem.getInstance()).isNull();
    }

    private class TestClass {
        private String testField;
    }

}