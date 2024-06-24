package Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment {
    @JsonProperty("post")
    private int postId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_email")
    private String authorEmail;

    @JsonProperty("content")
    private String content;

    public Comment(int postId, String authorName, String authorEmail, String content) {
        this.postId = postId;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.content = content;
    }
}
