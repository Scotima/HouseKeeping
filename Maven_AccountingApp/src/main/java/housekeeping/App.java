package housekeeping;

import housekeeping.ui.LoginPanel;
import housekeeping.ui.SummaryPanel;
import housekeeping.ui.TransactionPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionListener;

public class App {
    private JFrame frame;
    private JPanel currPanel;
    private static int loggedInUserId;

    private List<String> categoryList = new ArrayList<>();
    private Map<String, Integer> categoryMap = new HashMap<>();

    // 패널들을 멤버 변수로 분리
    private SummaryPanel sumPanel;
    private TransactionPanel tranPanel;
    private LoginPanel loginPanel;
    private TableData td = new TableData();
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                App window = new App();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void loadCategoryMap() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM expense_categories")) {

            while (rs.next()) {
                categoryMap.put(rs.getString("name"), rs.getInt("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public App() {
    	loadCategoryMap(); 
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // 카테고리 불러오기
        loadExpenseCategories();
        

        // 패널들 초기화
        loginPanel = new LoginPanel(
        	    new ImageIcon(getClass().getResource("/theme.jpg")).getImage(),
        	    e -> handleLogin(loginPanel, sumPanel),
        	    e -> showSignupPanel()
        	);
        loginPanel.logInBtn.setText("로그인");

        	tranPanel = new TransactionPanel(
        	    new ImageIcon(getClass().getResource("/Activation.jpg")).getImage(),
        	    categoryList.toArray(new String[0]),
        	    e -> {submitTransaction(tranPanel);
        	    	  td.updateList(loggedInUserId);
        	    	  sumPanel.refreshTable();
        	    	  switchPanel(sumPanel);},
        	    e -> switchPanel(sumPanel)
        	);
        	tranPanel.noteInput.setFont(new Font("한컴 고딕", Font.PLAIN, 33));
        	tranPanel.amountInput.setFont(new Font("맑은 고딕", Font.PLAIN, 33));
        	tranPanel.nameInput.setFont(new Font("맑은 고딕", Font.PLAIN, 33));
        	tranPanel.submitButton.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        		}
        	});
        	tranPanel.typeInput.setFont(new Font("맑은 고딕", Font.PLAIN, 33));

        	sumPanel = new SummaryPanel(
        	    new ImageIcon(getClass().getResource("/Activation.jpg")).getImage(),
        	    td,
        	    e -> switchPanel(tranPanel)
        	);
        	sumPanel.table.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        	sumPanel.categoryFilter.setFont(new Font("맑은 고딕", Font.PLAIN, 22));

        // 패널 초기 설정
        currPanel = loginPanel;
        frame.setSize(loginPanel.getDim());
        frame.setPreferredSize(loginPanel.getDim());
        
        frame.getContentPane().add(tranPanel);
        frame.getContentPane().add(sumPanel);
        frame.getContentPane().add(loginPanel);

        loginPanel.setVisible(true);
        tranPanel.setVisible(false);
        sumPanel.setVisible(false);
        
        frame.setVisible(true);
    }

    private void switchPanel(JPanel newPanel) {
        currPanel.setVisible(false);
        newPanel.setVisible(true);
        currPanel = newPanel;
    }

    private void handleLogin(LoginPanel loginPanel, SummaryPanel sumPanel) {
        String inputId = loginPanel.getUsername();
        String inputPass = loginPanel.getPassword();
        String hashedPass = hashPassword(inputPass);

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            ps.setString(1, inputId);
            ps.setString(2, hashedPass);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("id");
                td.updateList(loggedInUserId);
                switchPanel(sumPanel);
            } else {
                JOptionPane.showMessageDialog(null, "로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "DB 오류: " + ex.getMessage());
        }
    }

    private void submitTransaction(TransactionPanel tranPanel) {
        String name = tranPanel.nameInput.getText();
        String type = (String) tranPanel.typeInput.getSelectedItem();
        Integer categoryId = categoryMap.get(type);
        String amount = tranPanel.amountInput.getText();
        String note = tranPanel.noteInput.getText();

        java.util.Date selectedDate = (java.util.Date) tranPanel.dateInput.getValue();
        java.util.Date selectedTime = (java.util.Date) tranPanel.timeInput.getValue();

        LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        
        if (categoryId == null) {
            JOptionPane.showMessageDialog(null, "잘못된 카테고리입니다.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO expenses (user_id, category_id, amount, description, date) VALUES (?, ?, ?, ?, ?)")) {

            ps.setInt(1, loggedInUserId);
            ps.setInt(2, categoryId);
            ps.setBigDecimal(3, new BigDecimal(amount));
            ps.setString(4, note);
            ps.setTimestamp(5, timestamp);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "거래가 추가되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "거래 추가 실패: " + e.getMessage());
        }
    }

    private void loadExpenseCategories() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM expense_categories")) {
            while (rs.next()) {
                categoryList.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showSignupPanel() {
        JFrame signupFrame = new JFrame("회원가입");
        signupFrame.setSize(400, 300);
        signupFrame.getContentPane().setLayout(null);

        JLabel userLabel = new JLabel("아이디:");
        userLabel.setBounds(50, 50, 80, 25);
        signupFrame.getContentPane().add(userLabel);

        JTextField userText = new JTextField();
        userText.setBounds(150, 50, 160, 25);
        signupFrame.getContentPane().add(userText);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(50, 100, 80, 25);
        signupFrame.getContentPane().add(passwordLabel);

        JPasswordField passwordText = new JPasswordField();
        passwordText.setBounds(150, 100, 160, 25);
        signupFrame.getContentPane().add(passwordText);

        JButton signupButton = new JButton("가입하기");
        signupButton.setBounds(150, 150, 100, 30);
        signupFrame.getContentPane().add(signupButton);

        signupButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            String hashedPassword = hashPassword(password);

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
                ps.setString(1, username);
                ps.setString(2, hashedPassword);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(signupFrame, "회원가입 성공!");
                signupFrame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(signupFrame, "회원가입 실패: " + ex.getMessage());
            }
        });

        signupFrame.setVisible(true);
    }
    
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }
}    