package housekeeping.chart;

import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class ChartViewer extends JFrame {
    private int userId;

    public ChartViewer(int userId) {
        this.userId = userId;
        setTitle("지출 그래프");
        setSize(800, 700);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("카테고리별 지출", createPieAndBarPanel());
        tabbedPane.add("월별 지출 추세", createMonthlyExpensePanel());

        add(tabbedPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private JPanel createPieAndBarPanel() {
    	Map<String, Color> categoryColorMap = new HashMap<>();
        categoryColorMap.put("식비", new Color(173, 216, 230)); // 연한 파랑
        categoryColorMap.put("쇼핑", new Color(255, 182, 193)); // 연한 핑크
        categoryColorMap.put("교통", new Color(255, 255, 153)); // 연한 노랑
        categoryColorMap.put("여가", new Color(204, 255, 204)); // 연한 초록
        categoryColorMap.put("기타", new Color(221, 160, 221)); // 연보라
        categoryColorMap.put("공과금", new Color(255, 204, 204)); // 연한 연핑크
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        JComboBox<String> monthCombo = new JComboBox<>();
        topPanel.add(new JLabel("월 선택: "));
        topPanel.add(monthCombo);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel chartPanel = new JPanel(new GridLayout(2, 1));
        panel.add(chartPanel, BorderLayout.CENTER);

        Set<String> months = getAvailableMonths();
        for (String m : months) monthCombo.addItem(m);

        Runnable updateCharts = () -> {
            chartPanel.removeAll();
            String month = (String) monthCombo.getSelectedItem();
            Map<String, Double> categoryData = getCategoryExpenseByMonth(month);

            // Pie
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            categoryData.forEach(pieDataset::setValue);
            JFreeChart pie = ChartFactory.createPieChart("카테고리별 지출 (원형)", pieDataset, true, true, false);
            PiePlot piePlot = (PiePlot) pie.getPlot();
            pie.getTitle().setFont(new Font("Malgun Gothic", Font.BOLD, 18));
            pie.getLegend().setItemFont(new Font("Malgun Gothic", Font.PLAIN, 13));
            piePlot.setLabelFont(new Font("Malgun Gothic", Font.PLAIN, 14));
            piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
            ChartPanel pieChartPanel = new ChartPanel(pie);
            chartPanel.add(pieChartPanel);
            piePlot.setBackgroundPaint(Color.WHITE);

            // Bar
            DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
            categoryData.forEach((k, v) -> barDataset.addValue(v, "지출", k));
            JFreeChart bar = ChartFactory.createBarChart("카테고리별 지출 (막대)", "카테고리", "금액", barDataset);
            CategoryPlot barPlot = bar.getCategoryPlot();
            
            BarRenderer renderer = new BarRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    // 바 차트 색상 디버깅
                    String category = (String) barDataset.getColumnKey(column);
                    System.out.println("컬럼: " + category); // 콘솔 출력으로 확인
                    return categoryColorMap.getOrDefault(category, Color.PINK);
                }
            };
            renderer.setDrawBarOutline(false);
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter());
            barPlot.setRenderer(renderer);
            
            bar.getTitle().setFont(new Font("Malgun Gothic", Font.BOLD, 18));
            bar.getLegend().setItemFont(new Font("Malgun Gothic", Font.PLAIN, 13));
            barPlot.getDomainAxis().setLabelFont(new Font("Malgun Gothic", Font.PLAIN, 14));
            barPlot.getDomainAxis().setTickLabelFont(new Font("Malgun Gothic", Font.PLAIN, 13));
            barPlot.getRangeAxis().setLabelFont(new Font("Malgun Gothic" , Font.PLAIN, 14));
            ChartPanel barChartPanel = new ChartPanel(bar);
            chartPanel.add(barChartPanel);

            chartPanel.revalidate();
            chartPanel.repaint();
        };

        monthCombo.addActionListener(e -> updateCharts.run());
        if (!months.isEmpty()) {
            monthCombo.setSelectedIndex(0);
            updateCharts.run();
        }

        return panel;
    }

    private JPanel createMonthlyExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> data = getMonthlyTotalExpenses();
        data.forEach((month, total) -> dataset.addValue(total, "지출", month));

        JFreeChart chart = ChartFactory.createBarChart("월별 지출 추세", "월", "금액", dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        
     // ✅ Bar 렌더러 스타일 지정
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
        barRenderer.setSeriesPaint(0, new Color(135, 206, 250)); // 밝은 하늘색
        barRenderer.setBarPainter(new StandardBarPainter());
        barRenderer.setDrawBarOutline(false);
        barRenderer.setShadowVisible(false);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220)); // 연한 회색

        // 추세선 추가
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        data.forEach((month, total) -> lineDataset.addValue(total, "추세선", month));
        //CategoryPlot plot = chart.getCategoryPlot();
        plot.setDataset(1, lineDataset);
        plot.setRenderer(1, new org.jfree.chart.renderer.category.LineAndShapeRenderer());
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        
        chart.getTitle().setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        chart.getLegend().setItemFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        plot.getDomainAxis().setLabelFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        plot.getRangeAxis().setLabelFont(new Font("Malgun Gothic", Font.PLAIN, 14));

        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private Set<String> getAvailableMonths() {
        Set<String> months = new TreeSet<>(Collections.reverseOrder());
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
             PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT DATE_FORMAT(date, '%Y-%m') AS month FROM expenses WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) months.add(rs.getString("month"));
        } catch (Exception e) { e.printStackTrace(); }
        return months;
    }

    private Map<String, Double> getCategoryExpenseByMonth(String month) {
        Map<String, Double> map = new LinkedHashMap<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
        		PreparedStatement ps = conn.prepareStatement(
        			    "SELECT COALESCE(ec.name, '기타') AS name, SUM(e.amount) " +
        			    "FROM expenses e " +
        			    "JOIN expense_categories ec ON e.category_id = ec.id " +
        			    "WHERE e.user_id = ? AND DATE_FORMAT(e.date, '%Y-%m') = ? AND ec.name != '수입' " +
        			    "GROUP BY name"
        			);) {
            ps.setInt(1, userId);
            ps.setString(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    private Map<String, Double> getMonthlyTotalExpenses() {
        Map<String, Double> map = new LinkedHashMap<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
        		PreparedStatement ps = conn.prepareStatement(
        			    "SELECT DATE_FORMAT(e.date, '%Y-%m') AS month, SUM(e.amount) " +
        			    "FROM expenses e " +
        			    "JOIN expense_categories c ON e.category_id = c.id " +
        			    "WHERE e.user_id = ? AND c.name != '수입' " +
        			    "GROUP BY month ORDER BY month"
        			);) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
}