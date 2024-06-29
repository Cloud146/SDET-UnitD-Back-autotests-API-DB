package Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Вспомогательный класс для создания json объекта поста
 * @author Alex Seburev
 */
public class Post {
    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("status")
    private String status;

    public Post(String title, String content, String status) {
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }
}
