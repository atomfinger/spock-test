package john.mikael.gundersen.healthcare;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder(toBuilder = true)
public class Person {
    private Boolean dead, voided;
    private String gender, personName;
}