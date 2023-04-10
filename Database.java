package project4;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    private Connection con;

    /**
     * Constructor for Database class that connects to a Microsoft Access database.
     *
     * @param filepath the path to the Access database file
     * @return void
     */
    public Database(String filepath) {
        String url = "jdbc:ucanaccess:" + filepath;
        try {
            this.con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for searching the database for records matching a set of conditions.
     *
     * @param fields     the fields to retrieve from the database
     * @param conditions the conditions to use when searching for records
     * @return an ArrayList of Strings representing the records that matched the conditions
     */
    public ArrayList<String> searchDB(String fields, String conditions) {
        Statement st = null;
        ArrayList<String> output = new ArrayList<>();
        ResultSet rs;
        String result = new String();
        try {
            st = con.createStatement();
            if (conditions.isEmpty()) {
                rs = st.executeQuery("SELECT " + fields + " FROM Sellers INNER JOIN ((Markets INNER JOIN Products " +
                        "ON Markets.Store_ID = Products.Store_ID) INNER JOIN (Buyers INNER JOIN Cart ON Buyers.Id = " +
                        "Cart.Buyer_ID) ON Products.Product_ID = Cart.Product_ID) ON Sellers.Seller_ID = Markets.Seller_ID");
            } else {
                rs = st.executeQuery("SELECT " + fields + " FROM  Sellers INNER JOIN (Markets INNER JOIN Products " +
                        "ON Markets.Store_ID = Products.Store_ID) ON Sellers.Seller_ID = Markets.Seller_ID Where " + conditions);
            }
            while (rs.next()) {
                for (int i = 0; i < rs.getFetchSize(); i++) {
                    result = result + "," + rs.getString(i);
                }
                output.add(result);
            }
            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for adding a product to the database.
     *
     * @param name        the name of the product
     * @param store       the ID of the store where the product is sold
     * @param description a description of the product
     * @param quantity    the quantity of the product available for sale
     * @param price       the price of the product
     * @return void
     */
    public void addProduct(String name, int store, String description, int quantity, int price) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO PRODUCTS ( Product_name, Store_ID, Product_description, " +
                    "Quantity_available, price) Values ( \"" + name + "\", " + store + ", \"" + description + "\", "
                    + quantity + ", " + price + ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new market to the database with the given name and seller ID.
     *
     * @param name   the name of the market to add
     * @param seller the ID of the seller who owns the market
     */
    public void addMarket(String name, int seller) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO Markets (Store_name, Seller_ID) Values (\"" + name + "\", " + seller + ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new seller to the database with the given username, password, and name.
     *
     * @param username the username of the seller to add
     * @param password the password of the seller to add
     * @param name     the name of the seller to add
     */
    public void addSeller(String username, String password, String name) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO Sellers (Seller_Username, Seller_Password, Seller_name) Values (\""
                    + username + "\", \"" + password + "\", \"" + name + "\")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new buyer to the database with the given username, password, and name.
     *
     * @param username the username of the buyer to add
     * @param password the password of the buyer to add
     * @param name     the name of the buyer to add
     */
    public void addBuyer(String username, String password, String name) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO Buyers (Buyer_Username, Buyer_Password, Buyer_Name) Values (\""
                    + username + "\", \"" + password + "\", \"" + name + "\")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a record from the specified table with the given ID.
     *
     * @param table the name of the table to delete the record from
     * @param id    the ID of the record to delete
     */
    public void delete(String table, int id) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("DELETE FROM " + table + " WHERE " + table + ".id = " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

