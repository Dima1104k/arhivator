package repository;

import model.ArchiveInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresArchiveInfoRepository implements IArchiveInfoRepository {
    @Override
    public Optional<ArchiveInfo> findByPath(String path) {
        String sql = "SELECT id, file_path, format, total_size, created_at, last_accessed_at FROM archive_info WHERE file_path = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, path);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                ArchiveInfo info = new ArchiveInfo(
                        rs.getLong("id"),
                        rs.getString("file_path"),
                        rs.getString("format"),
                        rs.getLong("total_size"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("last_accessed_at", LocalDateTime.class)
                );
                return Optional.of(info);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public void update(ArchiveInfo info) {
        String sql = "UPDATE archive_info SET format = ?, total_size = ?, last_accessed_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, info.getFormat());
            pstmt.setLong(2, info.getTotalSize());
            pstmt.setObject(3, info.getLastAccessedAt());
            pstmt.setLong(4, info.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(ArchiveInfo info) {
        String sql = "INSERT INTO archive_info(file_path, format, total_size, created_at, last_accessed_at) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, info.getFilePath());
            pstmt.setString(2, info.getFormat());
            pstmt.setLong(3, info.getTotalSize());
            pstmt.setObject(4, info.getCreatedAt());
            pstmt.setObject(5, info.getLastAccessedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upsert(ArchiveInfo info) {
        Optional<ArchiveInfo> existing = findByPath(info.getFilePath());
        if (existing.isPresent()) {
            info.setId(existing.get().getId());
            update(info);
        } else {
            add(info);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM archive_info WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ArchiveInfo> listRecent(int limit) {
        String sql = "SELECT id, file_path, format, total_size, created_at, last_accessed_at FROM archive_info ORDER BY last_accessed_at DESC LIMIT ?";
        List<ArchiveInfo> result = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ArchiveInfo info = new ArchiveInfo(
                        rs.getLong("id"),
                        rs.getString("file_path"),
                        rs.getString("format"),
                        rs.getLong("total_size"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("last_accessed_at", LocalDateTime.class)
                );
                result.add(info);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}