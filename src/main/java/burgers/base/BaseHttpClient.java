package burgers.base;

import burgers.user.UserChangeResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

import burgers.constants.Url;

public abstract class BaseHttpClient {

    private RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri(Url.HOST)
            .addHeader("Content-Type", "application/json")
            .setRelaxedHTTPSValidation()
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new ErrorLoggingFilter())
            .build();

    protected Response doGetRequest(String path) {
        return (Response) given()
                .spec(baseRequestSpec)
                .get(path);
    }

    protected Response doGetRequest(String path, String token) {
        return (Response) given()
                .spec(baseRequestSpec)
                .header("Authorization", token)
                .get(path);
    }

    protected ValidatableResponse doPostRequest(String path, Object body) {
        return (ValidatableResponse) given()
                .spec(baseRequestSpec)
                .body(body)
                .post(path)
                .then();
    }

    protected ValidatableResponse doPostRequest(String path, String token, Object body) {
        return (ValidatableResponse) given()
                .spec(baseRequestSpec)
                .header("Authorization", token)
                .body(body)
                .post(path)
                .then();
    }

    protected Response doPatchRequest(String path, Object body) {
        return (Response) given()
                .spec(baseRequestSpec)
                .body(body)
                .patch(path);
        //.as(UserChangeResponse.class);
    }

    protected Response doPatchRequest(String path, String token, Object body) {
        return (Response) given()
                .spec(baseRequestSpec)
                .header("Authorization", token)
                .body(body)
                .patch(path);
        //.as(UserChangeResponse.class);
    }

    protected ValidatableResponse doDeleteRequest(String path, String token) {
        return given()
                .spec(baseRequestSpec)
                .header("Authorization", token)
                .delete(path)
                .then();
    }

}
