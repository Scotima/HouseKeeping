
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection connect() throws SQLException {
        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 데이터베이스 연결
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/accountbook",
                    "root",
                    ""
            );
        } catch (Exception e) {
            throw new SQLException("데이터베이스 연결 실패", e);
        }
    }
}
