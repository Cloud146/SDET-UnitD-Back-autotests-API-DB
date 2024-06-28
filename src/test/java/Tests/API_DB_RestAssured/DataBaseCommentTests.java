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
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class DataBaseCommentTests {

    private int postId;
    private List<Integer> commentIds;
    private RequestSpecification requestSpec;

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setup() throws IOException {
        RestAssuredHelper.setup();
        requestSpec = RestAssuredHelper.getRequestSpec();
        commentIds = new ArrayList<>();
    }

    @Story("Проверка данных созданных через SQL по API")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Создание поста и комментария к нему через SQL и получение ответов и данных по API")
    public void getCommentsByPostIdTest() throws SQLException, IOException {
        postId = DataBaseHelper.insertPost("Заголовок поста", "Содержимое поста", "publish");

        int commentId = DataBaseHelper.insertComment(postId, "Автор1", "author1@example.com", "Содержимое комментария 1");
        commentIds.add(commentId);

        given()
                .spec(requestSpec)
                .pathParam("commentId", commentId)
                .when()
                .get("/comments/{commentId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(commentId),
                "post", equalTo(postId),
                "author_name", equalTo("Автор1"),
                "content.rendered", containsString("Содержимое комментария 1"),
                "link", equalTo("http://localhost:8000/#comment-" + commentId));
    }

    @AfterMethod(description = "Удаление созданных сущностей")
    public void cleanUp() throws SQLException, IOException {
        for (int commentId : commentIds) {
            DataBaseHelper.deleteCommentById(commentId);
        }
        if (postId != 0) {
            DataBaseHelper.deletePostById(postId);
            postId = 0;
        }
    }
}
