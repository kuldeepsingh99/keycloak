package com.portal.passwordless.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class MagicLinkResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("link")
    private String link;

    @JsonProperty("sent")
    private boolean sent;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
