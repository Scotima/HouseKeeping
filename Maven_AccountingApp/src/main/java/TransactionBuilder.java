import java.util.Date;

public class TransactionBuilder {
	private Transaction t;

	// 생성자
	public TransactionBuilder(Transaction t) {
		this.t = t;
	}

	// name 설정
	public TransactionBuilder name(String name) {
		t.setName(name);
		return this;
	}

	// type 설정
	public TransactionBuilder type(String type) {
		t.setType(type);
		return this;
	}

	// amount 설정
	public TransactionBuilder amount(double amount) {
		t.setAmount(amount);
		return this;
	}

	// note 설정
	public TransactionBuilder note(String note) {
		t.setNote(note);
		return this;
	}

	// date 설정
	public TransactionBuilder date(Date date) {
		t.setDate(date);
		return this;
	}

	// 완성된 Transaction 객체 반환
	public Transaction transaction() {
		return t;
	}
}
