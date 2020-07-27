package john.mikael.gundersen.healthcare;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static john.mikael.gundersen.healthcare.asserts.ErrorsAssert.errorsAssertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserValidatorImplTest {

    private EmailValidator emailValidator = mock(EmailValidator.class);

    private UserValidatorImpl validator;

    @BeforeEach
    public void init() {
        validator = new UserValidatorImpl(false, emailValidator);
    }

    @Test
    public void validate_isRetiredWithoutRetireReason_retireReasonRejected() {
        val input = user().withRetired(true);
        errorsAssertThat(validator.validate(input))
                .hasErrorCount(1)
                .withObjectName("john.mikael.gundersen.healthcare.User")
                .hasCode("retireReason", "error.null");
    }

    @Test
    public void validate_hasMultipleErrors_multipleErrorsReturned() {
        val input = user()
                .withRetired(true)
                .withPerson(person().withGender(null))
                .withUsername("username with spaces");
        errorsAssertThat(validator.validate(input)).hasErrorCount(3);
    }

    @Test
    public void validatePerson_personIsNull_userRejected() {
        val input = user().withPerson(null);
        errorsAssertThat(validator.validate(input))
                .hasCode("person", "error.null");
    }

    @Test
    public void validatePerson_personMissingGender_genderRejected() {
        val input = user().withPerson(person().withGender(null));
        errorsAssertThat(validator.validate(input))
                .hasCode("person.gender", "error.null");
    }

    @Test
    public void validatePerson_personNeitherDeadOrAlive_genderRejected() {
        val input = user().withPerson(person().withDead(null));
        errorsAssertThat(validator.validate(input))
                .hasCode("person.dead", "error.null");
    }

    @Test
    public void validatePerson_personVoidedStatusMissing_voidedRejected() {
        val input = user();
        input.getPerson().setVoided(null);
        errorsAssertThat(validator.validate(input))
                .hasCode("person.voided", "error.null");
    }

    @Test
    public void validatePerson_personNameIsNull_nameRejected() {
        val input = user().withPerson(person().withPersonName(null));
        errorsAssertThat(validator.validate(input))
                .hasCode("person", "Person.names.length");
    }

    @Test
    public void validatePerson_personNameIsEmptyString_nameRejected() {
        val input = user();
        input.getPerson().setPersonName("");
        errorsAssertThat(validator.validate(input))
                .hasCode("person", "Person.names.length");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ")SpecialSymbol",
            "anotherSpecialSymbol#",
            "username with spaces",
            "ThisIsASuperLongUsernameWhoWouldEvenHaveSuchAUsername",
            "-usernameStartingWithDash"
    })
    public void validateUsername_usernameIsNotEmailAndUsernameIsInvalid_usernameRejected(String username) {
        val input = user().withUsername(username);
        errorsAssertThat(validator.validate(input))
                .hasCode("username", "error.username.pattern");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "John-doe", "john_Doe", "John.Doe"})
    public void validateUsername_usernameIsNotEmailAndUsernameIsValid_noErrors(String username) {
        val input = user().withUsername(username);
        assertThat(validator.validate(input).hasErrors()).isFalse();
    }

    @Test
    public void validateUsername_usernameIsEmailAndEmailIsValid_noErrors() {
        validator.setEmailAsUsername(true);
        when(emailValidator.isValid(any())).thenReturn(true);
        val input = user().withEmail("john@test.com");
        assertThat(validator.validate(input).hasErrors()).isFalse();
    }

    @Test
    public void validateUsername_usernameIsEmailAndEmailIsInvalid_usernameRejected() {
        validator.setEmailAsUsername(true);
        when(emailValidator.isValid("this is not an email")).thenReturn(false);
        val input = user().withUsername("this is not an emIail");
        errorsAssertThat(validator.validate(input))
                .hasCode("username", "error.username.email");
    }

    @Test
    public void validateUsername_emailIsInvalid_emailRejected() {
        when(emailValidator.isValid("this is not an email")).thenReturn(false);
        val input = user().withEmail("this is not an email");
        errorsAssertThat(validator.validate(input))
                .hasCode("email", "error.email.invalid");
    }

    private User user() {
        return User.builder()
                .retired(false)
                .username("Bob")
                .person(person())
                .build();
    }

    private Person person() {
        return Person.builder()
                .dead(false)
                .gender("Male")
                .personName("John Doe")
                .voided(false)
                .build();
    }
}