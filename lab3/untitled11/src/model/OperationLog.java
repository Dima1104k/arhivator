package model;

import java.time.LocalDateTime;
public class OperationLog {
    private long id;
    private long archiveInfoId;
    private OperationType operationType;
    private OperationStatus status;
    private LocalDateTime timestamp;
    private String message;

    public OperationLog(long id, long archiveInfoId, OperationType operationType,
                        OperationStatus status, LocalDateTime timestamp, String message) {
        this.id = id;
        this.archiveInfoId = archiveInfoId;
        this.operationType = operationType;
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
    }

    public OperationLog(long archiveInfoId, OperationType operationType,
                        OperationStatus status, String message) {
        this.id = 0;
        this.archiveInfoId = archiveInfoId;
        this.operationType = operationType;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getArchiveInfoId() {
        return archiveInfoId;
    }

    public void setArchiveInfoId(long archiveInfoId) {
        this.archiveInfoId = archiveInfoId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}