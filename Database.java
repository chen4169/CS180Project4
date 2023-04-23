package Project5;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * This class contains the methods to interact with the database file
 * Only the Server will use these methods
 * @Version 2023/4/23 1.2
 * @author Libin Chen
 */

/**
 * Important: the file path 'url' must be in the form 'jdbc:ucanaccess://C://path1//path2//path3//path4//Database1.accdb'
 */
public class Database {
    private Connection con;
    private String sellerCounter;
    private String buyerCounter;

    /**
     * This method will create the connection with the database file by using filepath
     * @param filepath A string represent the file path of the database file
     */
    public Database(String filepath) {
        String url = "jdbc:ucanaccess://" + filepath;
        try {
            this.con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will search Sellers and Buyers table in the database
     * and return a string of all the information asociated with the username
     * @param username the username of an account
     * @return A string that contains all the information of an acccount
     */
    public String getUserData(String username) {
        String query = "SELECT * FROM Buyers WHERE Buyer_Username = ? UNION SELECT * FROM Sellers WHERE Seller_Username = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String userType = rs.getString(2).startsWith("B") ? "C" : "S";
                return userType + rs.getInt(1) + "," + rs.getString(2) + "," + rs.getString(3) + "," + rs.getString(4);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while getting user data: " + e.getMessage());
            return "";
        }
        return "";
    }

    /**
     * This method will add new user information to the database file by
     * taking a string leading by "S" or "C" and separated by a ","
     * @param request a string like "Susername,password,trueName" or "Cusername,password,trueName"
     * @return a String message
     */
    public String addUserData(String request) {
        String[] userData = request.split(",");
        String userType = userData[0].substring(0, 1);
        String username = userData[0].substring(1);
        String password = userData[1];
        String trueName = userData[2];

        try {
            Statement stmt = con.createStatement();
            String query;
            if (userType.equals("S")) {
                query = "INSERT INTO Sellers (Seller_Username, Seller_Password, Seller_Name) " +
                        "VALUES ('" + username + "', '" + password + "', '" + trueName + "')";
            } else if (userType.equals("C")) {
                query = "INSERT INTO Buyers (Buyer_Username, Buyer_Password, Buyer_Name) " +
                        "VALUES ('" + username + "', '" + password + "', '" + trueName + "')";
            } else {
                return "Invalid user type";
            }
            int numRows = stmt.executeUpdate(query);
            if (numRows > 0) {
                return "User data added successfully";
            } else {
                return "Failed to add user data";
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return "Failed to add user data";
        }
    }

    /**
     * This method will search the purchase history of a certain costomer by the ID
     * and return a string array contains "OrderID,Product_ID,Product_Name,Store_ID,Store_Name,Buyer_ID,Quantity,Product_Price"
     * separated by a ","
     * @param buyerID the ID of the Customer to be searched
     * @return a string array contains the desired information
     * @throws SQLException the call function need to handle this exception
     */
    public String[] searchPurchaseHistoryByBuyerID(int buyerID) {
        ArrayList<String> purchaseHistoryList = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM PurchaseHistory WHERE Buyer_ID=" + buyerID;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int orderID = rs.getInt("OrderID");
                int productID = rs.getInt("Product_ID");
                String productName = rs.getString("Product_Name");
                int storeID = rs.getInt("Store_ID");
                String storeName = rs.getString("Store_Name");
                int quantity = rs.getInt("Quantity");
                double price = rs.getDouble("Product_Price");
                String row = orderID + "," + productID + "," + productName + "," + storeID + "," + storeName + "," + buyerID + "," + quantity + "," + price;
                purchaseHistoryList.add(row);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        String[] purchaseHistory = new String[purchaseHistoryList.size()];
        return purchaseHistoryList.toArray(purchaseHistory);
    }



    public String searchBuyerData(String username) {
        String buyerInfo = "";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Buyers WHERE Buyer_Username = '" + username + "'");

            if (rs.next()) {
                String buyerId = rs.getString("id");
                String buyerUsername = rs.getString("Buyer_Username");
                String buyerPassword = rs.getString("Buyer_Password");
                String buyerName = rs.getString("Buyer_Name");
                buyerInfo = String.join(",", buyerId, buyerUsername, buyerPassword, buyerName);
                System.out.println("Buyer information: " + buyerInfo);
            } else {
                System.out.println("No buyer found with the given username.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buyerInfo;
    }

    public String searchSellerData(String username) {
        String sellerInfo = "";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Sellers WHERE Seller_Username = '" + username + "'");

            if (rs.next()) {
                String sellerId = rs.getString("Seller_ID");
                String sellerUsername = rs.getString("Seller_Username");
                String sellerPassword = rs.getString("Seller_Password");
                String sellerName = rs.getString("Seller_Name");
                sellerInfo = String.join(",", sellerId, sellerUsername, sellerPassword, sellerName);
                System.out.println("Seller information: " + sellerInfo);
            } else {
                System.out.println("No seller found with the given username.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sellerInfo;
    }

    public String findMasSellerID() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(Seller_ID) AS max_id FROM Sellers");

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                sellerCounter = String.valueOf(maxId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sellerCounter;
    }

    public String findBuyerCounter() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(ID) AS max_id FROM Buyers");

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                buyerCounter = String.valueOf(maxId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buyerCounter;
    }

    public ArrayList<String> searchProduct() {
        ArrayList<String> productList = new ArrayList<String>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Products");

            while (rs.next()) {
                String productId = rs.getString("Product_ID");
                String productName = rs.getString("Product_name");
                String storeId = rs.getString("Store_ID");
                String productDesc = rs.getString("Product_description");
                int quantityAvailable = rs.getInt("Quantity_available");
                double price = rs.getDouble("Price");

                String productInfo = String.join(",", productId, productName, storeId, productDesc, Integer.toString(quantityAvailable), Double.toString(price));
                productList.add(productInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public ArrayList<String> searchCart(String buyerId) {
        ArrayList<String> cartData = new ArrayList<String>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Cart WHERE Buyer_Id = '" + buyerId + "'");

            while (rs.next()) {
                String cartId = rs.getString("Id");
                String productId = rs.getString("Product_ID");
                String cartInfo = String.join(",", cartId, buyerId, productId);
                cartData.add(cartInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartData;
    }

    public ArrayList<String> loadMarkets() {
        ArrayList<String> marketData = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Markets");
            while (rs.next()) {
                String storeId = rs.getString("Store_ID");
                String storeName = rs.getString("Store_name");
                String sellerId = rs.getString("Seller_ID");
                String marketInfo = String.join(",", storeId, storeName, sellerId);
                marketData.add(marketInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marketData;
    }

    public void addToCart(String buyerID, String productID) {
        try {
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO Cart (Buyer_ID, Product_ID) VALUES ('" + buyerID + "', '" + productID + "')";
            stmt.executeUpdate(sql);
            System.out.println("Product added to cart successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void emptyCart (){
        try {
            Statement stmt = con.createStatement();
            String sql = "TRUNCATE TABLE Cart";
            stmt.executeUpdate(sql);
            System.out.println("Cart table emptied successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPurchaseHistory(String productID, String storeID, int quantity, String buyerID, double price) {
        try {
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO PurchaceHistory (Product_ID, Store_ID, Quantity, Buyer_ID, Price) VALUES ('" + productID + "', '" + storeID + "', '" + quantity + "', '" + buyerID + "', '" + price + "')";
            stmt.executeUpdate(sql);
            System.out.println("Purchase history added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateProductQuantity(String productId, int newQuantity) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Products WHERE Product_ID = '" + productId + "'");

            if (rs.next()) {
                int currentQuantity = rs.getInt("Quantity_available");
                int updatedQuantity = currentQuantity - newQuantity;
                if (updatedQuantity < 0) {
                    return false;
                } else {
                    String sql = "UPDATE Products SET Quantity_available = " + updatedQuantity + " WHERE Product_ID = '" + productId + "'";
                    stmt.executeUpdate(sql);
                    System.out.println("Product quantity updated successfully.");
                    return true;
                }
            } else {
                System.out.println("Product not found.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> searchProductsByMarket(String marketChoice) {
        ArrayList<String> productData = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Products WHERE Store_ID='" + marketChoice + "'");
            while (rs.next()) {
                String productInfo = String.join(",", rs.getString("Product_ID"), rs.getString("Product_name"), rs.getString("Store_ID"), rs.getString("Product_description"), rs.getString("Quantity_available"), rs.getString("Price"));
                productData.add(productInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productData;
    }

    public ArrayList<String> searchMarketsBySellerId(String sellerId) {
        ArrayList<String> marketData = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM Markets WHERE Seller_ID='" + sellerId + "'";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String storeId = rs.getString("Store_ID");
                String storeName = rs.getString("Store_name");
                String sellerID = rs.getString("Seller_ID");

                String marketInfo = String.join(",", storeId, storeName, sellerID);
                marketData.add(marketInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marketData;
    }

    public void removeProduct(int productIndex) {
        try {
            Statement stmt = con.createStatement();
            String sql = "DELETE FROM Products WHERE Product_ID = " + productIndex;
            int rowsAffected = stmt.executeUpdate(sql);
            if (rowsAffected == 0) {
                System.out.println("No product found with ID " + productIndex);
            } else {
                System.out.println("Product removed successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProductBySeller(String productId, String productName, String storeId, String productDescription, int productQuantity, double productPrice) {
        try {
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO Products (Product_ID, Product_name, Store_ID, Product_description, Quantity_available, Price) VALUES ('" + productId + "', '" + productName + "', '" + storeId + "', '" + productDescription + "', " + productQuantity + ", " + productPrice + ")";
            stmt.executeUpdate(sql);
            System.out.println("Product added successfully, want to continue editing stores?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getMaxProductId() {
        String maxProductId = "";
        try {
            Statement stmt = con.createStatement();
            String sql = "SELECT MAX(Product_ID) FROM Products";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                maxProductId = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxProductId;
    }

    public void searchPurchaceHistoryByStoreId(String storeId) {
        try {
            String query = "SELECT * FROM PurchaceHistory WHERE Store_ID = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, storeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                while (resultSet.next()) {
                    System.out.println("ID: " + resultSet.getInt("ID") + ", Product_ID: " + resultSet.getInt("Product_ID") + ", Store_ID: " + resultSet.getInt("Store_ID") + ", Quantity: " + resultSet.getInt("Quantity") + ", Buyer_ID: " + resultSet.getInt("Buyer_ID") + ", Price: " + resultSet.getDouble("Price"));
                }
            } else {
                System.out.println("No purchase history found for the given store ID: " + resultSet.getInt("Store_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void addSeller(String sellerID, String username, String password, String name) {
        try {
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO Sellers (Seller_ID, Seller_Username, Seller_Password, Seller_Name) VALUES ('" + sellerID + "', '" + username + "', '" + password + "', '" + name + "')";
            stmt.executeUpdate(sql);
            System.out.println("Seller added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBuyer(String id, String username, String password, String name) {
        try {
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO Buyers (ID, Buyer_Username, Buyer_Password, Buyer_Name) VALUES ('" + id + "', '" + username + "', '" + password + "', '" + name + "')";
            stmt.executeUpdate(sql);
            System.out.println("Buyer added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
