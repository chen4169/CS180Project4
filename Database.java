import java.sql.*;
import java.util.ArrayList;

public class Database {
    private Connection con;
    public Database(String filepath) {
       String url = "jdbc:ucanaccess://" + filepath;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            this.con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<String> searchDB (String fields, String conditions) {
        Statement st = null;
        ArrayList<String> output = new ArrayList<>();
        ResultSet rs;
        String result = new String();
        try {
            st = con.createStatement();
            if (conditions.isEmpty()) {
                rs = st.executeQuery("SELECT " + fields + " FROM Sellers INNER JOIN ((Markets INNER JOIN Products " +
                        "ON Markets.Store_ID = Products.Store_ID) INNER JOIN (Buyers INNER JOIN Cart ON Buyers.Id = " +
                        "Cart.Buyer_ID) ON Products.Product_ID = Cart.Product_ID) ON Sellers.Seller_ID = Markets.Seller_ID" );
            } else {
                rs = st.executeQuery("SELECT " + fields + " FROM  Sellers INNER JOIN (Markets INNER JOIN Products " +
                        "ON Markets.Store_ID = Products.Store_ID) ON Sellers.Seller_ID = Markets.Seller_ID Where " + conditions);
            }
            while (rs.next()) {
                for (int i = 0; i < rs.getFetchSize(); i++) {
                    result =  result + "," + rs.getString(i);
                }
                output.add(result);
            }
            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addProduct (String name, int store, String description, int quantity, int price) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO PRODUCTS ( Product_name, Store_ID, Product_description, " +
                    "Quantity_available, price) Values ( \"" + name + "\", " + store + ", \"" + description + "\", "
                    + quantity + ", "  + price + ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addMarket (String name, int seller) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO Markets ( Store_name, Seller_ID) Values ( \"" + name + "\", " + seller + ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addSeller (String username, String password, String name) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO Sellers ( Seller_Username, Seller_Password, Seller_name) Values ( \""
                    + username + "\", \"" + password + "\", \"" + name +"\")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addBuyer (String username, String password, String name) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("INSERT INTO Buyers ( Buyer_Username, Buyer_Password, Buyer_Name) Values ( \""
                    + username + "\", \"" + password + "\", \"" + name +"\")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void delete (String table,String id) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("DELETE FROM "+  table + "WHERE " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void Update (String table, String quantity, String ID) {
        Statement st = null;
        String result = new String();
        try {
            st.executeQuery("Update" + table + "Set Quantity_available = " + quantity +"Where Product_ID = "+ ID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
