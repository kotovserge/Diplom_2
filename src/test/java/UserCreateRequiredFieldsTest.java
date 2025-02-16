import burgers.user.UserApi;
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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

    @DisplayName("Создать пользователя")
    @Description("Создаем пользователя")
    @Test
    public void userLoginTest() {
        userDataRegister = new UserRegisterRequest(email, password, name);
        response = userApi.register(userDataRegister);
        if (isTrue(response.extract().path("success"))) {
            accessToken = response.extract().path("accessToken");
        }
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.extract().statusCode())
                .as("Неверный Статус Код при создании пользователя без обязательных полей")
                .isEqualTo(HttpStatus.SC_FORBIDDEN);
        softAssertions.assertThat(isTrue(response.extract().path("success")))
                .as("Неверный статус сообщения при создании пользователя без обязательных полей")
                .isFalse();
        softAssertions.assertThat(response.extract().path("message").toString())
                .as("Неверное сообщение при создании пользователя без обязательных полей")
                .isEqualTo("Email, password and name are required fields");
        softAssertions.assertAll();

    };

    // Удаляем пользователя, если он был создан
    @After
    public void tearDown() {
        if ( !isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
