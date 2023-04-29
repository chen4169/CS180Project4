
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * This class contains the methods to interact with the database file
 * Only the Server will use these methods
 * @Version 2023/4/28 1.4
 * @author Libin Chen
 */

/**
 * Important: the file path 'url' must be in the form 'jdbc:ucanaccess://path1//path2//path3//path4//Database1.accdb'
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
                String userType = rs.getString(2).startsWith("B") ? "S" : "C";
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
     * separated by a ",". For example: ["1,100,Apple,1,Walmart,5,2,0.99", "2,101,Banana,2,Target,5,3,1.25", "4,103,Carrot,4,Kroger,5,4,0.75"]
     * @param buyerID the ID of the Customer to be searched
     * @return a string array contains the desired information
     * @throws SQLException the call function need to handle this exception
     */
    public String[] searchPurchaseHistoryByBuyerID(int buyerID) {
        String[] purchaseHistory = new String[0];
        String query = "SELECT * FROM PurchaseHistory WHERE Buyer_ID=" + buyerID;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String orderID = rs.getString("Order_ID");
                String productID = rs.getString("Product_ID");
                String productName = rs.getString("Product_Name");
                String storeID = rs.getString("Store_ID");
                String storeName = rs.getString("Store_Name");
                String buyerIDStr = rs.getString("Buyer_ID");
                String quantity = rs.getString("Quantity");
                String price = rs.getString("Product_Price");
                String purchaseInfo = orderID + "," + productID + "," + productName + "," + storeID + "," +
                        storeName + "," + buyerIDStr + "," + quantity + "," + price;
                purchaseHistory = Arrays.copyOf(purchaseHistory, purchaseHistory.length + 1);
                purchaseHistory[purchaseHistory.length - 1] = purchaseInfo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return purchaseHistory;
        }
        return purchaseHistory;
    }

    /**
     * This method will take a string to search any product that contains this string
     * and return them as an ArrayList in which each element is separated by a ","
     * @param searchWord the word used for searching matched products
     * @return an ArrayList that contains the matched result
     */
    public ArrayList<String> searchProducts(String searchWord) {
        ArrayList<String> result = new ArrayList<>();

        try {
            // create the SQL statement
            String query = "SELECT * FROM Products WHERE LOWER(Product_name) LIKE LOWER(?) OR LOWER(Product_description) LIKE LOWER(?)";
            PreparedStatement ps = con.prepareStatement(query);

            // set the search keyword for the prepared statement
            ps.setString(1, "%" + searchWord.toLowerCase() + "%");
            ps.setString(2, "%" + searchWord.toLowerCase() + "%");

            // execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // iterate through the results and add them to the result ArrayList
            while (rs.next()) {
                String productID = Integer.toString(rs.getInt("Product_ID"));
                String productName = rs.getString("Product_name");
                String storeID = Integer.toString(rs.getInt("Store_ID"));
                String productDescription = rs.getString("Product_description");
                String quantityAvailable = Integer.toString(rs.getInt("Quantity_available"));
                String price = Double.toString(rs.getDouble("Price"));

                result.add(productID + "," + productName + "," + storeID + "," + productDescription + "," + quantityAvailable + "," + price);
            }

        } catch (SQLException e) {
            // print the error message to the console and return an empty ArrayList
            System.err.println("Error searching for products: " + e.getMessage());
            return new ArrayList<String>();
        }

        // return the result ArrayList
        return result;
    }

    /**
     * Searches through all products in the Products table of the database and returns a string
     * in which each element is separated by a ",".
     * @return A string, where each element separated by a "@" represents a row in the Products table
     */
    public String listAllProducts() {
        ArrayList<String> result = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Products");
            while (rs.next()) {
                String productId = rs.getString("Product_ID");
                String productName = rs.getString("Product_name");
                String storeId = rs.getString("Store_ID");
                String productDesc = rs.getString("Product_description");
                String quantityAvail = rs.getString("Quantity_available");
                String price = rs.getString("Price");

                String product = productId + "," + productName + "," + storeId + "," + productDesc + "," + quantityAvail + "," + price;
                result.add(product);
            }
            return String.join("@", result);
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
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

}
