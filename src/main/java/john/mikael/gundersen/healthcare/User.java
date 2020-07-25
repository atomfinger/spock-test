package john.mikael.gundersen.healthcare;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Setter
@Getter
@Builder
@With
public class User {

    private boolean retired;
    private String retireReason, username, email;
    private Person person;
}