import burgers.constants.Ingredient;
import burgers.order.OrderApi;
import burgers.order.OrderRequest;
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
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();
    private OrderRequest orderRequest;
    private UserRegisterRequest userDataRegister;
    private ValidatableResponse response;
    private String accessToken;
    private String[] hash;
    private int codeStatusExpected;
    private Boolean successExpected;
    private String messageExpected;

    @Before
    public void prepare() {
        // Создание пользователя
        userDataRegister = new UserRandom().generateUser();
        response = userApi.register(userDataRegister);
        // Сохраним токен для последующего удаления пользователя
        if (isTrue(response.extract().path("success"))) {
            accessToken = response.extract().path("accessToken");
        }
    }

    public OrderCreateTest( String[] hash, String[] codeStatusExpected, String[] successExpected, String[] messageExpected) {
        this.hash = hash;
        this.codeStatusExpected = Integer.parseInt(codeStatusExpected[0]);
        this.successExpected = Boolean.parseBoolean(successExpected[0]);
        this.messageExpected = messageExpected[0];
    }

    @Parameterized.Parameters(name = " hash ({0}) , codeSatus ({1}) , success ({2}, message ({3}) )")
    public static Object[][] setParams() {
        return Ingredient.HASH;
    }

    @DisplayName("Заказ с авторизацией")
    @Description("Делаем заказ с авторизации пользователя")
    @Test
    public void createOrderAuth() {
        orderRequest = new OrderRequest(hash);
        orderApi = new OrderApi();
        // Запрос с авторизацией
        response = orderApi.createOrder(accessToken, orderRequest);
        assertEquals("Неверный Статус Код при создании заказа",
                codeStatusExpected, response.extract().statusCode());
        if (response.extract().statusCode()!=500) {
            assertEquals("Неверный статус success при создании заказа",
                    successExpected, response.extract().path("success"));
            assertEquals("Неверное сообщение message при создании заказа",
                    messageExpected, response.extract().path("message"));
        }
    }

    @DisplayName("Заказ без авторизацией")
    @Description("Делаем заказ без авторизацией пользователя")
    @Test
    public void createOrder() {
        orderRequest = new OrderRequest(hash);
        orderApi = new OrderApi();
        // Запрос без авторизации
        response = orderApi.createOrder(orderRequest);
        assertEquals("Неверный Статус Код при создании заказа без авторизацией",
                codeStatusExpected, response.extract().statusCode());
        if (response.extract().statusCode()!=500) {
            assertEquals("Невернй статус success при создании заказа без авторизацией",
                    successExpected, response.extract().path("success"));
            assertEquals("Неверное сообщение message при создании заказа без авторизацией",
                    messageExpected, response.extract().path("message"));
        }
    }

    @After
    public void tearDown() {
        if (!isNull(accessToken)) {
            userApi.delete(accessToken);
        }
    }
}
