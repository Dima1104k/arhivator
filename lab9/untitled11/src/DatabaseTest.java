import repository.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            System.out.println("Підключення успішне!");

            PreparedStatement pstmt = conn.prepareStatement("SELECT NOW()");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Поточний час на сервері: " + rs.getTimestamp(1));
            }

        } catch (SQLException e) {
            System.err.println("Помилка підключення: " + e.getMessage());
        }
    }
}