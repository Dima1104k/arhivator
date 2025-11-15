package model;

import visitor.ArchiveVisitor;

import java.time.LocalDateTime;

public class ArchivedFile extends ArchiveItem {
    private long originalSize;
    private long compressedSize;
    private String checksum;

    public ArchivedFile(String name, LocalDateTime modificationDate, long originalSize, long compressedSize, String checksum) {
        super(name, modificationDate);
        this.originalSize = originalSize;
        this.compressedSize = compressedSize;
        this.checksum = checksum;
    }

    public long getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }

    public long getCompressedSize() {
        return compressedSize;
    }

    public void setCompressedSize(long compressedSize) {
        this.compressedSize = compressedSize;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public void accept(ArchiveVisitor visitor) {
        visitor.visit(this);
    }
}