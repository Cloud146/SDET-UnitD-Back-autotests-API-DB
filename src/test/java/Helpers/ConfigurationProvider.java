package Helpers;

import java.io.IOException;

public class ConfigurationProvider {

    public String getDataBaseURL() throws IOException {
        return ConfigurationManager.getInstance().getProperty("db.url");
    }

    public String getDataBaseUsername() throws IOException {
        return ConfigurationManager.getInstance().getProperty("db.username");
    }

    public String getDataBasePassword() throws IOException {
        return ConfigurationManager.getInstance().getProperty("db.password");
    }
}
