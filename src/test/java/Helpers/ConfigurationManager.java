package Helpers;

import java.io.IOException;
import java.util.Properties;

/**
 * Вспомогательный класс для управления конфигурацией приложения.
 * Предназначен для загрузки свойств из конфигурационного файла и предоставления доступа к этим свойствам.
 * @author Alex Seburev
 */
public class ConfigurationManager {

    private static ConfigurationManager instance;
    private Properties properties;

    /**
     * Конструктор для загрузки свойств из конфигурационного файла.
     * @throws IOException если возникает ошибка при загрузке файла свойств
     */
    private ConfigurationManager() throws IOException {
        properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));
    }

    /**
     * Возвращает единственный экземпляр ConfigurationManager.
     * @return экземпляр ConfigurationManager
     * @throws IOException если возникает ошибка при создании экземпляра
     */
    public static ConfigurationManager getInstance() throws IOException {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    /**
     * Возвращает значение свойства по ключу.
     * @param key ключ свойства
     * @return значение свойства
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
