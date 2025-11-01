package repository;

import model.OperationLog;
import model.OperationStatus;
import model.OperationType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgresOperationLogRepository implements IOperationLogRepository {

    @Override
    public long add(OperationLog log) {
        String sql = "INSERT INTO operation_log(archive_id, operation_type, status, \"timestamp\", details) VALUES(?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, log.getArchiveInfoId());
            pstmt.setString(2, log.getOperationType().name());
            pstmt.setString(3, log.getStatus().name());
            pstmt.setObject(4, log.getTimestamp());
            pstmt.setString(5, log.getMessage());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
            throw new SQLException("Не вдалось отримати ID");

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<OperationLog> listByArchive(long archiveId, int limit) {

        String sql = "SELECT id, archive_id, operation_type, status, \"timestamp\", details FROM operation_log WHERE archive_id = ? ORDER BY \"timestamp\" DESC limit ?";
        List<OperationLog> result = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1,archiveId);
            pstmt.setInt(2,limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                OperationLog operationLog = new OperationLog(
                        rs.getLong("id"),
                        rs.getLong("archive_id"),
                        OperationType.valueOf(rs.getString("operation_type")),
                        OperationStatus.valueOf(rs.getString("status")),
                        rs.getObject("timestamp", LocalDateTime.class),
                        rs.getString("details")
                );
                result.add(operationLog);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}