import burgers.user.UserApi;
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

public class UserCreateTest {
    private UserApi userApi;
    private UserRegisterRequest userDataRegister;
    private ValidatableResponse response;
    private String accessToken;

    @Before
    public  void prepare() {
        userApi = new UserApi();
        userDataRegister = new UserRandom().generateUser();
    }

    // Создать уникального пользователя
    @Step("Создать уникального пользователя")
    @Test
    public void userLoginTest() {
        response = userApi.register(userDataRegister);
        if (isTrue(response.extract().path("success"))) {
            accessToken = response.extract().path("accessToken");
        }
        assertEquals("Неверный Статус Код при создании пользователя",
                HttpStatus.SC_OK, response.extract().statusCode());
        assertEquals("Неверное сообщение при создании пользователя",
                true, isTrue(response.extract().path("success")));
    };

    // Создать пользователя, который уже зарегистрирован
    @Step("Создать пользователя, который уже зарегистрирован ")
    @Test
    public void userLoginRepeatTest() {
        //Создаем нового пользователя
        response = userApi.register(userDataRegister);
        if (isTrue(response.extract().path("success"))) {
            accessToken = response.extract().path("accessToken");
        }
        // Создаем пользователя с такими же параметрами
        response = userApi.register(userDataRegister);
        assertEquals("Неверный Статус Код при создании зарегистрированного пользователя повторно",
                HttpStatus.SC_FORBIDDEN, response.extract().statusCode());
        assertEquals("Неверный статус сообщения при создании зарегистрированного пользователя повторно",
                false, response.extract().path("success"));
        assertEquals("Неверное сообщения при создании зарегистрированного пользователя повторно",
                "User already exists", response.extract().path("message"));
    };

    // Удаляем пользователя, если он был создан
    @After
    public void tearDown() {
        if ( !isNull(accessToken)) {
           userApi.delete(accessToken);
        }
    }

}
