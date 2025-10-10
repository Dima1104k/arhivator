package repository;

import model.OperationDetail;
import model.OperationStatus;
import model.OperationType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresOperationDetailRepository implements IOperationDetailRepository {
    @Override
    public void add(OperationDetail detail) {
        String sql = "INSERT INTO operationdetail(logid, itempath, action, status, message) VALUES(?,?,?,?,?)";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setLong(1,detail.getLogID());
            pstmt.setString(2,detail.getItemPath());
            pstmt.setString(3,detail.getType().name());
            pstmt.setString(4,detail.getStatus().name());
            pstmt.setString(5,detail.getMessage());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void addAll(List<OperationDetail> details) {
        for (OperationDetail detail : details){
            add(detail);
        }
    }

    @Override
    public List<OperationDetail> listByOperation(long operationId) {
        String sql = "SELECT detailid, logid, itempath, \"action\", status, message FROM operationdetail WHERE logid = ?";
        List<OperationDetail> result = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,operationId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                OperationDetail operationDetail = new OperationDetail(
                        rs.getLong("detailid"),
                        rs.getLong("logid"),
                        rs.getString("itempath"),
                        OperationType.valueOf(rs.getString("action")),
                        OperationStatus.valueOf(rs.getString("status")),
                        rs.getString("message")
                );
                result.add(operationDetail);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }
}
