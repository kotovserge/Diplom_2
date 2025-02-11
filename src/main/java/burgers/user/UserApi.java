package burgers.user;

import burgers.base.BaseHttpClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import burgers.constants.Url;

public class UserApi extends BaseHttpClient {

    @Step("Send POST to register a user")
    public ValidatableResponse register(Object body) {
        return doPostRequest(Url.USER_REGISTER_API, body);
    }

    @Step ("Send POST to authorize a user")
    public ValidatableResponse login(Object body) {
        return doPostRequest(Url.USER_AUTH_API, body);
    }

    @Step ("Send PATCH to change a user")
    public Response change(String token, Object body) {
        return doPatchRequest(Url.USER_CHANGE_API, token, body);
    }

    @Step ("Send PATCH to change a user")
    public Response change( Object body) {
        return doPatchRequest(Url.USER_CHANGE_API, body);
    }

    @Step ("Send POST to delete a user")
    public ValidatableResponse delete(String token) {
        //CourierDataDelete body = new CourierDataDelete( String.valueOf(idCourier));
        return doDeleteRequest(Url.USER_DELETE_API, token);
    }
}
