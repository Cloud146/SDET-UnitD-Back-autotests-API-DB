package Helpers;

import io.qameta.allure.Step;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Вспомогательный класс с методами работы с Базой Данных
 * @author Alex Seburev
 */
public class DataBaseHelper {

    /**
     * Метод проверки наличия поста в базе данных по его ID.
     * @param postId - ID поста
     * @return массив строк, содержащий заголовок, содержимое и статус поста
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
    @Step("Проверка наличия поста в базе данных по его ID")
    public static String[] getPostById(int postId) throws SQLException, IOException {
        try (Connection connection = DataBaseConnector.getConnection()) {
            String query = "SELECT post_title, post_content, post_status FROM wp_posts WHERE ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, postId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String title = resultSet.getString("post_title");
                        String content = resultSet.getString("post_content");
                        String status = resultSet.getString("post_status");
                        return new String[]{title, content, status};
                    } else {
                        throw new SQLException("Post not found in database");
                    }
                }
            }
        }
    }

    /**
     * Метод проверки, помечен ли пост как удаленный в базе данных.
     * @param postId - ID поста
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
    @Step("Проверка, помечен ли пост как удаленный в базе данных")
    public static void checkPostDeleted(int postId) throws SQLException, IOException {
        try (Connection connection = DataBaseConnector.getConnection()) {
            String query = "SELECT post_status FROM wp_posts WHERE ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, postId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String status = resultSet.getString("post_status");
                        if (!"trash".equals(status)) {
                            throw new SQLException("Post not marked as deleted in database");
                        }
                    } else {
                        throw new SQLException("Post not found in database");
                    }
                }
            }
        }
    }

    /**
     * Метод проверки наличия комментария в базе данных по его ID.
     * @param commentId - ID комментария
     * @return строка с содержимым комментария
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
    @Step("Проверка наличия комментария в базе данных по его ID")
    public static String getCommentById(int commentId) throws SQLException, IOException {
        try (Connection connection = DataBaseConnector.getConnection()) {
            String query = "SELECT comment_content FROM wp_comments WHERE comment_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, commentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("comment_content");
                    } else {
                        throw new SQLException("Comment not found in database");
                    }
                }
            }
        }
    }

    /**
     * Метод проверки, помечен ли комментарий как удаленный в базе данных.
     * @param commentId - ID комментария
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
    @Step("Проверка, помечен ли комментарий как удаленный в базе данных")
    public static void checkCommentDeleted(int commentId) throws SQLException, IOException {
        try (Connection connection = DataBaseConnector.getConnection()) {
            String query = "SELECT comment_approved FROM wp_comments WHERE comment_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, commentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String approved = resultSet.getString("comment_approved");
                        if (!"trash".equals(approved)) {
                            throw new SQLException("Comment not marked as deleted in database");
                        }
                    } else {
                        throw new SQLException("Comment not found in database");
                    }
                }
            }
        }
    }

    /**
     * Метод добавления нового поста в базу данных
     * @param title - заголовок поста
     * @param content - содержимое поста
     * @param status - статус поста
     * @return ID созданного поста
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    public static int insertPost(String title, String content, String status) throws SQLException, IOException {
        String query = "INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, post_excerpt, post_status, " +
                "comment_status, ping_status, post_password, post_name, to_ping, pinged, post_modified, post_modified_gmt, " +
                "post_content_filtered, post_parent, guid, menu_order, post_type, post_mime_type, comment_count) " +
                "VALUES (?, ?, ?, ?, ?, '', ?, 'open', 'open', '', '', '', '', ?, ?, '', '0', 'http://localhost:8000/?p=4', '0', 'post', '', '0')";
        try (Connection connection = DataBaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedNow = now.format(formatter);

            statement.setInt(1, 1); // post_author
            statement.setString(2, formattedNow); // post_date
            statement.setString(3, formattedNow); // post_date_gmt
            statement.setString(4, content); // post_content
            statement.setString(5, title); // post_title
            statement.setString(6, status); // post_status
            statement.setString(7, formattedNow); // post_modified
            statement.setString(8, formattedNow); // post_modified_gmt
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating post failed, no ID obtained.");
            }
        }
    }

    /**
     * Метод удаления поста из базы данных по ID.
     * @param id - ID поста
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    public static void deletePostById(int id) throws SQLException, IOException {
        String query = "DELETE FROM wp_posts WHERE ID = ?";
        try (Connection connection = DataBaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Метод добавления комментария в базу данных.
     * @param postId - идентификатор поста
     * @param author - автор комментария
     * @param authorEmail - email автора
     * @param content - содержимое комментария
     * @return идентификатор вставленного комментария
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    public static int insertComment(int postId, String author, String authorEmail, String content) throws SQLException, IOException {
        String query = "INSERT INTO wp_comments (comment_post_ID, comment_author, comment_author_email, comment_author_IP, " +
                "comment_date, comment_date_gmt, comment_content, comment_karma, comment_approved, comment_agent, " +
                "comment_type, comment_parent, user_id) " +
                "VALUES (?, ?, ?, '172.19.0.1', ?, ?, ?, 0, '0', 'Java-http-client/22.0.1', 'comment', 0, 0)";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        try (Connection connection = DataBaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, postId);
            statement.setString(2, author);
            statement.setString(3, authorEmail);
            statement.setString(4, formattedNow);
            statement.setString(5, formattedNow);
            statement.setString(6, content);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to insert comment, no ID obtained.");
                }
            }
        }
    }

    /**
     * Метод получения количества комментариев у поста
     * @param postId - идентификатор поста
     * @return количество комментариев у поста
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    public static int getCommentCountForPost(int postId) throws SQLException, IOException {
        String query = "SELECT COUNT(*) FROM wp_comments WHERE comment_post_ID = ?";

        try (Connection connection = DataBaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, postId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return 0;
                }
            }
        }
    }

    /**
     * Метод удаления комментария из базы данных по его идентификатору.
     * @param commentId - идентификатор комментария
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    public static void deleteCommentById(int commentId) throws SQLException, IOException {
        String query = "DELETE FROM wp_comments WHERE comment_ID = ?";

        try (Connection connection = DataBaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, commentId);
            preparedStatement.executeUpdate();
        }
    }
}
