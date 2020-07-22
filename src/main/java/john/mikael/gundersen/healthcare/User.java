package john.mikael.gundersen.healthcare;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder(toBuilder = true)
public class User {

    private boolean retired;
    private String retireReason, username, email;
    private Person person;
}