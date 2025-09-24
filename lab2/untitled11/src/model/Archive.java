package model;

import java.time.LocalDateTime;

public class Archive {
    private String filePath;
    private ArchiveFormat format;
    private long totalSize;
    private ArchivedFolder rootFolder;

    public Archive(String filePath, ArchiveFormat format) {
        this.filePath = filePath;
        this.format = format;
        this.rootFolder = new ArchivedFolder("/", LocalDateTime.now());
    }

    public String getFilePath() {
        return filePath;
    }

    public ArchiveFormat getFormat() {
        return format;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public ArchivedFolder getRootFolder() {
        return rootFolder;
    }
}