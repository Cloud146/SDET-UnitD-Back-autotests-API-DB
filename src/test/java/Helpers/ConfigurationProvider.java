package Helpers;

import java.io.IOException;

/**
 * Вспомогательный класс для предоставления конфигурационных данных из ConfigurationManager.
 * @author Alex Seburev
 */
public class ConfigurationProvider {

    /**
     * Возвращает URL базы данных из конфигурационного файла
     * @return URL базы данных
     * @throws IOException если возникает ошибка при получении свойства
     */
    public String getDataBaseURL() throws IOException {
        return ConfigurationManager.getInstance().getProperty("db.url");
    }

    /**
     * Возвращает имя пользователя базы данных из конфигурационного файла
     * @return имя пользователя базы данных
     * @throws IOException если возникает ошибка при получении свойства
     */
    public String getDataBaseUsername() throws IOException {
        return ConfigurationManager.getInstance().getProperty("db.username");
    }

    /**
     * Возвращает пароль базы данных из конфигурационного файла
     * @return пароль базы данных
     * @throws IOException если возникает ошибка при получении свойства
     */
    public String getDataBasePassword() throws IOException {
        return ConfigurationManager.getInstance().getProperty("db.password");
    }
}
