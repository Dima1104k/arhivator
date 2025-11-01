package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArchivedFolder extends ArchiveItem {
    private List<ArchiveItem> children = new ArrayList<>();

    public ArchivedFolder(String name, LocalDateTime modificationDate) {
        super(name, modificationDate);
    }

    public void addChild(ArchiveItem item) {
        children.add(item);
    }

    public List<ArchiveItem> getChildren() {
        return children;
    }
}