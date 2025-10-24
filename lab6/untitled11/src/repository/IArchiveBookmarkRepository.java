package repository;

import model.ArchiveBookmark;
import java.util.List;

public interface IArchiveBookmarkRepository {
    void add(ArchiveBookmark bookmark);
    void delete(long bookmarkId);
    List<ArchiveBookmark> listByArchive(long archiveId);
}


