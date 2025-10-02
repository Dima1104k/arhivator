package repository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream("resources/config.properties")) {
            if (input == null) {
                System.out.println("Не вдалося знайти config.properties");
            } else {
                properties.load(input);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver не знайдено");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        }

        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }
}