package john.mikael.gundersen.healthcare;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Code copied and rewritten from the OPENMRS project:
 * https://github.com/openmrs/openmrs-core/blob/1c21cb563496bbfeb58133b0ad2ea8b39121783e/api/src/main/java/org/openmrs/validator/UserValidator.java
 */
public class UserValidatorImpl implements UserValidator {

    private boolean emailAsUsername;

    private EmailValidator emailValidator;

    public UserValidatorImpl(boolean emailAsUsername, EmailValidator emailValidator) {
        this.emailAsUsername = emailAsUsername;
        this.emailValidator = emailValidator;
    }

    public void setEmailAsUsername(boolean emailAsUsername) {
        this.emailAsUsername = emailAsUsername;
    }

    /**
     * Checks that all required parameters for a user are filled out
     *
     * @param user to validate
     * @return Errors which contains all the invalid information for the given user
     */
    public Errors validate(User user) {
        Errors errors = new BindException(user, user.getClass().getName());
        if (user.isRetired() && StringUtils.isEmpty(user.getRetireReason()))
            errors.rejectValue("retireReason", "error.null");
        errors.addAllErrors(validatePerson(user));
        errors.addAllErrors(validateUsername(user));
        return errors;
    }

    private Errors validatePerson(User user) {
        Errors errors = new BindException(user, user.getClass().getName());
        Person person = user.getPerson();
        if (user.getPerson() == null) {
            errors.rejectValue("person", "error.null");
            return errors;
        }
        if (person.getGender() == null)
            errors.rejectValue("person.gender", "error.null");
        if (person.getDead() == null)
            errors.rejectValue("person.dead", "error.null");
        if (person.getVoided() == null)
            errors.rejectValue("person.voided", "error.null");
        if (person.getPersonName() == null || person.getPersonName().isEmpty())
            errors.rejectValue("person", "Person.names.length");
        return errors;
    }

    private Errors validateUsername(User user) {
        Errors errors = new BindException(user, user.getClass().getName());
        if (emailAsUsername) {
            boolean isValidUserName = isUserNameAsEmailValid(user.getUsername());
            if (!isValidUserName)
                errors.rejectValue("username", "error.username.email");
        } else {
            boolean isValidUserName = isUserNameValid(user.getUsername());
            if (!isValidUserName)
                errors.rejectValue("username", "error.username.pattern");
        }
        if (!StringUtils.isEmpty(user.getEmail()) && !isEmailValid(user.getEmail()))
            errors.rejectValue("email", "error.email.invalid");
        return errors;
    }

    private boolean isUserNameValid(String username) {
        String expression = "^[\\w][\\Q_\\E\\w-\\.]{1,49}$";
        if (StringUtils.isEmpty(username))
            return true;
        try {
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(username);
            return matcher.matches();
        } catch (PatternSyntaxException pex) {

            return false;
        }
    }

    public boolean isUserNameAsEmailValid(String username) {
        return emailValidator.isValid(username);
    }

    private boolean isEmailValid(String email) {
        return emailValidator.isValid(email);
    }
}
