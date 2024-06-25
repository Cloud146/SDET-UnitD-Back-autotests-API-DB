package Tests;

import Helpers.DataBaseHelper;
import Helpers.Post;
import Helpers.WordPressClient;
import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.asserts.SoftAssert;

import java.net.http.HttpResponse;

import static org.testng.Assert.*;

@Epic("API DB Testing")
@Feature("WordPress Posts")
public class PostTests {

    private WordPressClient client;
    private ObjectMapper objectMapper;
    SoftAssert softAssert = new SoftAssert();

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setUp() {
        String baseUrl = "http://localhost:8000";
        String username = "Firstname.LastName";
        String password = "123-Test";
        client = new WordPressClient(baseUrl, username, password);
        objectMapper = new ObjectMapper();
    }

    @Story("Создание поста")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания поста")
    public void createPostTest() throws Exception {
        Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
        HttpResponse<String> createResponse = client.createPost(post);
        softAssert.assertEquals(createResponse.statusCode(), 201);

        JsonNode responseBody = objectMapper.readTree(createResponse.body());
        int postId = responseBody.get("id").asInt();

        String[] postDetails = DataBaseHelper.getPostById(postId);
        softAssert.assertEquals(postDetails[0], "Заголовок поста");
        softAssert.assertEquals(postDetails[1], "Содержимое поста");
        softAssert.assertEquals(postDetails[2], "publish");
        softAssert.assertAll();
    }

    @Story("Обновление поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Тест обновления поста")
    public void updatePostByIDTest() throws Exception {
        int postID = 200;

        Post updatedPost = new Post("Обновленный заголовок", "Обновленное содержимое", "publish");
        HttpResponse<String> updateResponse = client.updatePost(postID, updatedPost);
        softAssert.assertEquals(updateResponse.statusCode(), 200);

        String[] postDetails = DataBaseHelper.getPostById(postID);
        softAssert.assertEquals(postDetails[0], "Обновленный заголовок");
        softAssert.assertEquals(postDetails[1], "Обновленное содержимое");
        softAssert.assertEquals(postDetails[2], "publish");
        softAssert.assertAll();
    }

    @Story("Удаление поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Тест удаления поста")
    public void deletePostByIDTest() throws Exception {
        int postID = 200;

        HttpResponse<String> deleteResponse = client.deletePost(postID);
        assertEquals(deleteResponse.statusCode(), 200);

        DataBaseHelper.checkPostDeleted(postID);
    }

    @Story("Создание и обновление поста")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания и обновления поста")
    public void createAndUpdatePostTest() throws Exception {
        Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
        HttpResponse<String> createResponse = client.createPost(post);
        softAssert.assertEquals(createResponse.statusCode(), 201);

        JsonNode responseBody = objectMapper.readTree(createResponse.body());
        int postId = responseBody.get("id").asInt();

        Post updatedPost = new Post("Обновленный заголовок", "Обновленное содержимое", "publish");
        HttpResponse<String> updateResponse = client.updatePost(postId, updatedPost);
        softAssert.assertEquals(updateResponse.statusCode(), 200);

        String[] postDetails = DataBaseHelper.getPostById(postId);
        softAssert.assertEquals(postDetails[0], "Обновленный заголовок");
        softAssert.assertEquals(postDetails[1], "Обновленное содержимое");
        softAssert.assertEquals(postDetails[2], "publish");
        softAssert.assertAll();
    }

    @Story("Создание и удаление поста")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания и удаления поста")
    public void createAndDeletePostTest() throws Exception {
        Post post = new Post("Заголовок поста для удаления", "Содержимое поста для удаления", "publish");
        HttpResponse<String> createResponse = client.createPost(post);
        softAssert.assertEquals(createResponse.statusCode(), 201);

        JsonNode createResponseBody = objectMapper.readTree(createResponse.body());
        int postId = createResponseBody.get("id").asInt();

        HttpResponse<String> deleteResponse = client.deletePost(postId);
        softAssert.assertEquals(deleteResponse.statusCode(), 200);
        DataBaseHelper.checkPostDeleted(postId);
        softAssert.assertAll();
    }
}
