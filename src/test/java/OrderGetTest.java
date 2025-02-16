import burgers.order.OrderApi;
import burgers.user.UserApi;
import burgers.user.UserRandom;
import burgers.user.UserRegisterRequest;
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

public class OrderGetTest {
    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();
    private UserRegisterRequest userDataRegister;
    private ValidatableResponse responseUser;
    private Response response;
    private String accessToken;

    @Before
    public void prepare() {
        // Создание пользователя
        userDataRegister = new UserRandom().generateUser();
        responseUser = userApi.register(userDataRegister);
        // Сохраним токен для последующего удаления пользователя
        if (isTrue(responseUser.extract().path("success"))) {
            accessToken = responseUser.extract().path("accessToken");
        }
    }

    @DisplayName("Проверка получения заказов авторизированным пользователем")
    @Description("Получаем заказы авторизированного пользователя")
    @Test
    public void getUserAuthOrderTest() {
        response = orderApi.getUserOrders(accessToken);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.then().extract().statusCode())
                .as("Неверный Статус Код при получении всех заказов авторизированного пользователя")
                .isEqualTo(HttpStatus.SC_OK);
        softAssertions.assertThat(isTrue(response.then().extract().path("success")))
                .as("Неверный статус ответа при получении всех заказов авторизированного пользователя")
                .isTrue();
        softAssertions.assertAll();
    }

    @DisplayName("Проверка получения заказов неавторизированным пользователем")
    @Description("Получаем заказы неавторизированного пользователя")
    @Test
    public void getUserOrdersTest() {
        response = orderApi.getUserOrders();
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.then().extract().statusCode())
                .as("Неверный Статус Код при получении всех заказов неавторизированного пользователя")
                .isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        softAssertions.assertThat(isTrue(response.then().extract().path("success")))
                .as("Неверный статус ответа при получении всех заказов неавторизированного пользователя")
                .isFalse();
        softAssertions.assertThat(response.then().extract().path("message").toString())
                .as("Неверное сообщение ответа при получении всех заказов неавторизированного пользователя")
                .isEqualTo("You should be authorised");
        softAssertions.assertAll();
    }

    @After
    public void tearDown() {
        if (!isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
