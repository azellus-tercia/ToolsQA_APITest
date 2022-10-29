import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserGetJson;
import model.CreateUserPostJson;
import model.JsonSuccessResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import runner.BaseRunner;
import runner.EndPoints;

public final class AccountTest extends BaseRunner {
    private String newUserId;
    private String newTokenAPI;

    @Test (description = "Отправляем запрос и получаем ответ от сервера")
    public void testGetResponse() {
        Response response = requestSpec()
                .body(new CreateUserPostJson("username",  "password"))
                .post(EndPoints.PAGE_ACCOUNT_USER);

        Assert.assertNotNull(response);
    }

    @Test
    public void testCreateUserWithWrongPassword() {
        CreateUserGetJson response = requestSpecWithAuth()
                .body(new CreateUserPostJson("username",  "password"))
                .post(EndPoints.PAGE_ACCOUNT_USER)
                .as(CreateUserGetJson.class);

        Assert.assertEquals(response.getCode(), "1300");
        Assert.assertEquals(response.getMessage(), "Passwords must have at least one non alphanumeric character," +
                " one digit ('0'-'9'), one uppercase ('A'-'Z'), one lowercase ('a'-'z')," +
                " one special character and Password must be eight characters or longer.");
    }

    @Test
    public void testGetHeaders() {
        Response response = requestSpec()
                .get(EndPoints.PAGE_BOOKSTORE_BOOKS);

        Assert.assertEquals(response.header("Content-Type"), "application/json; charset=utf-8");
        Assert.assertEquals(response.header("Content-Length"), "4514");
        Assert.assertEquals(response.header("Server"), "nginx/1.17.10 (Ubuntu)");
        Assert.assertEquals(response.header("Connection"), "keep-alive");
        Assert.assertEquals(response.header("X-Powered-By"), "Express");
        Assert.assertEquals(response.header("ETag"), "W/\"11a2-8zfX++QwcgaCjSU6F8JP9fUd1tY\"");
    }

    @Test
    public void testUserExists() {
        CreateUserGetJson createUserGetJson = requestSpec()
                .body(new CreateUserPostJson("test_rest", "Testrest@123"))
                .post(EndPoints.PAGE_ACCOUNT_USER).getBody().as(CreateUserGetJson.class);

        Assert.assertEquals(createUserGetJson.getCode(), "1204");
        Assert.assertEquals(createUserGetJson.getMessage(), "User exists!");
    }

    @Test
    public void emptyContentTypeUserRegistrationTest() {
        JsonSuccessResponse jsonSuccessResponse = RestAssured
                .given()
                .body(new CreateUserPostJson("test_rest", "rest@123"))
                .post(EndPoints.BASE_API_URL + EndPoints.PAGE_ACCOUNT_USER)
                .getBody()
                .as(JsonSuccessResponse.class);

        Assert.assertEquals(jsonSuccessResponse.getCode(), "1200");
        Assert.assertEquals(jsonSuccessResponse.getMessage(), "UserName and Password required.");
    }

    @Test
    public void userRegistrationSuccessfulTest() {
        Response response = requestSpec()
                .body(new CreateUserPostJson("test_rest", "Rest@123"))
                .post(EndPoints.PAGE_ACCOUNT_USER);

        newUserId = response
                .getBody()
                .jsonPath()
                .get("userID")
                .toString();

        newTokenAPI = requestSpec()
                .body(new CreateUserPostJson("test_rest", "Rest@123"))
                .post(EndPoints.PAGE_GENERATE_TOKEN)
                .getBody()
                .jsonPath()
                .get("token")
                .toString();

        Assert.assertEquals(response.getStatusCode(), 201);
    }

    @Test (dependsOnMethods = "userRegistrationSuccessfulTest")
    public void userRegistrationExistingUserTest() {
        Response response = requestSpec()
                .body(new CreateUserPostJson("test_rest", "Rest@123"))
                .post(EndPoints.PAGE_ACCOUNT_USER);

        Assert.assertEquals(response.getStatusCode(), 406);
    }

    @Test(dependsOnMethods = "userRegistrationExistingUserTest")
    public void userDeleteTest() {
        Response response = requestSpec()
                .auth()
                .oauth2(newTokenAPI)
                .delete(EndPoints.PAGE_ACCOUNT_USER + "/" + newUserId);

        Assert.assertEquals(response.getStatusCode(), 204);
    }
}
