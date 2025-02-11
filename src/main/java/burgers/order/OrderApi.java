package burgers.order;

import burgers.base.BaseHttpClient;
import burgers.constants.Url;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OrderApi extends BaseHttpClient {

    @Step("Send GET to receive information ingredients")
    public Response ingredients() {
        return doGetRequest(Url.GET_INGREDIENTS_API);
    }

    @Step("Send POST to create order")
    public ValidatableResponse createOrder(String token, Object body) {
        return doPostRequest(Url.POST_ORDERS_API, token, body);
    }

    @Step("Send POST to create order")
    public ValidatableResponse createOrder(Object body) {
        return doPostRequest(Url.POST_ORDERS_API, body);
    }

    @Step("Send GET to receive user orders")
    public Response getUserOrders(String token) {
        return doGetRequest(Url.GET_ORDERS_API, token);
    }

    @Step("Send GET to receive user orders")
    public Response getUserOrders() {
        return doGetRequest(Url.GET_ORDERS_API);
    }

 }
