package Helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Вспомогательный класс для установки соединения с базой данных
 * @author Alex Seburev
 */
public class DataBaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/wordpress";
    private static final String USER = "wordpress";
    private static final String PASSWORD = "wordpress";

    /**
     * Метод возвращения соединение с базой данных.
     * @return - объект Connection для подключения к базе данных
     * @throws SQLException если возникает ошибка при подключении
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
