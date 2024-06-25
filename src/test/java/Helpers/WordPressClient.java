package Helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * Вспомогательный класс с методами работы с WordPress
 * @author Alex Seburev
 */
public class WordPressClient {
    private final String baseUrl;
    private final String authHeader;
    private final ObjectMapper objectMapper;

    /**
     * Объект авторизированного клиента WordPress
     * @param baseUrl - базой URL
     * @param username - логин для входа
     * @param password - пароль для входа
     */
    public WordPressClient(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        String auth = username + ":" + password;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Метод создания нового поста.
     * @param post - объект поста
     * @return HTTP - ответ в виде строки
     * @throws Exception если возникает ошибка при отправке запроса
     */
    @Step("Создание нового поста")
    public HttpResponse<String> createPost(Post post) throws Exception {
        String url = baseUrl + "/wp-json/wp/v2/posts";
        String json = objectMapper.writeValueAsString(post);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Метод обновления существующего поста.
     * @param postId - ID поста
     * @param post - объект поста с обновленными данными
     * @return HTTP - ответ в виде строки
     * @throws Exception если возникает ошибка при отправке запроса
     */
    @Step("Обновление существующего поста")
    public HttpResponse<String> updatePost(int postId, Post post) throws Exception {
        String url = baseUrl + "/wp-json/wp/v2/posts/" + postId;
        String json = objectMapper.writeValueAsString(post);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeader)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Метод удаления поста.
     * @param postId - ID поста
     * @return HTTP - ответ в виде строки
     * @throws Exception если возникает ошибка при отправке запроса
     */
    @Step("Удаление поста")
    public HttpResponse<String> deletePost(int postId) throws Exception {
        String url = baseUrl + "/wp-json/wp/v2/posts/" + postId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Метод создания нового комментария.
     * @param comment - объект комментария
     * @return HTTP - ответ в виде строки
     * @throws Exception если возникает ошибка при отправке запроса
     */
    @Step("Создание нового комментария")
    public HttpResponse<String> createComment(Comment comment) throws Exception {
        String url = baseUrl + "/wp-json/wp/v2/comments";
        String json = objectMapper.writeValueAsString(comment);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Метод обновления существующего комментария.
     * @param commentId - ID комментария
     * @param comment - объект комментария с обновленными данными
     * @return HTTP -ответ в виде строки
     * @throws Exception если возникает ошибка при отправке запроса
     */
    @Step("Обновление существующего комментария")
    public HttpResponse<String> updateComment(int commentId, Comment comment) throws Exception {
        String url = baseUrl + "/wp-json/wp/v2/comments/" + commentId;
        String json = objectMapper.writeValueAsString(comment);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeader)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Метод удаления комментария.
     * @param commentId - ID комментария
     * @return HTTP - ответ в виде строки
     * @throws Exception если возникает ошибка при отправке запроса
     */
    @Step("Удаление комментария")
    public HttpResponse<String> deleteComment(int postId, int commentId) throws Exception {
        String url = baseUrl + "/wp-json/wp/v2/comments/" + commentId + "?post=" + postId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
