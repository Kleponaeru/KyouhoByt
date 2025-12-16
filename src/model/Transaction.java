package model;

public class Transaction {
    private int transactionID;
    private String username;
    private String transactionDate;

    public Transaction(int transactionID, String username, String transactionDate) {
        this.transactionID = transactionID;
        this.username = username;
        this.transactionDate = transactionDate;
    }

    public int getTransactionID() { return transactionID; }
    public void setTransactionID(int transactionID) { this.transactionID = transactionID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }
}
