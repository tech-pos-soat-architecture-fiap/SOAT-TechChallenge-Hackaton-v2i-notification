package br.com.fiap.v2i.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoNotificationMessage {

    private String videoId;
    private String userEmail;
    private String status;
    private String outputUrl;
    private String errorMessage;

    public VideoNotificationMessage() {
    }

    public VideoNotificationMessage(String videoId, String userEmail, String status, String outputUrl, String errorMessage) {
        this.videoId = videoId;
        this.userEmail = userEmail;
        this.status = status;
        this.outputUrl = outputUrl;
        this.errorMessage = errorMessage;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "VideoNotificationMessage{" +
                "videoId='" + videoId + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", status='" + status + '\'' +
                ", outputUrl='" + outputUrl + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}

