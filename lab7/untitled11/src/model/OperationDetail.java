package model;


public class OperationDetail {
    private long detailID;
    private long logID;
    private String itemPath;
    private OperationType type;
    private OperationStatus status;
    private String message;

    public OperationDetail(long detailID, long logID, String itemPath, OperationType type,
                           OperationStatus status, String message) {
        this.detailID = detailID;
        this.logID = logID;
        this.itemPath = itemPath;
        this.type = type;
        this.status = status;
        this.message = message;
    }

    public long getDetailID() {
        return detailID;
    }

    public void setDetailID(long detailID) {
        this.detailID = detailID;
    }

    public long getLogID() {
        return logID;
    }

    public void setLogID(long logID) {
        this.logID = logID;
    }

    public String getItemPath() {
        return itemPath;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}