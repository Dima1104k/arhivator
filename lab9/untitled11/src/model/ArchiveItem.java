package model;

import visitor.ArchiveVisitor;

import java.time.LocalDateTime;

public abstract class ArchiveItem {
    protected String name;
    protected LocalDateTime modificationDate;

    public ArchiveItem(String name, LocalDateTime modificationDate) {
        this.name = name;
        this.modificationDate = modificationDate;
    }
    public abstract void accept(ArchiveVisitor visitor);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
}