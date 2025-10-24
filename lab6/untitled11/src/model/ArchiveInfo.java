package model;

import java.time.LocalDateTime;

public class ArchiveInfo {
    private long id;
    private String filePath;
    private String format;
    private long totalSize;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    public ArchiveInfo(long id, String filePath, String format,
                       long totalSize, LocalDateTime createdAt,
                       LocalDateTime lastAccessedAt) {
        this.id = id;
        this.filePath = filePath;
        this.format = format;
        this.totalSize = totalSize;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }


}
