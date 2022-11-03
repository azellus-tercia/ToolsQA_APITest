import io.restassured.response.Response;
import model.CreateUserPostJson;
import org.testng.Assert;
import org.testng.annotations.Test;
import runner.BaseTest;
import runner.EndPoints;

public final class AccountTest extends BaseTest {

    @Test
    public void testGetResponseJson() {
        Response response = requestSpec()
                .body(getJSONObject("request.json"))
                .post(EndPoints.PAGE_ACCOUNT_USER);

        Assert.assertNotNull(response);
    }

    @Test (description = "Отправляем запрос и получаем ответ от сервера")
    public void testGetResponse() {
        Response response = requestSpec()
                .body(new CreateUserPostJson("username",  "password").toString())
                .post(EndPoints.PAGE_ACCOUNT_USER);

        Assert.assertNotNull(response);
    }
}
