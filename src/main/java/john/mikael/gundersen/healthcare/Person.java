package john.mikael.gundersen.healthcare;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Setter
@Getter
@Builder
@With
public class Person {
    private Boolean dead, voided;
    private String gender, personName;
}