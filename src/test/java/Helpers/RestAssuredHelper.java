package Helpers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class RestAssuredHelper {

    private static ConfigurationProvider configurationProvider = new ConfigurationProvider();

    public static void setup() {
        RestAssured.baseURI = "http://localhost:8000/wp-json/wp/v2";
    }

    public static RequestSpecification getRequestSpec() throws IOException {
        return given()
                .auth()
                .preemptive()
                .basic(configurationProvider.getWordPressBasicAuthUsername(), configurationProvider.getWordPressBasicAuthPassword());
    }
}
