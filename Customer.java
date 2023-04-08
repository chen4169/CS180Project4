import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Customer {
    String username;
    String password;
    String id;  // COULD BE INT
    String name;
    ArrayList<Product> shoppingCart;

    ArrayList<Product> purchased;

    public Customer(String username, String password, String id, String name) {  // Constructor for if customer exists
        this.username = username;
        this.password = password;
        this.id = id;
        this.name = name;
    }

    public void close() {
        // TODO: This should write shopping cart and purchase history when called.  Read from the fields and write to the files
    }

    public void addAccount(String fileName) {  // FIXME -> add Account information to the database - could be easier to hard code the database file instead of using fileName parameter
        String info = String.format("%s,%s,%s,%s", this.id, this.username, this.password, this.name);

        // TODO: Should add the info string to the database

    }

    public void saveCart(String fileName) {  // FIXME -> Integrate Databases
        // Want to write the users cart into the cart file for database
        // Information stored in arraylist of products (shoppingCart field)
    }

    public void saveHistory(String fileName) {  // FIXME -> Integrate Databases
        // want to write the users history into a history file in database
        // Information stored in arraylist of products (purchased field)
    }

    public void addToCart(Product product) {
        this.shoppingCart.add(product);
    }

    public void setShoppingCart(ArrayList<Product> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ArrayList<Product> getShoppingCart() {
        return shoppingCart;
    }

    public void removeFromCart(Product product) {
        this.shoppingCart.remove(product);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addToHistory(Product product) {
        this.purchased.add(product);
    }

    public void setPurchased(ArrayList<Product> purchased) {
        this.purchased = purchased;
    }

    public ArrayList<Product> getPurchased() {
        return purchased;
    }

    public void removeFromHistory(Product product) {
        this.purchased.remove(product);
    }

}

class Product {
    private String storeID;
    private String id;
    private String name;
    private String description;
    private int quantity;
    private double price;
    private String seller;

    public Product(String id, String name, String description, int quantity, double price, String seller) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.seller = seller;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", id, name, storeID, description, quantity, price);
    }
}

