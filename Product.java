package project4;

public class Product {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private double price;
    private String seller;
    //private int storeID;

    public Product(String id, String name, String description, int quantity, double price, String seller) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.seller = seller;
        //this.storeID = storeID;
    }
/**
    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }
 */
    public String getSeller() {
        return seller;
    }

    public void setSeller(String name) {
        this.seller = seller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", id, name, description, quantity, price);
    }
}
