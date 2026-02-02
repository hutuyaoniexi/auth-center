package com.demo.authcenter.web.response;

import java.time.OffsetDateTime;

public class ApiError {

    private OffsetDateTime timestamp;
    private int code;
    private String message;
    private String path;

    public static ApiError of(int code, String message, String path) {
        ApiError e = new ApiError();
        e.timestamp = OffsetDateTime.now();
        e.code = code;
        e.message = message;
        e.path = path;
        return e;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public String getPath() { return path; }

    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
    public void setCode(int code) { this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setPath(String path) { this.path = path; }
}
