import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileDataManager implements DataManager {
    private static final String FILE_PATH = "transactions.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void save(Transaction t, int userId) {
        // userId는 파일 저장에서는 사용하지 않음
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            String line = String.join(",",
                    t.getName(),
                    t.getType(),
                    String.valueOf(t.getAmount()),
                    t.getNote().replace(",", " "),
                    DATE_FORMAT.format(t.getDate()));
            pw.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Transaction> loadAll(int userId) {
        List<Transaction> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    Transaction t = new Transaction(
                            parts[0],
                            parts[1],
                            Double.parseDouble(parts[2]),
                            parts[3],
                            DATE_FORMAT.parse(parts[4])
                    );
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
