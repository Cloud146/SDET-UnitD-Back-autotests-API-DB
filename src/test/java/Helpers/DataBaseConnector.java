package Helpers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Вспомогательный класс для установки соединения с базой данных
 * @author Alex Seburev
 */
public class DataBaseConnector {

    private static ConfigurationProvider configurationProvider = new ConfigurationProvider();

    /**
     * Метод возвращения соединение с базой данных.
     * @return - объект Connection для подключения к базе данных
     * @throws SQLException если возникает ошибка при подключении
     */
    public static Connection getConnection() throws SQLException, IOException {
        String url = configurationProvider.getDataBaseURL();
        String user = configurationProvider.getDataBaseUsername();
        String password = configurationProvider.getDataBasePassword();

        return DriverManager.getConnection(url, user, password);
    }
}
