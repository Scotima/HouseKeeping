import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableData extends AbstractTableModel {
    private List<Transaction> list;
    private final String[] headers = {"Name", "Category", "Amount", "Description", "Date"};
    private int userId;
    private boolean useDatabase;

    public TableData() {
        this.useDatabase = false;
        updateList();
    }

    public TableData(int userId) {
        this.userId = userId;
        this.useDatabase = true;
        updateList();
    }

    public void updateList() {
        list = new ArrayList<>();
        if (useDatabase) {
            try (Connection conn = DatabaseConnection.connect()) {
                String query = "SELECT e.category, e.amount, e.description, e.date, u.username " +
                        "FROM expenses e " +
                        "JOIN users u ON e.user_id = u.id " +
                        "WHERE e.user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, userId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            String category = rs.getString("category");
                            double amount = rs.getDouble("amount");
                            String description = rs.getString("description");
                            Date date = rs.getDate("date");
                            String username = rs.getString("username");

                            Transaction t = new Transaction();
                            t.setName(username);
                            t.setType(category);
                            t.setAmount(amount);
                            t.setNote(description);
                            t.setDate(date);

                            list.add(t);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
        	//나중에 파일 대신 DB연동으로 전환.
            FileDataManager manager = new FileDataManager();
            list = manager.loadAll(userId);
        }
    }

    @Override
    public int getRowCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    
    public Object getValueAt(int row, int column) {
        if (list == null || row >= list.size()) return null;
        Transaction t = list.get(row);
        switch (column) {
            case 0: return t.getName();
            case 1: return t.getType();
            case 2: return t.getAmount();
            case 3: return t.getNote();
            case 4: return t.getDate();
            default: return null;
        }
    }


    @Override
    public String getColumnName(int column) {
        return headers[column];
    }

    public void refresh() {
        updateList();
        fireTableDataChanged();
    }
} 
