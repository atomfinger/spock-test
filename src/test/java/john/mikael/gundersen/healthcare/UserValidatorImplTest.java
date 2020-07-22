package john.mikael.gundersen.healthcare;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static john.mikael.gundersen.healthcare.asserts.ErrorsAssert.errorsAssertThat;
import static org.assertj.core.api.Assertions.assertThat;
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
        val input = validUser();
        input.setRetired(true);
        errorsAssertThat(validator.validate(input))
                .hasErrorCount(1)
                .withObjectName("john.mikael.gundersen.healthcare.User")
                .hasCode("retireReason", "error.null");
    }

    @Test
    public void validate_hasMultipleErrors_multipleErrorsReturned() {
        val input = validUser();
        input.setRetired(true);
        input.getPerson().setGender(null);
        input.setUsername("username with spaces");
        errorsAssertThat(validator.validate(input)).hasErrorCount(3);
    }

    @Test
    public void validatePerson_personIsNull_userRejected() {
        val input = validUser();
        input.setPerson(null);
        errorsAssertThat(validator.validate(input))
                .hasCode("person", "error.null");
    }

    @Test
    public void validatePerson_personMissingGender_genderRejected() {
        val input = validUser();
        input.getPerson().setGender(null);
        errorsAssertThat(validator.validate(input))
                .hasCode("person.gender", "error.null");
    }

    @Test
    public void validatePerson_personNeitherDeadOrAlive_genderRejected() {
        val input = validUser();
        input.getPerson().setDead(null);
        errorsAssertThat(validator.validate(input))
                .hasCode("person.dead", "error.null");
    }

    @Test
    public void validatePerson_personVoidedStatusMissing_voidedRejected() {
        val input = validUser();
        input.getPerson().setVoided(null);
        errorsAssertThat(validator.validate(input))
                .hasCode("person.voided", "error.null");
    }

    @Test
    public void validatePerson_personNameIsNull_nameRejected() {
        val input = validUser();
        input.getPerson().setPersonName(null);
        errorsAssertThat(validator.validate(input))
                .hasCode("person", "Person.names.length");
    }

    @Test
    public void validatePerson_personNameIsEmptyString_nameRejected() {
        val input = validUser();
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
        val input = validUser();
        input.setUsername(username);
        errorsAssertThat(validator.validate(input))
                .hasCode("username", "error.username.pattern");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "John-doe", "john_Doe", "John.Doe"})
    public void validateUsername_usernameIsNotEmailAndUsernameIsValid_noErrors(String username) {
        val input = validUser();
        input.setUsername(username);
        assertThat(validator.validate(input).hasErrors()).isFalse();
    }

    @Test
    public void validateUsername_usernameIsEmailAndEmailIsValid_noErrors() {
        validator.setEmailAsUsername(true);
        val email = "john@test.com";
        val input = validUser();
        input.setUsername(email);
        when(emailValidator.isValid(email)).thenReturn(true);
        assertThat(validator.validate(input).hasErrors()).isFalse();
    }

    @Test
    public void validateUsername_usernameIsEmailAndEmailIsInvalid_usernameRejected() {
        validator.setEmailAsUsername(true);
        val email = "this is not an email";
        val input = validUser();
        input.setUsername(email);
        when(emailValidator.isValid(email)).thenReturn(false);
        errorsAssertThat(validator.validate(input))
                .hasCode("username", "error.username.email");
    }

    @Test
    public void validateUsername_emailIsInvalid_emailRejected() {
        val email = "this is not an email";
        val input = validUser();
        input.setEmail(email);
        when(emailValidator.isValid(email)).thenReturn(false);
        errorsAssertThat(validator.validate(input))
                .hasCode("email", "error.email.invalid");
    }

    private User validUser() {
        return User.builder()
                .retired(false)
                .username("Bob")
                .person(validPerson())
                .build();
    }

    private Person validPerson() {
        return Person.builder()
                .dead(false)
                .gender("Male")
                .personName("John Doe")
                .voided(false)
                .build();
    }
}