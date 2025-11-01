package repository;

import model.ArchiveBookmark;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgresArchiveBookmarkRepository implements IArchiveBookmarkRepository{
    @Override
    public void add(ArchiveBookmark bookmark) {
        String sql = "INSERT INTO archivebookmark(archiveid, itempath, displayname, isfolder, note, created_at) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, bookmark.getArchiveID());
            pstmt.setString(2, bookmark.getItemPath());
            pstmt.setString(3, bookmark.getDisplayName());
            pstmt.setBoolean(4, bookmark.isFolder());
            pstmt.setString(5, bookmark.getNote());
            pstmt.setObject(6, bookmark.getCreatedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long bookmarkId) {
        String sql = "DELETE FROM archivebookmark WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setLong(1, bookmarkId);
             pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<ArchiveBookmark> listByArchive(long archiveId) {
        String sql = "SELECT * FROM archivebookmark WHERE archiveId = ?";
        List<ArchiveBookmark> result = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,archiveId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                ArchiveBookmark archiveBookmark = new ArchiveBookmark(
                        rs.getLong("bookmarkid"),
                        rs.getLong("archiveid"),
                        rs.getString("itempath"),
                        rs.getString("displayname"),
                        rs.getBoolean("isfolder"),
                        rs.getString("note"),
                        rs.getObject("created_at", LocalDateTime.class)
                );
                result.add(archiveBookmark);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
