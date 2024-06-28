package Tests.API_DB_RestAssured;

import Helpers.DataBaseHelper;
import Helpers.RestAssuredHelper;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.sql.SQLException;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class DataBasePostTests {
    private RequestSpecification requestSpec;
    private int postId;

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setup() throws IOException {
        RestAssuredHelper.setup();
        requestSpec = RestAssuredHelper.getRequestSpec();
    }

    @Story("Проверка данных созданных через SQL по API")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Создание поста через SQL и получение его данных по API")
    public void getPostByIdTest() throws Exception {
        postId = DataBaseHelper.insertPost("Заголовок поста", "Содержимое поста", "publish");

        given()
                .spec(requestSpec)
                .pathParam("postId", postId)
                .when()
                .get("/posts/{postId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title.rendered", equalTo("Заголовок поста"),
                "content.rendered", equalTo("<p>Содержимое поста</p>\n"),
                "status", equalTo("publish"));
    }

    @AfterMethod(description = "Удаление созданного поста")
    public void cleanUp() throws SQLException, IOException {
        if (postId != 0) {
            DataBaseHelper.deletePostById(postId);
            postId = 0;
        }
    }
}
