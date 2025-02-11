import burgers.order.OrderApi;
import burgers.user.UserApi;
import burgers.user.UserRandom;
import burgers.user.UserRegisterRequest;
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

    @Step("Получить заказы авторизированного пользователя")
    @Test
    public void getUserAuthOrderTest() {
        response = orderApi.getUserOrders(accessToken);
        assertEquals("Неверный Статус Код при получении всех заказов авторизированного пользователя",
                HttpStatus.SC_OK, response.then().extract().statusCode());
        assertEquals("Неверный статус ответа при получении всех заказов авторизированного пользователя",
                true, response.then().extract().path("success"));
    }

    @Step("Получить заказы неавторизированного пользователя")
    @Test
    public void getUserOrdersTest() {
        response = orderApi.getUserOrders();
        assertEquals("Неверный Статус Код при получении всех заказов авторизированного пользователя",
                HttpStatus.SC_UNAUTHORIZED, response.then().extract().statusCode());
        assertEquals("Неверный статус ответа при получении всех заказов неавторизированного пользователя",
                false, response.then().extract().path("success"));
        assertEquals("Неверное сообщение при получении всех заказов неавторизированного пользователя",
                "You should be authorised", response.then().extract().path("message"));
    }

    @After
    public void tearDown() {
        if (!isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
