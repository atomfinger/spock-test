package john.mikael.gundersen.healthcare

import spock.lang.Specification

class UserValidatorImplSpec extends Specification {

    EmailValidator emailValidator = Mock()

    UserValidatorImpl validator

    def setup() {
        validator = new UserValidatorImpl(false, emailValidator)
    }

    def "should not validate(user is retired without no retiredReason"() {
        when: "user is retired"
            def errors = validator.validate(user(retired: true))
        then:
            errors.errorCount == 1
            errors.objectName == "john.mikael.gundersen.healthcare.User"
            errors.getFieldError("retireReason").codes.contains("error.null")
    }

    def "user has multiple errors"() {
        when:
            def errors = validator.validate(
                    user([gender: null, retired: true, username: "username with spaces"])
            )
        then:
            errors.errorCount == 3
    }

    def "user must have person object"() {
        when:
            def errors = validator.validate(user([person: null]))
        then:
            errors.getFieldError("person").codes.contains("error.null")
    }

    def "person missing gender field"() {
        when:
            def errors = validator.validate(user([gender: null]))
        then:
            errors.getFieldError("person.gender").codes.contains("error.null")
    }

    def "person dead field is null"() {
        when:
            def errors = validator.validate(user([dead: null]))
        then:
            errors.getFieldError("person.dead").codes.contains("error.null")
    }

    def "person voided status is null"() {
        when:
            def errors = validator.validate(user([voided: null]))
        then:
            errors.getFieldError("person.voided").codes.contains("error.null")
    }

    def "person name is null"() {
        when:
            def errors = validator.validate(user([personName: null]))
        then:
            errors.getFieldError("person").codes.contains("Person.names.length")
    }

    def "person name is blank string"() {
        when:
            def errors = validator.validate(user([personName: ""]))
        then:
            errors.getFieldError("person").codes.contains("Person.names.length")
    }

    def "username is not email and is invalid"(String username) {
        when:
            def errors = validator.validate(user([username: username]))
        then:
            errors.getFieldError("username").codes.contains("error.username.pattern")
        where:
            username << [")SpecialSymbol",
                         "anotherSpecialSymbol#",
                         "username with spaces",
                         "ThisIsASuperLongUsernameWhoWouldEvenHaveSuchAUsername",
                         "-usernameStartingWithDash"]
    }

    def "verify some valid usernames"(String username) {
        expect:
            !validator.validate(user([username: username])).hasErrors()
        where:
            username << ["", "John", "John-doe", "john_Doe", "John.Doe"]
    }

    def "username is valid email"() {
        when:
            validator.emailAsUsername = true
            def email = "john@test.com"
            emailValidator.isValid(email) >> true
        then:
            !validator.validate(user([username: email])).hasErrors()
    }

    def "username is invalid email"() {
        when:
            validator.emailAsUsername = true
            def email = "this is not an email"
            emailValidator.isValid(email) >> false
            def errors = validator.validate(user([username: email]))
        then:
            errors.getFieldError("username").codes.contains("error.username.email")
    }

    def "user has invalid email"() {
        when:
            def email = "this is not an email"
            emailValidator.isValid(email) >> false
            def errors = validator.validate(user([email: email]))
        then:
            errors.getFieldError("email").codes.contains("error.email.invalid")
    }

    static User user(Map args = [:]) {
        def map = [retired: false, username: "bob", email: null, person: person(args)] << args
        return User.builder()
                .retired(map["retired"] as boolean)
                .username(map["username"] as String)
                .person(map["person"] as Person)
                .email(map["email"] as String)
                .build()
    }

    static def person(Map args = [:]) {
        def map = [dead: false, gender: "Male", personName: "John Doe", voided: false] << args
        return Person.builder()
                .dead(map["dead"] as Boolean)
                .gender(map["gender"] as String)
                .personName(map["personName"] as String)
                .voided(map["voided"] as Boolean)
                .build()
    }
}