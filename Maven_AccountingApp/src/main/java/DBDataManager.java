import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBDataManager implements DataManager {
    @Override
    public void save(Transaction t, int userId) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO expenses (user_id, category, amount, description, date) VALUES (?, ?, ?, ?, ?)")) {

            ps.setInt(1, userId);
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getNote());
            ps.setTimestamp(5, new Timestamp(t.getDate().getTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Transaction> loadAll(int userId) {
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT e.category, e.amount, e.description, e.date, u.username " +
                     "FROM expenses e " +
                     "JOIN users u ON e.user_id = u.id " +
                     "WHERE e.user_id = ?")) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction();
                t.setName(rs.getString("username"));
                t.setType(rs.getString("category"));
                t.setAmount(rs.getDouble("amount"));
                t.setNote(rs.getString("description"));
                t.setDate(rs.getDate("date"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
