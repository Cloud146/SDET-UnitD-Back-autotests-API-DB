package Tests.API_HttpResponse;

import Helpers.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.io.IOException;
import java.net.http.HttpResponse;
import static org.testng.Assert.*;

@Epic("API DB Testing")
@Feature("WordPress Comments")
public class CommentTests {
    private WordPressClient client;
    private ObjectMapper objectMapper;
    SoftAssert softAssert = new SoftAssert();

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setUp() throws IOException {
        client = new WordPressClient();
        objectMapper = new ObjectMapper();
    }

    @Story("Создание комментария к существующему посту")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания комментария к существующему посту с postId", priority = 1)
    public void createCommentByPostIdTest() throws Exception {
        int postId = 469;

        Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
        HttpResponse<String> createCommentResponse = client.createComment(comment);
        softAssert.assertEquals(createCommentResponse.statusCode(), 201);

        JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
        int commentId = commentResponseBody.get("id").asInt();

        String commentContent = DataBaseHelper.getCommentById(commentId);
        softAssert.assertEquals(commentContent, "Содержимое комментария");
        softAssert.assertAll();
    }

    @Story("Обновление комментария у существующего поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Тест обновления комментария у существующего поста с postId", priority = 2)
    public void updateCommentByIdTest() throws Exception {
        int postId = 190;
        int commentId = 30;

        Comment updatedComment = new Comment(postId, "Автор", "author@example.com", "Обновленное содержимое комментария");
        HttpResponse<String> updateCommentResponse = client.updateComment(commentId, updatedComment);
        softAssert.assertEquals(updateCommentResponse.statusCode(), 200);

        String updatedCommentContent = DataBaseHelper.getCommentById(commentId);
        softAssert.assertEquals(updatedCommentContent, "Обновленное содержимое комментария");
        softAssert.assertAll();
    }

    @Story("Удаление комментария у существующего поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Тест удаления комментария у существующего поста с postId", priority = 3)
    public void deleteCommentByIdTest() throws Exception {
        int postId = 333;
        int commentId = 123;

        HttpResponse<String> deleteCommentResponse = client.deleteComment(postId ,commentId);
        assertEquals(deleteCommentResponse.statusCode(), 200);

        DataBaseHelper.checkCommentDeleted(commentId);
    }

    @Story("Создание поста и добавление комментария к нему")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания поста и добавления комментария к нему", priority = 4)
    public void createPostAndCommentTest() throws Exception {
        Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
        HttpResponse<String> createPostResponse = client.createPost(post);
        softAssert.assertEquals(createPostResponse.statusCode(), 201);

        JsonNode postResponseBody = objectMapper.readTree(createPostResponse.body());
        int postId = postResponseBody.get("id").asInt();

        Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
        HttpResponse<String> createCommentResponse = client.createComment(comment);
        softAssert.assertEquals(createCommentResponse.statusCode(), 201);

        JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
        int commentId = commentResponseBody.get("id").asInt();

        String commentContent = DataBaseHelper.getCommentById(commentId);
        softAssert.assertEquals(commentContent, "Содержимое комментария");
        softAssert.assertAll();
    }

    @Story("Создание поста, добавление и обновление комментария к нему")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания поста, добавления и обновления комментария к нему", priority = 5)
    public void createPostAndCommentAndUpdateCommentTest() throws Exception {
        Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
        HttpResponse<String> createPostResponse = client.createPost(post);
        softAssert.assertEquals(createPostResponse.statusCode(), 201);

        JsonNode postResponseBody = objectMapper.readTree(createPostResponse.body());
        int postId = postResponseBody.get("id").asInt();

        Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
        HttpResponse<String> createCommentResponse = client.createComment(comment);
        softAssert.assertEquals(createCommentResponse.statusCode(), 201);

        JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
        int commentId = commentResponseBody.get("id").asInt();

        Comment updatedComment = new Comment(postId, "Автор", "author@example.com", "Обновленное содержимое комментария");
        HttpResponse<String> updateCommentResponse = client.updateComment(commentId, updatedComment);
        softAssert.assertEquals(updateCommentResponse.statusCode(), 200);

        String updatedCommentContent = DataBaseHelper.getCommentById(commentId);
        softAssert.assertEquals(updatedCommentContent, "Обновленное содержимое комментария");
        softAssert.assertAll();
    }

    @Story("Создание поста, добавление и удаление комментария у него")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания поста, добавления и удаления комментария у него", priority = 6)
    public void cratePostAndCommentAndDeleteCommentTest() throws Exception {
        Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
        HttpResponse<String> createPostResponse = client.createPost(post);
        softAssert.assertEquals(createPostResponse.statusCode(), 201);

        JsonNode postResponseBody = objectMapper.readTree(createPostResponse.body());
        int postId = postResponseBody.get("id").asInt();

        Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
        HttpResponse<String> createCommentResponse = client.createComment(comment);
        softAssert.assertEquals(createCommentResponse.statusCode(), 201);

        JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
        int commentId = commentResponseBody.get("id").asInt();

        HttpResponse<String> deleteCommentResponse = client.deleteComment(postId ,commentId);
        softAssert.assertEquals(deleteCommentResponse.statusCode(), 200);
        DataBaseHelper.checkCommentDeleted(commentId);
        softAssert.assertAll();
    }
}
