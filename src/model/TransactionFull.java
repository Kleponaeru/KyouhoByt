package model;

public class TransactionFull {
    private int transactionID;
    private String username;
    private String merchName;
    private int merchPrice;
    private int quantity;
    private String transactionDate;

    public TransactionFull(int transactionID, String username, String merchName, int merchPrice, int quantity, String transactionDate) {
        this.transactionID = transactionID;
        this.username = username;
        this.merchName = merchName;
        this.merchPrice = merchPrice;
        this.quantity = quantity;
        this.transactionDate = transactionDate;
    }

    public int getTransactionID() { return transactionID; }
    public String getUsername() { return username; }
    public String getMerchName() { return merchName; }
    public int getMerchPrice() { return merchPrice; }
    public int getQuantity() { return quantity; }
    public String getTransactionDate() { return transactionDate; }
}
