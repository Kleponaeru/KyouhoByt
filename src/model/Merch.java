package model;

public class Merch {
    private int merchID;
    private String merchName;
    private int merchPrice;
    private int merchStock;
    private String merchType;

    public Merch(int merchID, String merchName, int merchPrice, int merchStock, String merchType) {
        this.merchID = merchID;
        this.merchName = merchName;
        this.merchPrice = merchPrice;
        this.merchStock = merchStock;
        this.merchType = merchType;
    }

    public int getMerchID() { return merchID; }
    public void setMerchID(int merchID) { this.merchID = merchID; }

    public String getMerchName() { return merchName; }
    public void setMerchName(String merchName) { this.merchName = merchName; }

    public int getMerchPrice() { return merchPrice; }
    public void setMerchPrice(int merchPrice) { this.merchPrice = merchPrice; }

    public int getMerchStock() { return merchStock; }
    public void setMerchStock(int merchStock) { this.merchStock = merchStock; }

    public String getMerchType() { return merchType; }
    public void setMerchType(String merchType) { this.merchType = merchType; }
}