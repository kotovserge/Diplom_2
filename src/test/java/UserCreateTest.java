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
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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

    @DisplayName("Создать уникального пользователя")
    @Description("Создаем уникального пользователя")
    @Test
    public void userLoginTest() {
        response = userApi.register(userDataRegister);
        if (isTrue(response.extract().path("success"))) {
            accessToken = response.extract().path("accessToken");
        }
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.extract().statusCode())
                .as("Неверный Статус код при создании пользователя")
                .isEqualTo(HttpStatus.SC_OK);
        softAssertions.assertThat(isTrue(response.extract().path("success")))
                .as("Неверный статус сообщения при создании пользователя")
                .isTrue();
        softAssertions.assertAll();


    };

    @DisplayName("Создать пользователя, который уже зарегистрирован")
    @Description("Создаем пользователя, который уже зарегистрирован")
    @Test
    public void userLoginRepeatTest() {
        //Создаем нового пользователя
        response = userApi.register(userDataRegister);
        if (isTrue(response.extract().path("success"))) {
            accessToken = response.extract().path("accessToken");
        }
        // Создаем пользователя с такими же параметрами
        response = userApi.register(userDataRegister);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.extract().statusCode())
                .as("Неверный Статус Код при создании зарегистрированного пользователя повторно")
                .isEqualTo(HttpStatus.SC_FORBIDDEN);
        softAssertions.assertThat(isTrue(response.extract().path("success")))
                .as("Неверный статус сообщения при создании зарегистрированного пользователя повторно")
                .isFalse();
        softAssertions.assertThat(response.extract().path("message").toString())
                .as("Неверное сообщения при создании зарегистрированного пользователя повторно")
                .isEqualTo("User already exists");
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
