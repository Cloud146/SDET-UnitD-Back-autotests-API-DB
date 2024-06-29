package Tests.API_RestAssured;

import Helpers.Comment;
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

public class CommentTests {
    private RequestSpecification requestSpec;
    private int postId;
    private int commentId;

    @Story("Basic Auth Set Up")
    @BeforeClass
    public void setup() throws IOException {
        RestAssuredHelper.setup();
        requestSpec = RestAssuredHelper.getRequestSpec();
    }

    @Story("Создание комментария к существующему посту")
    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Тест создания комментария к существующему посту с postId", priority = 1)
    public void createAndGetCommentByIdTest() {
        postId = 474;

        Comment comment = new Comment(postId, "Автор1", "author1@example.com", "Комментарий API Rest Assured");

        int commentId = given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(comment)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .path("id");


        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .pathParam("commentId", commentId)
                .when()
                .get("/comments/{commentId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("post", equalTo(comment.getPostId()),
                        "author_name", equalTo(comment.getAuthorName()),
                        "content.rendered", equalTo("<p>" + comment.getContent() + "</p>\n"));
    }

    @Story("Обновление комментария у существующего поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Тест обновления комментария у существующего поста с postId", priority = 2)
    public void updateCommentByIdTest() {
        postId = 474;
        commentId = 216;

        Comment updatedComment = new Comment(postId, "Автор2", "author2@example.com", "Обновленный комментарий API Rest Assured");

        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(updatedComment)
                .pathParam("commentId", commentId)
                .when()
                .put("/comments/{commentId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("post", equalTo(updatedComment.getPostId()),
                        "author_name", equalTo(updatedComment.getAuthorName()),
                        "content.rendered", equalTo("<p>" + updatedComment.getContent() + "</p>\n"));

        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .pathParam("commentId", commentId)
                .when()
                .get("/comments/{commentId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("post", equalTo(updatedComment.getPostId()),
                        "author_name", equalTo(updatedComment.getAuthorName()),
                        "content.rendered", equalTo("<p>" + updatedComment.getContent() + "</p>\n"));
    }

    @Story("Удаление комментария у существующего поста")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Тест удаления комментария у существующего поста с postId", priority = 3)
    public void deleteCommentByIdTest() {
        postId = 474;
        commentId = 217;

        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .pathParam("commentId", commentId)
                .when()
                .delete("/comments/{commentId}")
                .then()
                .statusCode(200);

        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .pathParam("commentId", commentId)
                .when()
                .get("/comments/{commentId}")
                .then()
                .statusCode(200);
    }
}
