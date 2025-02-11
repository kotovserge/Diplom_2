import burgers.user.UserApi;
import burgers.user.UserLoginRequest;
import burgers.user.UserRandom;
import burgers.user.UserRegisterRequest;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.junit.Assert.assertEquals;

public class UserLoginTest {
    private UserApi userApi;
    private UserRegisterRequest userDataRegister;
    private UserLoginRequest userDataLogin;
    private ValidatableResponse response;
    private String accessToken;

    @Before
    public  void prepare() {
        userApi = new UserApi();
        userDataRegister = new UserRandom().generateUser();
        // Создание пользователя
        response = userApi.register(userDataRegister);
        // Сохраним токен для последующего удаления пользователя
        if (isTrue(response.extract().path("success"))) {
            // Сохраним токен для последующего удаления пользователя
            accessToken = response.extract().path("accessToken");
        }
    }

    @Step("Авторизация пользователя")
    @Test
    public void userLoginTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                userDataRegister.getEmail(), userDataRegister.getPassword());
        response = userApi.login(userLoginRequest);
        assertEquals("Неверный Статус Код при авторизации пользователя",
                HttpStatus.SC_OK, response.extract().statusCode());
        assertEquals("Неверный статус сообщения при авторизации пользователя",
                true, response.extract().path("success"));
    }

    @Step("Авторизация с неверным логином (email)")
    @Test
    public void userLoginEmailTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                new UserRandom().email(), userDataRegister.getPassword());
        response = userApi.login(userLoginRequest);
        assertEquals("Неверный Статус Код при авторизации пользователя",
                HttpStatus.SC_UNAUTHORIZED, response.extract().statusCode());
        assertEquals("Неверный статус сообщения при авторизации пользователя",
                "email or password are incorrect", response.extract().path("message"));
    }

    @Step("Авторизация с неверным паролем (pasword)")
    @Test
    public void userLoginPasswordlTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                userDataRegister.getEmail(), new UserRandom().password());
        response = userApi.login(userLoginRequest);
        assertEquals("Неверный Статус Код при авторизации пользователя",
                HttpStatus.SC_UNAUTHORIZED, response.extract().statusCode());
        assertEquals("Неверный статус сообщения при авторизации пользователя",
                "email or password are incorrect", response.extract().path("message"));
    }

    @After
    public void tearDown() {
        if ( !isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
