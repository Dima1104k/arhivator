package p2p;

import java.io.Serializable;

public class ArchiveInfo implements Serializable {
    private String name;
    private long size;
    private String format;

    public ArchiveInfo(String name, long size, String format) {
        this.name = name;
        this.size = size;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return name + " (" + size + " bytes, " + format + ")";
    }
}
