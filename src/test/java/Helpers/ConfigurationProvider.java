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

    /**
     * Возвращает URL WordPress из конфигурационного файла
     * @return URL WordPress
     * @throws IOException если возникает ошибка при получении свойства
     */
    public String getWordPressBaseURL() throws IOException {
        return ConfigurationManager.getInstance().getProperty("wordpress.base.url");
    }

    /**
     * Возвращает имя пользователя для Basic Auth у WordPress из конфигурационного файла
     * @return имя пользователя для Basic Auth
     * @throws IOException если возникает ошибка при получении свойства
     */
    public String getWordPressBasicAuthUsername() throws IOException {
        return ConfigurationManager.getInstance().getProperty("wordpress.basic.auth.username");
    }

    /**
     * Возвращает пароль для Basic Auth у WordPress из конфигурационного файла
     * @return пароль для Basic Auth
     * @throws IOException если возникает ошибка при получении свойства
     */
    public String getWordPressBasicAuthPassword() throws IOException {
        return ConfigurationManager.getInstance().getProperty("wordpress.basic.auth.password");
    }
}
