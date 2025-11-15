package model;

import iterator.ArchiveIterator;
import iterator.BreadthFirstIterator;
import iterator.DepthFirstIterator;
import iterator.IteratorType;

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

    public ArchiveIterator getIterator(IteratorType type){
        switch (type){
            case DEPTH_FIRST:
                return new DepthFirstIterator(rootFolder);
            case BREADTH_FIRST:
                return new BreadthFirstIterator(rootFolder);
            default:
                return new DepthFirstIterator(rootFolder);
        }
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