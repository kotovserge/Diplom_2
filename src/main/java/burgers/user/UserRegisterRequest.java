package burgers.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserRegisterRequest {
    private String email;
    private String password;
    private String name;

}
