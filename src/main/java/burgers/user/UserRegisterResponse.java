package burgers.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRegisterResponse {
    private Boolean success;
    class user {
        private String email;
        private String name;
    }
    private String accessToken;
    private String refrashToken;
}
