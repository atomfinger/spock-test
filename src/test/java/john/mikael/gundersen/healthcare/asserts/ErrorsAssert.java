package john.mikael.gundersen.healthcare.asserts;

import org.assertj.core.api.AbstractAssert;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorsAssert extends AbstractAssert<ErrorsAssert, Errors> {

    public ErrorsAssert(Errors actual) {
        super(actual, ErrorsAssert.class);
    }

    public static ErrorsAssert errorsAssertThat(Errors actual){
        return new ErrorsAssert(actual);
    }

    public ErrorsAssert hasErrorCount(int errorCount) {
        assertThat(actual.getFieldErrorCount()).isEqualTo(errorCount);
        return this;
    }

    public ErrorsAssert hasErrorForField(String field) {
        assertThat(actual.getFieldErrors(field)).isNotEmpty();
        return this;
    }

    public ErrorsAssert withObjectName(String objectName) {
        assertThat(actual.getObjectName()).isEqualTo(objectName);
        return this;
    }

    public ErrorsAssert hasCode(String field, String code) {
        assertThat(actual.getFieldErrors(field)).flatExtracting("codes").contains(code);
        return this;
    }
}