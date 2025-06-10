package housekeeping.ui;

import housekeeping.ImagePanel;
import housekeeping.App;
import housekeeping.DatabaseConnection;
import housekeeping.chart.ChartViewer;
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
import housekeeping.PDFReportGenerator;
import housekeeping.Transaction;
import housekeeping.chart.ChartViewer;
import housekeeping.TableData;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

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
        tranBtn.setIcon(new ImageIcon(getClass().getResource("/record.jpg")));
        tranBtn.addActionListener(onTranBtnClick);
        add(tranBtn);

        JLabel lblSearch = new JLabel("Search :");
        lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 22));
        lblSearch.setBounds(345, 76, 83, 40);
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
        
        
        
        //pdf 생성 코드
        JButton reportBtn = new JButton("보고서 만들기");
        reportBtn.setBackground(new Color(234, 226, 219));
        reportBtn.setBounds(1300, 620, 200, 40); // 원하는 위치로 조정
        reportBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        reportBtn.setEnabled(false); // 처음엔 비활성화
        add(reportBtn);

        // 테이블 클릭 시 버튼 활성화
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    reportBtn.setEnabled(true);
                }
            }
        });

        // 버튼 클릭 시 PDF 생성
        reportBtn.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "거래를 한 개 이상 선택하세요.");
                return;
            }
            List<Transaction> selectedTransactions = new ArrayList<>();
            TableData td = (TableData) table.getModel();
         
            
            for(int row : selectedRows) {
            	int modelRow = table.convertRowIndexToModel(row);
            	selectedTransactions.add(td.getTransactionAt(modelRow));
            }

            String[] options = {"회사 보고서", "대학교 보고서", "개인 기록용"};
            String format = (String) JOptionPane.showInputDialog(
                this,
                "보고서 형식을 선택하세요",
                "보고서 양식 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
            );

            if (format != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("PDF 보고서 저장 위치 선택");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!path.endsWith(".pdf")) path += ".pdf";
                    
                    //영수증 이미지 첨부 선택
                    
                    List<String> imagePaths = new ArrayList<>();
                    int attachTmg = JOptionPane.showConfirmDialog(this, "영수증을 첨부하시겠습니까?", "영수증 올리기", JOptionPane.YES_NO_OPTION);
                    if(attachTmg == JOptionPane.YES_OPTION) {
                    	JFileChooser imageChooser = new JFileChooser();
                    	imageChooser.setMultiSelectionEnabled(true);
                    	imageChooser.setDialogTitle("첨부할 이미지 선택 (여러개 가능)");
                    	int imageResult = imageChooser.showOpenDialog(this);
                    	if(imageResult == JFileChooser.APPROVE_OPTION) {
                    		for(File img : imageChooser.getSelectedFiles()) {
                    			imagePaths.add(img.getAbsolutePath());
                    		}
                    	}
                    	
                    }
                    
                    try {
                        PDFReportGenerator.saveToPDF(selectedTransactions, path, imagePaths);
                        JOptionPane.showMessageDialog(this, "PDF 보고서가 저장되었습니다.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "보고서 생성 중 오류: " + ex.getMessage());
                    }
                }
            }
        });
        
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        
        //지출 그래프 버튼
        JButton chartBtn = new JButton("");
        chartBtn.setBounds(370, 620, 259, 40);
        chartBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        chartBtn.setIcon(new ImageIcon(getClass().getResource("/viewgraph.jpg")));
        add(chartBtn);
        
        chartBtn.addActionListener(e -> {
            new ChartViewer(App.getLoggedInUserId()); // 또는 현재 userId
        });
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
    
    public void refreshTable() {
        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
        table.repaint();
    }
    
   
}
