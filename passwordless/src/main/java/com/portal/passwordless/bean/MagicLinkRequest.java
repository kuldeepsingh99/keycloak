package com.portal.passwordless.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class MagicLinkRequest {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    @JsonProperty("expiration_seconds")
    private int expirationSeconds = 60 * 60 * 24;

    @JsonProperty("force_create")
    private boolean forceCreate = false;

    @JsonProperty("update_profile")
    private boolean updateProfile = false;

    @JsonProperty("update_password")
    private boolean updatePassword = false;

    @JsonProperty("send_email")
    private boolean sendEmail = false;

    @JsonProperty("scope")
    private String scope = null;

    @JsonProperty("nonce")
    private String nonce = null;

    @JsonProperty("state")
    private String state = null;

    @JsonProperty("remember_me")
    private Boolean rememberMe = false;

    @JsonProperty("reusable")
    private Boolean actionTokenPersistent = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public int getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(int expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public boolean isForceCreate() {
        return forceCreate;
    }

    public void setForceCreate(boolean forceCreate) {
        this.forceCreate = forceCreate;
    }

    public boolean isUpdateProfile() {
        return updateProfile;
    }

    public void setUpdateProfile(boolean updateProfile) {
        this.updateProfile = updateProfile;
    }

    public boolean isUpdatePassword() {
        return updatePassword;
    }

    public void setUpdatePassword(boolean updatePassword) {
        this.updatePassword = updatePassword;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Boolean getActionTokenPersistent() {
        return actionTokenPersistent;
    }

    public void setActionTokenPersistent(Boolean actionTokenPersistent) {
        this.actionTokenPersistent = actionTokenPersistent;
    }
}
