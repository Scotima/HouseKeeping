package housekeeping.ui;

import housekeeping.ImagePanel;
import housekeeping.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SummaryPanel extends ImagePanel {
    private static final long serialVersionUID = 1L;
    public JTextField searchInput;
    public JTable table;
    public JButton tranBtn;
    public JComboBox<String> categoryFilter;

    private AbstractTableModel tableModel;

    public SummaryPanel(Image image, AbstractTableModel tableModel, ActionListener onTranBtnClick) {
        super(image);
        this.tableModel = tableModel;
        setLayout(null);

        tranBtn = new JButton("");
        tranBtn.setBounds(29, 182, 259, 40);
        tranBtn.setBorder(null);
        tranBtn.setIcon(new ImageIcon(getClass().getResource("/Transaction.jpg")));
        tranBtn.addActionListener(onTranBtnClick);
        add(tranBtn);

        JLabel lblSearch = new JLabel("Search :");
        lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 22));
        lblSearch.setBounds(337, 76, 83, 40);
        add(lblSearch);

        searchInput = new JTextField();
        searchInput.setFont(new Font("Tahoma", Font.PLAIN, 22));
        searchInput.setBounds(432, 76, 880, 40);
        searchInput.setColumns(10);
        add(searchInput);

        // categoryFilter를 DB에서 동적으로 불러오기
        java.util.List<String> categories = new ArrayList<>();
        categories.add("All");
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM expense_categories")) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        categoryFilter = new JComboBox<>(categories.toArray(new String[0]));
        categoryFilter.setFont(new Font("Tahoma", Font.PLAIN, 22));
        categoryFilter.setBounds(1329, 76, 183, 40);
        categoryFilter.setBackground(Color.WHITE);
        add(categoryFilter);

        JPanel tp = new JPanel();
        tp.setBounds(337, 140, 1175, 467);
        tp.setOpaque(false);
        add(tp);

        table = new JTable(tableModel);
        table.setBounds(337, 140, 1155, 445);
        table.setRowHeight(30);
        table.setFont(new Font("Sansserif", Font.BOLD, 15));
        table.setPreferredScrollableViewportSize(new Dimension(1155, 430));
        tp.add(new JScrollPane(table));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(92, 179, 255));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Sansserif", Font.BOLD, 15));

        searchInput.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        categoryFilter.addActionListener(e -> applyFilters());
    }

    private void applyFilters() {
        String search = searchInput.getText();
        String selected = (String) categoryFilter.getSelectedItem();

        TableRowSorter<AbstractTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        if ((search == null || search.isEmpty()) && (selected == null || selected.equals("All"))) {
            sorter.setRowFilter(null);
        } else if (search != null && !search.isEmpty() && (selected == null || selected.equals("All"))) {
            sorter.setRowFilter(RowFilter.regexFilter(search));
        } else if ((search == null || search.isEmpty()) && selected != null) {
            sorter.setRowFilter(RowFilter.regexFilter(selected, 1)); // 1 = Category column
        } else {
            RowFilter<Object, Object> rf1 = RowFilter.regexFilter(search);
            RowFilter<Object, Object> rf2 = RowFilter.regexFilter(selected, 1);
            sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(rf1, rf2)));
        }
    }
}
