package iterator;

import model.ArchiveItem;

public interface ArchiveIterator {
    boolean hasNext();
    ArchiveItem next();
}
