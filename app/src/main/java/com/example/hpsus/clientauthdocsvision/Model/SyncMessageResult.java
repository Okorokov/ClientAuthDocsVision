package com.example.hpsus.clientauthdocsvision.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SyncMessageResult {

    @SerializedName("Id")
    @Expose
    private String Id;

    @SerializedName("RequestId")
    @Expose
    private String RequestId;

    @SerializedName("MessageType")
    @Expose
    private String MessageType;

    @SerializedName("SettingsHash")
    @Expose
    private int SettingsHash;

    @SerializedName("SessionToken")
    @Expose
    private String SessionToken;

    @SerializedName("Message")
    @Expose
    private String Message;

    public SyncMessageResult() {
    }

    public SyncMessageResult(String id, String requestId, String messageType, int settingsHash, String sessionToken, String message) {
        Id = id;
        RequestId = requestId;
        MessageType = messageType;
        SettingsHash = settingsHash;
        SessionToken = sessionToken;
        Message = message;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public int getSettingsHash() {
        return SettingsHash;
    }

    public void setSettingsHash(int settingsHash) {
        SettingsHash = settingsHash;
    }

    public String getSessionToken() {
        return SessionToken;
    }

    public void setSessionToken(String sessionToken) {
        SessionToken = sessionToken;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
