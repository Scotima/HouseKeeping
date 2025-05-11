import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class App {
    private JFrame frame;
    private JTextField idField;
    private JPasswordField passField;
    private JPanel currPanel;
    private JTextField nameInput;
    private JTextField amountInput;
    private JTextField searchInput;
    private JTable table;
    private JComboBox<String> typeInput;
    private List<String> categoryList = new ArrayList<>(Arrays.asList("Food", "Transport", "Shopping", "Etc"));
    private int loggedInUserId;

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

    public App() {
        initialize();
    }

    private void initialize() {
        TableData td = new TableData();
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImagePanel loginPanel = new ImagePanel(new ImageIcon(getClass().getResource("/theme.jpg")).getImage());
        ImagePanel tranPanel = new ImagePanel(new ImageIcon(getClass().getResource("/Activation.jpg")).getImage());
        ImagePanel sumPanel = new ImagePanel(new ImageIcon(getClass().getResource("/Activation.jpg")).getImage());

        currPanel = loginPanel;

        frame.setSize(loginPanel.getDim());
        frame.setPreferredSize(loginPanel.getDim());
        frame.getContentPane().setLayout(null);

        JButton tranBtn = new JButton("");
        tranBtn.setIcon(new ImageIcon(getClass().getResource("/Transaction.jpg")));
        tranBtn.setBounds(29, 182, 259, 40);
        tranBtn.setBorder(null);
        sumPanel.add(tranBtn);
        tranBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tranPanel.setVisible(true);
                sumPanel.setVisible(false);
            }
        });

        searchInput = new JTextField();
        searchInput.setFont(new Font("Tahoma", Font.PLAIN, 22));
        searchInput.setBounds(432, 76, 856, 40);
        sumPanel.add(searchInput);

        table = new JTable(td);
        table.setRowHeight(30);
        table.setFont(new Font("Sansserif", Font.BOLD, 15));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(337, 140, 1175, 467);
        sumPanel.add(scrollPane);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(92, 179, 255));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Sansserif", Font.BOLD, 15));

        searchInput.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String search = searchInput.getText();
                TableRowSorter<AbstractTableModel> trs = new TableRowSorter<>(td);
                table.setRowSorter(trs);
                trs.setRowFilter(RowFilter.regexFilter(search));
            }
        });

        frame.getContentPane().add(sumPanel);
        frame.getContentPane().add(tranPanel);
        frame.getContentPane().add(loginPanel);
        tranPanel.setVisible(false);
        sumPanel.setVisible(false);

        idField = new JTextField();
        idField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        idField.setBounds(1223, 311, 296, 43);
        loginPanel.add(idField);

        passField = new JPasswordField();
        passField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        passField.setBounds(1223, 391, 296, 43);
        loginPanel.add(passField);

        JButton logInBtn = new JButton("");
        logInBtn.setBorder(null);
        logInBtn.setBounds(1183, 467, 338, 38);
        logInBtn.setIcon(new ImageIcon(getClass().getResource("/button.jpg")));
        logInBtn.setPressedIcon(new ImageIcon(getClass().getResource("/btnClicked.jpg")));
        loginPanel.add(logInBtn);
        logInBtn.addActionListener(e -> switchPanel(sumPanel));
        
        JLabel searchName = new JLabel("Name");
        searchName.setFont(new Font("Tahoma", Font.PLAIN, 22));
        searchName.setBounds(346, 75, 74, 40);
        sumPanel.add(searchName);
        
        JComboBox categoryFilter = new JComboBox<>(new String[] {"All", "Food", "Transport", "Shopping", "ETC"});
        categoryFilter.setBounds(1329, 76, 183, 40);
        sumPanel.add(categoryFilter);
        categoryFilter.addActionListener(e -> {
            String selected = (String) categoryFilter.getSelectedItem();
            TableRowSorter sorter = new TableRowSorter<>(td);
            table.setRowSorter(sorter);

            if (selected.equals("All")) {
                sorter.setRowFilter(null);  // 전체 표시
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected, 1));  // 1 = Category 컬럼 index
            }
        });

        JButton signupBtn = new JButton("회원가입");
        signupBtn.setBounds(1183, 520, 338, 38);
        loginPanel.add(signupBtn);
        signupBtn.addActionListener(e -> showSignupPanel());

        nameInput = new JTextField();
        nameInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
        nameInput.setBounds(527, 123, 935, 49);
        tranPanel.add(nameInput);

        typeInput = new JComboBox<>(categoryList.toArray(new String[0]));
        typeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
        typeInput.setBounds(527, 203, 935, 49);
        tranPanel.add(typeInput);

        amountInput = new JTextField();
        amountInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
        amountInput.setBounds(527, 284, 935, 49);
        tranPanel.add(amountInput);

        JTextArea noteInput = new JTextArea();
        noteInput.setFont(new Font("Courier New", Font.PLAIN, 33));
        noteInput.setBounds(527, 370, 935, 60);
        tranPanel.add(noteInput);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateInput = new JSpinner(dateModel);
        dateInput.setEditor(new JSpinner.DateEditor(dateInput, "yyyy-MM-dd"));
        dateInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
        dateInput.setBounds(527, 450, 300, 49);
        tranPanel.add(dateInput);

        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeInput = new JSpinner(timeModel);
        timeInput.setEditor(new JSpinner.DateEditor(timeInput, "HH:mm:ss"));
        timeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
        timeInput.setBounds(527, 510, 300, 49);
        tranPanel.add(timeInput);

        JButton btnSubmit = new JButton("SUBMIT");
        btnSubmit.setFont(new Font("Tahoma", Font.PLAIN, 33));
        btnSubmit.setBounds(527, 572, 935, 71);
        tranPanel.add(btnSubmit);
        
        JButton sumbtn = new JButton("");
        sumbtn.setIcon(new ImageIcon(getClass().getResource("/summary.jpg")));
        sumbtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		td.refresh(); // 🔁 Summary 탭 진입 시 테이블 갱신
                sumPanel.setVisible(true);
                tranPanel.setVisible(false);
        	}
        });
        sumbtn.setBounds(30, 123, 258, 40);
        tranPanel.add(sumbtn);
        
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
        nameLabel.setBounds(319, 127, 192, 49);
        tranPanel.add(nameLabel);
        
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
        categoryLabel.setBounds(319, 203, 192, 49);
        tranPanel.add(categoryLabel);
        
        JLabel priceLabel = new JLabel("Price");
        priceLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
        priceLabel.setBounds(319, 284, 192, 49);
        tranPanel.add(priceLabel);

        btnSubmit.addActionListener(arg0 -> {
        	 String name = nameInput.getText();
        	    String type = (String) typeInput.getSelectedItem();
        	    double amount = Double.parseDouble(amountInput.getText());
        	    String note = noteInput.getText();

        	    Date selectedDate = (Date) dateInput.getValue();
        	    Date selectedTime = (Date) timeInput.getValue();

        	    LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        	    LocalTime localTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        	    LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
        	    Date fullDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        	    Transaction t = new Transaction(name, type, amount, note, fullDate);

        	    // 🔁 DataManager 인터페이스 기반 - 현재는 파일
        	    DataManager manager = new FileDataManager();
        	    manager.save(t, loggedInUserId);

        	    JOptionPane.showMessageDialog(null, "거래가 저장되었습니다.");
        	    //td.refresh();
        	    updateMonthlySummary(type, amount);
        });
    }

    private void switchPanel(JPanel target) {
        if (currPanel != null) currPanel.setVisible(false);
        target.setVisible(true);
        currPanel = target;
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
            JOptionPane.showMessageDialog(signupFrame, "[샘플] 사용자 " + username + "가 가입되었습니다.");
            signupFrame.dispose();
        });

        signupFrame.setVisible(true);
    }

    private void updateMonthlySummary(String type, double amount) {
        String month = java.time.LocalDate.now().getMonth().toString();
        try (FileWriter fw = new FileWriter("monthly_summary.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(month + "," + type + "," + amount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
