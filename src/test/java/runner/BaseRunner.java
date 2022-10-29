package runner;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.CreateUserPostJson;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public abstract class BaseRunner {

    private String tokenAPI;
    private String userId;
    private RequestSpecification toolsQA;

    @BeforeClass
    protected void createUser() {
        toolsQA = new RequestSpecBuilder()
                .setBaseUri(EndPoints.BASE_API_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        Response response = requestSpec()
                .body(new CreateUserPostJson(BaseProperties.getProperties().getProperty("username"),
                        BaseProperties.getProperties().getProperty("password")))
                .post(EndPoints.PAGE_ACCOUNT_USER);

        if (response.getStatusCode() != 201) {
            throw new RuntimeException("Error: " + response.getBody().jsonPath().get("message").toString());
        } else {
            userId = response
                    .getBody()
                    .jsonPath()
                    .get("userID")
                    .toString();
        }
    }

    @BeforeClass
    protected void setTokenAPI() {
        tokenAPI = requestSpec()
                .body(new CreateUserPostJson(BaseProperties.getProperties().getProperty("username"),
                        BaseProperties.getProperties().getProperty("password")))
                .post(EndPoints.PAGE_GENERATE_TOKEN)
                .getBody()
                .jsonPath()
                .get("token")
                .toString();
    }

    @BeforeMethod
    protected void beforeMethod(Method method) {
        BaseProperties.logf("Run %s.%s", this.getClass().getName(), method.getName());
    }

    @AfterClass
    protected void deleteUser() {
        requestSpecWithAuth()
                .delete(EndPoints.PAGE_ACCOUNT_USER + "/" + userId);
    }

    protected RequestSpecification requestSpec() {
        return RestAssured
                .given()
                .spec(toolsQA);
    }

    protected RequestSpecification requestSpecWithAuth() {
        return RestAssured
                .given()
                .spec(toolsQA)
                .auth()
                .oauth2(tokenAPI);
    }
}
