package com.example.Football_League_API.external.exception;

public class SyncException extends RuntimeException {

    private final String resourceType;
    private final Object resourceId;

    public SyncException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }

    public SyncException(String message, String resourceType, Object resourceId) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public SyncException(String message, Throwable cause) {
        super(message, cause);
        this.resourceType = null;
        this.resourceId = null;
    }

    public SyncException(String message, String resourceType, Object resourceId, Throwable cause) {
        super(message, cause);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }

    public String getDetailedMessage() {
        if (resourceType != null && resourceId != null) {
            return String.format("%s [%s: %s]", getMessage(), resourceType, resourceId);
        }
        return getMessage();
    }
}
