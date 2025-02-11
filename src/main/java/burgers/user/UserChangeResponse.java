package burgers.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserChangeResponse {
    public String suscces;
    public UserEmailName user;
}
