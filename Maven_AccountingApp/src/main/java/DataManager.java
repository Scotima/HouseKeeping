import java.util.List;

public interface DataManager {
	void save(Transaction t, int userId);
    List<Transaction> loadAll(int userId);
}
