package housekeeping;

import java.util.Date;

public class Transaction {
    private String name;       // 사용자 이름
    private int categoryId;    // 카테고리 ID (DB 저장용)
    private String type;       // 카테고리 이름 (표시용)
    private double amount;     // 지출 금액
    private String note;       // 설명
    private Date date;         // 지출 일자

    // 생성자
    public Transaction(String name, int categoryId, String type, double amount, String note, Date date) {
        this.name = name;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.date = date;
    }

    // 기본 생성자
    public Transaction() {}

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction [name=" + name + ", categoryId=" + categoryId + ", type=" + type +
               ", amount=" + amount + ", note=" + note + ", date=" + date + "]";
    }
}   
