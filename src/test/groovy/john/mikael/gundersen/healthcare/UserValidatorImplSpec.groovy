package john.mikael.gundersen.healthcare

import spock.lang.Specification


class UserValidatorImplSpec extends Specification {

    EmailValidator emailValidator = Mock()

    UserValidatorImpl validator

    def setup() {
        validator = new UserValidatorImpl(false, emailValidator)
    }

    def "should not validate user is retired without no retiredReason"() {
        when: "user is retired"
            def errors = validator.validate(user(retired: true))
        then:
            errors.errorCount == 1
            errors.objectName == "john.mikael.gundersen.healthcare.User"
            errors.getFieldError("retireReason").codes.contains("error.null")
    }

    def "user must have person object"() {
        when:
            def errors = validator.validate(user([person: null]))
        then:
            errors.errorCount == 1
            errors.getFieldError("person").codes.contains("error.null")
    }

    static User user(Map args = [:]) {
        def map = [retired: false, username: "bob", person: person()] << args
        return User.builder()
                .retired(map["retired"] as boolean)
                .username(map["username"] as String)
                .person(map["person"] as Person)
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