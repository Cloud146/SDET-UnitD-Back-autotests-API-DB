package Tests.API_RestAssured;

import Helpers.Post;
import Helpers.RestAssuredHelper;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PostTests {
    private RequestSpecification requestSpec;
    private int postId;

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setup() throws IOException {
        RestAssuredHelper.setup();
        requestSpec = RestAssuredHelper.getRequestSpec();
    }

    @Story("Создание поста")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания поста", priority = 1)
    public void createPostTest(){
        Post post = new Post("Пост API Rest Assured", "Содержимое поста", "publish");

        postId = given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .path("id");

        given()
                .spec(requestSpec)
                .pathParam("postId", postId)
                .when()
                .get("/posts/{postId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title.rendered", equalTo(post.getTitle()),
                        "content.rendered", equalTo("<p>" + post.getContent() + "</p>\n"),
                        "status", equalTo(post.getStatus()));

    }

    @Story("Обновление поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "createPostTest", description = "Тест обновления поста", priority = 2)
    public void updatePostByIdTest() {
        Post updatedPost = new Post("Обновленный пост API Rest Assured", "Обновленное содержимое поста", "publish");

        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(updatedPost)
                .pathParam("postId", postId)
                .when()
                .put("/posts/{postId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title.rendered", equalTo(updatedPost.getTitle()),
                        "content.rendered", equalTo("<p>" + updatedPost.getContent() + "</p>\n"),
                        "status", equalTo(updatedPost.getStatus()));

        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .pathParam("postId", postId)
                .when()
                .get("/posts/{postId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title.rendered", equalTo(updatedPost.getTitle()),
                        "content.rendered", equalTo("<p>" + updatedPost.getContent() + "</p>\n"),
                        "status", equalTo(updatedPost.getStatus()));
    }

    @Story("Удаление поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "createPostTest", description = "Тест удаления поста", priority = 3)
    public void deletePostByIdTest() {
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .pathParam("postId", postId)
                .when()
                .delete("/posts/{postId}")
                .then()
                .statusCode(200);

        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .pathParam("postId", postId)
                .when()
                .get("/posts/{postId}")
                .then()
                .statusCode(200);
    }
}
