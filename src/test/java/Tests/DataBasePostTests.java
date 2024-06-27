package Tests;

import Helpers.DataBaseHelper;
import Helpers.WordPressClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.sql.SQLException;


public class DataBasePostTests {
    private ObjectMapper objectMapper;
    SoftAssert softAssert = new SoftAssert();
    private int postId = 1;

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setUp() {
        String baseUrl = "http://localhost:8000";
        String username = "Firstname.LastName";
        String password = "123-Test";
        WordPressClient client = new WordPressClient(baseUrl, username, password);
        objectMapper = new ObjectMapper();

        RestAssured.baseURI = baseUrl;
        RestAssured.authentication = RestAssured.basic(username, password);
    }

    @Story("Проверка данных созданных через SQL по API")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Создание поста через SQL и получение его данных по API")
    public void getPostByIdTest() throws Exception {
        postId = DataBaseHelper.insertPost("Заголовок поста", "Содержимое поста", "publish");

        Response response = RestAssured.get("/wp-json/wp/v2/posts/" + postId);
        softAssert.assertEquals(response.statusCode(), 200);

        JsonNode responseBody = objectMapper.readTree(response.getBody().asString());
        softAssert.assertEquals(responseBody.get("title").get("rendered").asText(), "Заголовок поста");
        softAssert.assertEquals(responseBody.get("content").get("rendered").asText(), "<p>Содержимое поста</p>\n");
        softAssert.assertEquals(responseBody.get("status").asText(), "publish");
        softAssert.assertAll();
    }

    @AfterMethod(description = "Удаление созданных сущностей")
    public void cleanUp() throws SQLException, IOException {
        if (postId != 0) {
            DataBaseHelper.deletePostById(postId);
            postId = 0;
        }
    }
}

