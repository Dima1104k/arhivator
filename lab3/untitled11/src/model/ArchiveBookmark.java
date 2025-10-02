package model;


import java.time.LocalDateTime;

public class ArchiveBookmark {
    private long bookmarkID;
    private long archiveID;
    private String itemPath;
    private String displayName;
    private boolean isFolder;
    private String note;
    private LocalDateTime createdAt;

    public ArchiveBookmark(long bookmarkID, long archiveID, String itemPath, String displayName,
                           boolean isFolder, String note, LocalDateTime createdAt) {
        this.bookmarkID = bookmarkID;
        this.archiveID = archiveID;
        this.itemPath = itemPath;
        this.displayName = displayName;
        this.isFolder = isFolder;
        this.note = note;
        this.createdAt = createdAt;
    }

    public long getBookmarkID() {
        return bookmarkID;
    }

    public void setBookmarkID(long bookmarkID) {
        this.bookmarkID = bookmarkID;
    }

    public long getArchiveID() {
        return archiveID;
    }

    public void setArchiveID(long archiveID) {
        this.archiveID = archiveID;
    }

    public String getItemPath() {
        return itemPath;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}