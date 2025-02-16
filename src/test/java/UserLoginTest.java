import burgers.user.UserApi;
import burgers.user.UserLoginRequest;
import burgers.user.UserRandom;
import burgers.user.UserRegisterRequest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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

    @DisplayName("Авторизация пользователя")
    @Description("Авторизируем пользователя")
    @Test
    public void userLoginTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                userDataRegister.getEmail(), userDataRegister.getPassword());
        response = userApi.login(userLoginRequest);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.extract().statusCode())
                .as("Неверный Статус Код при авторизации пользователя")
                .isEqualTo(HttpStatus.SC_OK);
        softAssertions.assertThat(isTrue(response.extract().path("success")))
                .as("Неверный статус сообщения при авторизации пользователя")
                .isTrue();
        softAssertions.assertAll();
    }

    @DisplayName("Авторизация пользователя с неверным логином (email)")
    @Description("Авторизируем пользователя с неверным логином (email")
    @Test
    public void userLoginEmailTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                new UserRandom().email(), userDataRegister.getPassword());
        response = userApi.login(userLoginRequest);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.extract().statusCode())
                .as("Неверный Статус Код при авторизации пользователя с неверным логином")
                .isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        softAssertions.assertThat(response.extract().path("message").toString())
                .as("Неверное сообщение при авторизации пользователя с неверным логином")
                .isEqualTo("email or password are incorrect");
        softAssertions.assertAll();
    }

    @DisplayName("Авторизация пользователя с неверным паролем (pasword)")
    @Description("Авторизируем пользователя с неверным паролем (pasword)")
    @Test
    public void userLoginPasswordlTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                userDataRegister.getEmail(), new UserRandom().password());
        response = userApi.login(userLoginRequest);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.extract().statusCode())
                .as("Неверный Статус Код при авторизации пользователя с неверным аролем")
                .isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        softAssertions.assertThat(response.extract().path("message").toString())
                .as("Неверное сообщение при авторизации пользователя с неверным паролем")
                .isEqualTo("email or password are incorrect");
        softAssertions.assertAll();
    }

    @After
    public void tearDown() {
        if ( !isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
