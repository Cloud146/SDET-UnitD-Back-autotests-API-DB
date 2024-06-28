package Tests;

import Helpers.DataBaseHelper;
import Helpers.WordPressClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class DataBaseCommentTestsOld {

    SoftAssert softAssert = new SoftAssert();
    private ObjectMapper objectMapper;
    private int postId;
    private List<Integer> commentIds;

    @Story("Basic Auth Set Up")
    @BeforeMethod
    public void setUp() throws Exception {
        String baseUrl = "http://localhost:8000";
        String username = "Firstname.LastName";
        String password = "123-Test";
        WordPressClient client = new WordPressClient(baseUrl, username, password);
        objectMapper = new ObjectMapper();

        RestAssured.baseURI = baseUrl;
        RestAssured.authentication = RestAssured.basic(username, password);

        commentIds = new ArrayList<>();
    }

    @Story("Проверка данных созданных через SQL по API")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Создание поста и комментариев к нему через SQL и получение ответов по API")
    public void getCommentsByPostIdTest() throws SQLException, IOException {
        postId = DataBaseHelper.insertPost("Заголовок поста", "Содержимое поста", "publish");

        int commentId1 = DataBaseHelper.insertComment(postId, "Автор1", "author1@example.com", "Содержимое комментария 1");
        int commentId2 = DataBaseHelper.insertComment(postId, "Автор2", "author2@example.com", "Содержимое комментария 2");
        commentIds.add(commentId1);
        commentIds.add(commentId2);

        int commentCount = DataBaseHelper.getCommentCountForPost(postId);
        softAssert.assertEquals(commentCount, 2, "Number of comments in the database does not match");

        Response response1 = RestAssured.get("/wp-json/wp/v2/comments/" + commentId1);
        softAssert.assertEquals(response1.statusCode(), 200);

        Response response2 = RestAssured.get("/wp-json/wp/v2/comments/" + commentId2);
        softAssert.assertEquals(response2.statusCode(), 200);

        String commentContent1 = DataBaseHelper.getCommentById(commentId1);
        softAssert.assertEquals(commentContent1, "Содержимое комментария 1");

        String commentContent2 = DataBaseHelper.getCommentById(commentId2);
        softAssert.assertEquals(commentContent2, "Содержимое комментария 2");
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


