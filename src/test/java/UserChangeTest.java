import burgers.user.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.junit.Assert.assertEquals;

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

    // Изменить email пользователя с авторизацией
    @Step("Изменить email пользователя с авторизацией")
    @Test
    public void userChangeEmailAuthTest() {
        userDataRegister.setEmail(new UserRandom().email());
        response = userApi.change(accessToken, userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);

        assertEquals("Неверный Статус Код при изменении email пользователя",
                HttpStatus.SC_OK, response.then().extract().statusCode());
        assertEquals("Неверный статус сообщения при изменении email пользователя",
                true, response.then().extract().path("success"));
        assertEquals("Новое значение Email не сохранилось !",
                userDataRegister.getEmail(), responseObject.user.getEmail());
    }

    // Изменить password c авторизацией
    @Step("Изменить password c авторизацией")
    @Test
    public void userChangePasswordAuthTest() {
        userDataRegister.setPassword(new UserRandom().password());
        response = userApi.change(accessToken, userDataRegister);

        assertEquals("Неверный Статус Код при изменении password пользователя",
                HttpStatus.SC_OK, response.then().extract().statusCode());
        assertEquals("Неверный статус сообщения при изменении password пользователя",
                true, response.then().extract().path("success"));
    }

    // Изменить name c авторизацией
    @Step("Изменить name c авторизацией")
    @Test
    public void userChangeNameAuthTest() {
        String oldEmail = userDataRegister.getName();
        userDataRegister.setPassword(new UserRandom().name());
        response = userApi.change(accessToken, userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);

        assertEquals("Неверный Статус Код при изменении name пользователя",
                HttpStatus.SC_OK, response.then().extract().statusCode());
        assertEquals("Неверный статус сообщения при изменении name пользователя",
                true, response.then().extract().path("success"));
        assertEquals("Новое значение Name не сохранилось !",
                userDataRegister.getName(), responseObject.user.getName());
    }

    // Изменить email пользователя без авторизацией
    @Step("Изменить email пользователя без авторизацией")
    @Test
    public void userChangeEmailTest() {
        userDataRegister.setEmail(new UserRandom().email());
        response = userApi.change(userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);
        assertEquals("Неверный Статус Код при изменении email пользователя без авторизации",
                HttpStatus.SC_UNAUTHORIZED, response.then().extract().statusCode());
        assertEquals("Неверный статус сообщения при изменении name пользователя без авторизаци",
                false, response.then().extract().path("success"));
        assertEquals("Неверное сообщения при ошибки изменении email пользователя без авторизаци",
                "You should be authorised", response.then().extract().path("message"));
    }

    // Изменить password без авторизацией
    @Step("Изменить password без авторизации")
    @Test
    public void userChangePasswordTest() {
        userDataRegister.setPassword(new UserRandom().password());
        response = userApi.change( userDataRegister);

        assertEquals("Неверный Статус Код при изменении password пользователя без авторизации",
                HttpStatus.SC_UNAUTHORIZED, response.then().extract().statusCode());
        assertEquals("Неверный статус сообщения при изменении password пользователя без авторизации",
                false, response.then().extract().path("success"));
        assertEquals("Неверное сообщения ошибки при изменении password пользователя без авторизации",
                "You should be authorised", response.then().extract().path("message"));
    }

    // Изменить name без авторизацией
    @Step("Изменить name без авторизации")
    @Test
    public void userChangeNameTest() {
        String oldEmail = userDataRegister.getName();
        userDataRegister.setPassword(new UserRandom().name());
        response = userApi.change( userDataRegister);
        UserChangeResponse responseObject = response.as(UserChangeResponse.class);

        assertEquals("Неверный Статус Код при изменении name пользователя без авторизации",
                HttpStatus.SC_UNAUTHORIZED, response.then().extract().statusCode());
        assertEquals("Неверный статус сообщения при изменении name пользователя без авторизации",
                false, response.then().extract().path("success"));
        assertEquals("Неверное сообщения при ошибки изменении email пользователя без авторизации",
                "You should be authorised", response.then().extract().path("message"));
    }

    // Изменение почты  на существующую в системе
    @Step("Изменить email пользователя на существующий email")
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

        assertEquals("Неверный Статус Код при изменении email пользователя",
                HttpStatus.SC_FORBIDDEN, response.then().extract().statusCode());
        assertEquals("Неверный статус сообщения при изменении email пользователя",
                false, response.then().extract().path("success"));
        assertEquals("Неверное сообщение при обновлении почты !",
                "User with such email already exists", response.then().extract().path("message"));

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
