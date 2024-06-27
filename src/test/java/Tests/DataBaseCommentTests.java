package Tests;

import Helpers.DataBaseHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DataBaseCommentTests {

    private final ObjectMapper objectMapper = new ObjectMapper();
    SoftAssert softAssert = new SoftAssert();
    private int postId;
    private List<Integer> commentIds;

    @BeforeMethod
    public void setUp() throws Exception {
        // Вставка поста напрямую в базу данных
        postId = DataBaseHelper.insertPost("Заголовок поста", "Содержимое поста", "publish");
        commentIds = new ArrayList<>();
    }

    @Test
    public void testGetCommentsByPostId() throws Exception {
        int commentId1 = DataBaseHelper.insertComment(postId, "Автор1", "author1@example.com", "Содержимое комментария 1");
        int commentId2 = DataBaseHelper.insertComment(postId, "Автор2", "author2@example.com", "Содержимое комментария 2");
        commentIds.add(commentId1);
        commentIds.add(commentId2);

        int commentCount = DataBaseHelper.getCommentCountForPost(postId);
        softAssert.assertEquals(commentCount, 2, "Number of comments in the database does not match");

        String commentContent1 = DataBaseHelper.getCommentById(commentId1);
        softAssert.assertEquals(commentContent1, "Содержимое комментария 1");

        String commentContent2 = DataBaseHelper.getCommentById(commentId2);
        softAssert.assertEquals(commentContent2, "Содержимое комментария 2");
        softAssert.assertAll();
    }

    /**
     * Удаляет пост и комментарии из базы данных после выполнения теста.
     */
    @AfterMethod
    public void cleanUp() throws SQLException, IOException {
        for (int commentId : commentIds) {
            DataBaseHelper.deleteCommentById(commentId);
        }
        if (postId != 0) {
            DataBaseHelper.deletePostById(postId);
            postId = 0;
        }
    }
}
