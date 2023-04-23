package Project5;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
}
