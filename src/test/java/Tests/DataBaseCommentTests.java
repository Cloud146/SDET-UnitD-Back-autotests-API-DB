package Tests;

import Helpers.DataBaseHelper;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;

public class DataBaseCommentTests {

    private int postId;
    private List<Integer> commentIds;

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8000/wp-json/wp/v2";
        commentIds = new ArrayList<>();
    }

    @Story("Проверка данных созданных через SQL по API")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Создание поста и комментария к нему через SQL и получение ответов и данных по API")
    public void getComment() throws SQLException, IOException {
        postId = DataBaseHelper.insertPost("Заголовок поста", "Содержимое поста", "publish");

        int commentId1 = DataBaseHelper.insertComment(postId, "Автор1", "author1@example.com", "Содержимое комментария 1");
        commentIds.add(commentId1);

        RequestSpecification request = given()
                .auth()
                .preemptive()
                .basic("Firstname.LastName", "123-Test");

        Response response = request.get("/comments/" +commentId1);

        Assert.assertEquals(response.getStatusCode(), 200);

        int id = response.jsonPath().getInt("id");
        int post = response.jsonPath().getInt("post");
        String authorName = response.jsonPath().getString("author_name");
        String content = response.jsonPath().getString("content.rendered");
        String link = response.jsonPath().getString("link");

        Assert.assertEquals(id, commentId1);
        Assert.assertEquals(post, postId);
        Assert.assertEquals(authorName, "Автор1");
        Assert.assertTrue(content.contains("Содержимое комментария 1"));
        Assert.assertEquals(link, "http://localhost:8000/#comment-" +commentId1);
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
