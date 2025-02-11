import burgers.user.UserApi;
import burgers.user.UserRandom;
import burgers.user.UserRegisterRequest;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UserCreateRequiredFieldsTest {

    private UserApi userApi;
    private String email;
    private String password;
    private String name;
    private UserRegisterRequest userDataRegister;
    private ValidatableResponse response;
    private String accessToken;

    @Before
    public  void prepare() {
        userApi = new UserApi();
        userDataRegister = new UserRandom().generateUser();
    }

    public UserCreateRequiredFieldsTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = " email ({0}) , password ({1}) , name ({2})")
    public static Object[][] setParams() {
        return new Object[][]{
                {"", new UserRandom().password(), new UserRandom().name()},
                {null, new UserRandom().password(), new UserRandom().name()},
                {new UserRandom().email(), "", new UserRandom().name()},
                {new UserRandom().email(), null, new UserRandom().name()},
                {new UserRandom().email(), new UserRandom().password(), ""},
                {new UserRandom().email(), new UserRandom().password(), null},
        };
    }

    // Создать пользователя с не заполненым полем email
    @Step("Создать пользователя")
    @Test
    public void userLoginTest() {
        userDataRegister = new UserRegisterRequest(email, password, name);
        response = userApi.register(userDataRegister);
        if (isTrue(response.extract().path("success"))) {
            accessToken = response.extract().path("accessToken");
        }
        assertEquals("Неверный Статус Код при создании пользователя без обязательных полей",
                HttpStatus.SC_FORBIDDEN, response.extract().statusCode());
        assertEquals("Неверный статус сообщения при создании пользователя без обязательных полей",
                false, response.extract().path("success"));
        assertEquals("Неверное сообщения при создании пользователя без обязательных полей",
                "Email, password and name are required fields", response.extract().path("message"));

    };

    // Удаляем пользователя, если он был создан
    @After
    public void tearDown() {
        if ( !isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
