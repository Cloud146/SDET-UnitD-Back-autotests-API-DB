package Helpers;

import java.io.IOException;
import java.util.Properties;

public class ConfigurationManager {

    private static ConfigurationManager instance;
    private Properties properties;

    private ConfigurationManager() throws IOException {
        properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));
    }

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

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
