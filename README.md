# spock-test
This project was made for [this](https://www.jmgundersen.net/spock-and-junit-a-comparison) blog post and is a comparison between JUnit and Spock.

Class under test can be found [here](../spock-test/src/main/java/john/mikael/gundersen/healthcare/UserValidatorImpl.java)  
The Spock tests can be found [here](src/test/groovy/john/mikael/gundersen/healthcare/UserValidatorImplSpec.groovy), while the JUnit tests can be found [here](../spock-test/src/test/java/john/mikael/gundersen/healthcare/UserValidatorImplTest.java).

## How to build
This is a maven project which is used both for build and for handling dependencies:

```mvn clean install```

## How to run tests
This is probably not to anybodies surprise, but I had troubles with getting two different testing frameworks to play together. Since this is a small example project I settled on executing the tests in the IDE, in this case IntelliJ.