import burgers.user.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

public class UserChangeTest {
    private UserApi userApi;
    private UserRegisterRequest userDataRegister;
    private UserRegisterRequest userDataRegisterTwo;
    private Response response;
    private ValidatableResponse validatableResponse;
    private String accessToken;
    private String accessTokenTwo;

    @Before
    public  void prepare() {
        userApi = new UserApi();
        userDataRegister = new UserRandom().generateUser();
        validatableResponse = userApi.register(userDataRegister);
        // Сохраним токен для последующего удаления пользователя
        if (isTrue(validatableResponse.extract().path("success"))) {
            // Сохраним токен для последующего зменения и удаления пользователя
            accessToken = validatableResponse.extract().path("accessToken");
        }
    }

    @DisplayName("Изменение email пользователя с авторизацией")
    @Description("Изменяем email пользователя с авторизацией")
    @Test
    public void userChangeEmailAuthTest() {
        userDataRegister.setEmail(new UserRandom().email());
        response = userApi.change(accessToken, userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.then().extract().statusCode())
                .as("Неверный Статус Код при изменении email авторизированного пользователя")
                .isEqualTo(HttpStatus.SC_OK);
        softAssertions.assertThat(isTrue(response.then().extract().path("success")))
                .as("Неверный статус ответа при изменении email авторизированного пользователя")
                .isTrue();
        softAssertions.assertThat(responseObject.user.getEmail().toString())
                .as("Новое значение Email не сохранилось !")
                .isEqualTo(userDataRegister.getEmail());
        softAssertions.assertAll();



    }

    @DisplayName("Изменение name пользователя с авторизацией")
    @Description("Изменяем name пользователя с авторизацией")
    @Test
    public void userChangeNameAuthTest() {
        String oldEmail = userDataRegister.getName();
        userDataRegister.setPassword(new UserRandom().name());
        response = userApi.change(accessToken, userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.then().extract().statusCode())
                .as("Неверный Статус Код при изменении name авторизированного пользователя")
                .isEqualTo(HttpStatus.SC_OK);
        softAssertions.assertThat(isTrue(response.then().extract().path("success")))
                .as("Неверный статус ответа при изменении name авторизированного пользователя")
                .isTrue();
        softAssertions.assertThat(responseObject.user.getName().toString())
                .as("Новое значение name не сохранилось !")
                .isEqualTo(userDataRegister.getName());
        softAssertions.assertAll();

    }

    @DisplayName("Изменение email пользователя без авторизации")
    @Description("Изменяем email пользователя без авторизации")
    @Test
    public void userChangeEmailNoAuthTest() {
        userDataRegister.setEmail(new UserRandom().email());
        response = userApi.change(userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.then().extract().statusCode())
                .as("Неверный Статус Код при изменении email не авторизированного пользователя")
                .isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        softAssertions.assertThat(isTrue(response.then().extract().path("success")))
                .as("Неверный статус ответа при изменении email не авторизированного пользователя")
                .isFalse();
        softAssertions.assertThat(response.then().extract().path("message").toString())
                .as("Неверное сообщения при ошибки изменении email не авторизированного пользователя")
                .isEqualTo("You should be authorised");
        softAssertions.assertAll();

    }

    @DisplayName("Изменение name пользователя без авторизации")
    @Description("Изменяем name пользователя без авторизации")
    @Test
    public void userChangeNameNoAuthTest() {
        String oldEmail = userDataRegister.getName();
        userDataRegister.setPassword(new UserRandom().name());
        response = userApi.change( userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.then().extract().statusCode())
                .as("Неверный Статус Код при изменении name не авторизированного пользователя")
                .isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        softAssertions.assertThat(isTrue(response.then().extract().path("success")))
                .as("Неверный статус ответа при изменении name не авторизированного пользователя")
                .isFalse();
        softAssertions.assertThat(response.then().extract().path("message").toString())
                .as("Неверное сообщения при ошибки изменении name не авторизированного пользователя")
                .isEqualTo("You should be authorised");
        softAssertions.assertAll();

    }

    @DisplayName("Изменить email пользователя на существующий email")
    @Description("Изменяем email пользователя на существующий email")
    @Test
    public void userChangeEmailToEmailTest() {
        // Создаем второго пользователя
        userDataRegisterTwo = new UserRandom().generateUser();
        validatableResponse = userApi.register(userDataRegisterTwo);
        // Сохраним токен для последующего удаления пользователя
        if (isTrue(validatableResponse.extract().path("success"))) {
            // Сохраним токен для последующего зменения и удаления пользователя
            accessTokenTwo = validatableResponse.extract().path("accessToken");
        }

        // Поменяем почту первого пользователя на почту второго
        userDataRegister.setEmail(userDataRegisterTwo.getEmail());
        response = userApi.change(accessToken, userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.then().extract().statusCode())
                .as("Неверный Статус Код при изменении email на существующий email")
                .isEqualTo(HttpStatus.SC_FORBIDDEN);
        softAssertions.assertThat(isTrue(response.then().extract().path("success")))
                .as("Неверный статус ответа при изменении email на существующий email")
                .isFalse();
        softAssertions.assertThat(response.then().extract().path("message").toString())
                .as("Неверное сообщения при ошибки изменении email на существующий email")
                .isEqualTo("User with such email already exists");
        softAssertions.assertAll();

        // Удаляем второго пользователя
        if (!isNull(accessTokenTwo)) {
            userApi.delete(accessTokenTwo);
        }
    }

    // Удаляем пользователя, если он был создан
    @After
    public void tearDown() {
        if ( !isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
