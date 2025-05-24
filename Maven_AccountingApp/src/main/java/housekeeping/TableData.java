package housekeeping;

import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TableData extends AbstractTableModel {
    private final String[] columnNames = {"이름", "카테고리", "금액", "설명", "날짜"};
    private List<Transaction> list;
    
    public TableData() {
    	 
        //updateList(userId);
        //System.out.println("[App] loggedInUserId = " + userId);
    	  this.list = new ArrayList<>();
    }

    public void updateList(int userId) {
    	System.out.println("[App] loggedInUserId = " + userId);
        this.list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect()) {
            // category_id와 category_name 모두 SELECT
            String query = "SELECT e.amount, e.description, e.date, u.username, " +
                    "c.id AS category_id, c.name AS category_name " +
                    "FROM expenses e " +
                    "JOIN users u ON e.user_id = u.id " +
                    "JOIN expense_categories c ON e.category_id = c.id " +
                    "WHERE e.user_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId); // 현재 사용자 ID 설정
                System.out.println("쿼리 실행 시작...");
                try (ResultSet rs = pstmt.executeQuery()) {
                	System.out.println("ResultSet 실행됨");
                    while (rs.next()) {
                    	System.out.println(">> 한 줄 발견!");
                        int categoryId = rs.getInt("category_id");
                        String categoryName = rs.getString("category_name");
                        double amount = rs.getDouble("amount");
                        String description = rs.getString("description");
                        Date date = rs.getDate("date");
                        String username = rs.getString("username");

                        // Transaction 객체 생성 및 설정
                        Transaction t = new TransactionBuilder(new Transaction())
                                .name(username)
                                .categoryId(categoryId)
                                .type(categoryName)
                                .amount(amount)
                                .note(description)
                                .date(date)
                                .transaction();

                        list.add(t);
                        
                        System.out.println("불러온 거래:");
                        System.out.println("  이름: " + username);
                        System.out.println("  카테고리명: " + categoryName);
                        System.out.println("  금액: " + amount);
                        System.out.println("  설명: " + description);
                        System.out.println("  날짜: " + date);
                        

                  
                    }
                    
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        fireTableDataChanged(); 
        
      
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction t = list.get(rowIndex);
        switch (columnIndex) {
            case 0: return t.getName();
            case 1: return t.getType(); // 카테고리 이름
            case 2: return t.getAmount();
            case 3: return t.getNote();
            case 4: return t.getDate();
            default: return null;
        }
    }
    
    public Transaction getTransactionAt(int rowIndex) {
        if (list != null && rowIndex >= 0 && rowIndex < list.size()) {
            return list.get(rowIndex);
        }
        return null;
    }
}  
