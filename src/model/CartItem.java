package model;

public class CartItem {
    private String merchName;
    private int merchPrice;
    private int quantity;
    private int merchID;

    public CartItem(String merchName, int merchPrice, int quantity, int merchID) {
        this.merchName = merchName;
        this.merchPrice = merchPrice;
        this.quantity = quantity;
        this.merchID = merchID;
    }

    public String getMerchName() {
        return merchName;
    }

    public int getMerchPrice() {
        return merchPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMerchID() {
        return merchID;
    }

    public int getTotal() {
        return merchPrice * quantity;
    }
}
