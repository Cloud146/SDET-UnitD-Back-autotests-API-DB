package Tests;

import Helpers.Comment;
import Helpers.DataBaseConnector;
import Helpers.Post;
import Helpers.WordPressClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.testng.Assert.*;

public class CommentTests {
    private WordPressClient client;
    private ObjectMapper objectMapper;

    @BeforeClass
    public void setUp() {
        String baseUrl = "http://localhost:8000";
        String username = "Firstname.LastName";
        String password = "123-Test";
        client = new WordPressClient(baseUrl, username, password);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createCommentByPostIdTest() {
        int postId = 130;
        try {
            Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
            HttpResponse<String> createCommentResponse = client.createComment(comment);
            assertEquals(createCommentResponse.statusCode(), 201);

            JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
            assertTrue(commentResponseBody.has("content"));
            assertEquals(commentResponseBody.get("content").get("rendered").asText(), "<p>Содержимое комментария</p>\n");

            int commentId = commentResponseBody.get("id").asInt();

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT comment_content FROM wp_comments WHERE comment_ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, commentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        assertTrue(resultSet.next(), "Comment not found in database");

                        String content = resultSet.getString("comment_content");
                        assertEquals(content, "Содержимое комментария");
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void updateCommentByIdTest() {
        int postId = 130;
        int commentId = 20;
        try {
            Comment updatedComment = new Comment(postId, "Автор", "author@example.com", "Обновленное содержимое комментария");
            HttpResponse<String> updateCommentResponse = client.updateComment(commentId, updatedComment);
            assertEquals(updateCommentResponse.statusCode(), 200);

            JsonNode updatedCommentResponseBody = objectMapper.readTree(updateCommentResponse.body());
            assertEquals(updatedCommentResponseBody.get("content").get("rendered").asText(), "<p>Обновленное содержимое комментария</p>\n");

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT comment_content FROM wp_comments WHERE comment_ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, commentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        assertTrue(resultSet.next(), "Comment not found in database");

                        String content = resultSet.getString("comment_content");
                        assertEquals(content, "Обновленное содержимое комментария");
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void deleteCommentByIdTest() {
        int postId = 132;
        int commentId = 22;
        try {
            HttpResponse<String> deleteCommentResponse = client.deleteComment(postId ,commentId);
            assertEquals(deleteCommentResponse.statusCode(), 200);

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT comment_approved FROM wp_comments WHERE comment_ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, commentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            String commentApproved = resultSet.getString("comment_approved");
                            assertEquals(commentApproved, "trash", "Comment not marked as deleted in database");
                        } else {
                            fail("Comment not found in database after deletion");
                        }
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }


    @Test
    public void createPostAndCommentTest() {
        try {
            Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
            HttpResponse<String> createPostResponse = client.createPost(post);
            assertEquals(createPostResponse.statusCode(), 201);

            JsonNode postResponseBody = objectMapper.readTree(createPostResponse.body());
            int postId = postResponseBody.get("id").asInt();

            Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
            HttpResponse<String> createCommentResponse = client.createComment(comment);
            assertEquals(createCommentResponse.statusCode(), 201);

            JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
            assertTrue(commentResponseBody.has("content"));
            assertEquals(commentResponseBody.get("content").get("rendered").asText(), "<p>Содержимое комментария</p>\n");
            int commentId = commentResponseBody.get("id").asInt();

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT comment_content FROM wp_comments WHERE comment_ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, commentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        assertTrue(resultSet.next(), "Comment not found in database");

                        String content = resultSet.getString("comment_content");
                        assertEquals(content, "Содержимое комментария");
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void createPostAndCommentAndUpdateCommentTest() {
        try {
            Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
            HttpResponse<String> createPostResponse = client.createPost(post);
            assertEquals(createPostResponse.statusCode(), 201);

            JsonNode postResponseBody = objectMapper.readTree(createPostResponse.body());
            int postId = postResponseBody.get("id").asInt();

            Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
            HttpResponse<String> createCommentResponse = client.createComment(comment);
            assertEquals(createCommentResponse.statusCode(), 201);

            JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
            int commentId = commentResponseBody.get("id").asInt();

            Comment updatedComment = new Comment(postId, "Автор", "author@example.com", "<p>Обновленное содержимое комментария</p>");
            HttpResponse<String> updateCommentResponse = client.updateComment(commentId, updatedComment);
            assertEquals(updateCommentResponse.statusCode(), 200);

            JsonNode updatedCommentResponseBody = objectMapper.readTree(updateCommentResponse.body());
            assertEquals(updatedCommentResponseBody.get("content").get("rendered").asText(), "<p>Обновленное содержимое комментария</p>\n");

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT comment_content FROM wp_comments WHERE comment_ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, commentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        assertTrue(resultSet.next(), "Updated comment not found in database");

                        String content = resultSet.getString("comment_content");
                        assertEquals(content, "Обновленное содержимое комментария");
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test()
    public void cratePostAndCommentAndDeleteCommentTest() {
        try {
            Post post = new Post("Заголовок поста", "Содержимое поста", "publish");
            HttpResponse<String> createPostResponse = client.createPost(post);
            assertEquals(createPostResponse.statusCode(), 201);

            JsonNode postResponseBody = objectMapper.readTree(createPostResponse.body());
            int postId = postResponseBody.get("id").asInt();

            Comment comment = new Comment(postId, "Автор", "author@example.com", "Содержимое комментария");
            HttpResponse<String> createCommentResponse = client.createComment(comment);
            assertEquals(createCommentResponse.statusCode(), 201);

            JsonNode commentResponseBody = objectMapper.readTree(createCommentResponse.body());
            int commentId = commentResponseBody.get("id").asInt();

            HttpResponse<String> deleteCommentResponse = client.deleteComment(postId ,commentId);
            assertEquals(deleteCommentResponse.statusCode(), 200);

            try (Connection connection = DataBaseConnector.getConnection()) {
                String query = "SELECT comment_approved FROM wp_comments WHERE comment_ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, commentId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            String commentApproved = resultSet.getString("comment_approved");
                            assertEquals(commentApproved, "trash", "Comment not marked as deleted in database");
                        } else {
                            fail("Comment not found in database after deletion");
                        }
                    }
                }
            }
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }
}
