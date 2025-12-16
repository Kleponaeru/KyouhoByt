package model;

public class TransactionDetail {
    private String merchName;
    private int merchPrice;
    private int quantity;

    public TransactionDetail(String merchName, int merchPrice, int quantity) {
        this.merchName = merchName;
        this.merchPrice = merchPrice;
        this.quantity = quantity;
    }

    public String getMerchName() { return merchName; }
    public void setMerchName(String merchName) { this.merchName = merchName; }

    public int getMerchPrice() { return merchPrice; }
    public void setMerchPrice(int merchPrice) { this.merchPrice = merchPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getTotal() {
        return merchPrice * quantity;
    }
}