package Helpers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseHelper {

    /**
     * Проверяет наличие поста в базе данных по его ID.
     * @param postId ID поста
     * @return массив строк, содержащий заголовок, содержимое и статус поста
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
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
     * Проверяет, помечен ли пост как удаленный в базе данных.
     * @param postId ID поста
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
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
     * Проверяет наличие комментария в базе данных по его ID.
     * @param commentId ID комментария
     * @return строка с содержимым комментария
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
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
     * Проверяет, помечен ли комментарий как удаленный в базе данных.
     * @param commentId ID комментария
     * @throws SQLException если возникает ошибка при выполнении запроса
     */
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
}
