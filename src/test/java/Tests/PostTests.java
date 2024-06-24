package Tests;

import Helpers.DataBaseConnector;
import Helpers.Post;
import Helpers.WordPressClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.testng.Assert.*;

public class PostTests {

    private WordPressClient client;
    private ObjectMapper objectMapper;

    public int postID;

    @BeforeClass
    public void setUp() {
        String baseUrl = "http://localhost:8000";
        String username = "Firstname.LastName";
        String password = "123-Test";
        client = new WordPressClient(baseUrl, username, password);
        objectMapper = new ObjectMapper();
    }

    @Test()
    public void createPostTest() {
        try {
            Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
            HttpResponse<String> createResponse = client.createPost(post);
            assertEquals(createResponse.statusCode(), 201);

            JsonNode responseBody = objectMapper.readTree(createResponse.body());
            int postId = responseBody.get("id").asInt();

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT post_title, post_content, post_status FROM wp_posts WHERE ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, postId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        assertTrue(resultSet.next(), "Post not found in database");

                        String title = resultSet.getString("post_title");
                        String content = resultSet.getString("post_content");
                        String status = resultSet.getString("post_status");

                        assertEquals(title, "Заголовок поста");
                        assertEquals(content, "Содержимое поста");
                        assertEquals(status, "publish");
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void updatePostByIDTest() {
        postID = 122;
        try {
            Post updatedPost = new Post("Обновленный заголовок", "Обновленное содержимое", "publish");
            HttpResponse<String> updateResponse = client.updatePost(postID, updatedPost);
            assertEquals(updateResponse.statusCode(), 200);

            // Проверяем обновленный пост в базе данных
            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT post_title, post_content, post_status FROM wp_posts WHERE ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, postID);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        assertTrue(resultSet.next(), "Updated post not found in database");

                        String title = resultSet.getString("post_title");
                        String content = resultSet.getString("post_content");
                        String status = resultSet.getString("post_status");

                        assertEquals(title, "Обновленный заголовок");
                        assertEquals(content, "Обновленное содержимое");
                        assertEquals(status, "publish");
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void deletePostByIDTest() {
        postID = 134;
        try {
            HttpResponse<String> deleteResponse = client.deletePost(postID);
            assertEquals(deleteResponse.statusCode(), 200);

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT post_status FROM wp_posts WHERE ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, postID);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            String postStatus = resultSet.getString("post_status");
                            assertEquals(postStatus, "trash", "Post not marked as deleted in database");
                        } else {
                            fail("Post not found in database after deletion");
                        }
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void createAndUpdatePostTest() {
        try {
            Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
            HttpResponse<String> createResponse = client.createPost(post);
            assertEquals(createResponse.statusCode(), 201);

            JsonNode createResponseBody = objectMapper.readTree(createResponse.body());
            int postId = createResponseBody.get("id").asInt();

            Post updatedPost = new Post("Обновленный заголовок", "Обновленное содержимое", "publish");
            HttpResponse<String> updateResponse = client.updatePost(postId, updatedPost);
            assertEquals(updateResponse.statusCode(), 200);

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT post_title, post_content, post_status FROM wp_posts WHERE ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, postId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        assertTrue(resultSet.next(), "Updated post not found in database");

                        String title = resultSet.getString("post_title");
                        String content = resultSet.getString("post_content");
                        String status = resultSet.getString("post_status");

                        assertEquals(title, "Обновленный заголовок");
                        assertEquals(content, "Обновленное содержимое");
                        assertEquals(status, "publish");
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void createAndDeletePostTest() {
        try {
            Post post = new Post("Заголовок поста для удаления", "Содержимое поста для удаления", "publish");
            HttpResponse<String> createResponse = client.createPost(post);
            assertEquals(createResponse.statusCode(), 201);

            JsonNode createResponseBody = objectMapper.readTree(createResponse.body());
            int postId = createResponseBody.get("id").asInt();

            HttpResponse<String> deleteResponse = client.deletePost(postId);
            assertEquals(deleteResponse.statusCode(), 200);

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT post_status FROM wp_posts WHERE ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, postId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            String postStatus = resultSet.getString("post_status");
                            assertEquals(postStatus, "trash", "Post not marked as deleted in database");
                        } else {
                            fail("Post not found in database after deletion");
                        }
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }
}
