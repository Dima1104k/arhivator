package repository;

import model.ArchiveBookmark;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArchiveBookmarkRepositoryImpl implements IArchiveBookmarkRepository {
    private final List<ArchiveBookmark> bookmarks = new ArrayList<>();
    private long nextId = 1;

    @Override
    public void add(ArchiveBookmark bookmark) {
        if (bookmark.getBookmarkID() == 0) {
            bookmark.setBookmarkID(nextId++);
        }
        bookmarks.add(bookmark);
        System.out.println("Додано закладку '" + bookmark.getDisplayName() + "'");
    }

    @Override
    public void delete(long bookmarkId) {
        bookmarks.removeIf(b -> b.getBookmarkID() == bookmarkId);
        System.out.println("Видалено закладку з ID: " + bookmarkId);
    }

    @Override
    public List<ArchiveBookmark> listByArchive(long archiveId) {
        System.out.println("Пошук закладок для архіву з ID: " + archiveId);
        return bookmarks.stream()
                .filter(b -> b.getArchiveID() == archiveId)
                .collect(Collectors.toList());
    }
}