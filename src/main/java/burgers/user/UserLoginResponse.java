package burgers.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserLoginResponse {
    private Boolean success;
    private String accessToken;
    private String refrashToken;
    class user {
        private String email;
        private String name;
    }

}
