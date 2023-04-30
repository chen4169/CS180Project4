import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * This class contains the methods to interact with the database file
 * Only the Server will use these methods
 * @Version 2023/4/30 1.6
 * @author Libin Chen
 */

/**
 * Important: the file path 'url' must be in the form 'jdbc:ucanaccess://path1//path2//path3//path4//Database1.accdb'
 */
public class Database {
    private Connection con;

    /**
     * This method will create the connection with the database file by using filepath
     * @param filepath A string represent the file path of the database file
     */
    public Database(String filepath) {
        String url = "jdbc:ucanaccess://" + filepath;
        try {
            this.con = DriverManager.getConnection(url);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will search Sellers and Buyers table in the database
     * and return a string of all the information asociated with the username
     * @param username the username of an account
     * @return A string that contains all the information of an account
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
    public synchronized String addUserData(String request) {
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
     * This method will search the purchase history of a certain customer by the ID
     * and return a string array contains "OrderID,Product_ID,Product_Name,Store_ID,Store_Name,Buyer_ID,Quantity,Product_Price"
     * separated by a ",". For example: ["1,100,Apple,1,Walmart,5,2,0.99", "2,101,Banana,2,Target,5,3,1.25", "4,103,Carrot,4,Kroger,5,4,0.75"]
     * @param buyerID the ID of the Customer to be searched
     * @return a string array contains the desired information
     * @throws SQLException the calling function needs to handle this exception
     */
    public String[] searchPurchaseHistoryByBuyerID(String buyerID) {
        int customerID;
        String[] purchaseHistory = new String[0];
        try {
            customerID = Integer.parseInt(buyerID);
        } catch (Exception e) {
            return purchaseHistory;
        }
        String query = "SELECT * FROM PurchaseHistory WHERE Buyer_ID=" + customerID;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String orderID = rs.getString("OrderID");
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
     * and return them as a string in which each element is separated by a ","
     * @param searchWord the word used for searching matched products
     * @return a String that contains the matched result
     */
    public String searchProducts(String searchWord) {
        StringBuilder result = new StringBuilder();

        try {
            // create the SQL statement
            String query = "SELECT * FROM Products WHERE LOWER(Product_name) LIKE LOWER(?) OR LOWER(Product_description) LIKE LOWER(?)";
            PreparedStatement ps = con.prepareStatement(query);

            // set the search keyword for the prepared statement
            ps.setString(1, "%" + searchWord.toLowerCase() + "%");
            ps.setString(2, "%" + searchWord.toLowerCase() + "%");

            // execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // iterate through the results and add them to the result StringBuilder
            while (rs.next()) {
                String productID = Integer.toString(rs.getInt("Product_ID"));
                String productName = rs.getString("Product_name");
                String storeID = Integer.toString(rs.getInt("Store_ID"));
                String productDescription = rs.getString("Product_description");
                String quantityAvailable = Integer.toString(rs.getInt("Quantity_available"));
                String price = Double.toString(rs.getDouble("Price"));

                result.append(productID).append(",").append(productName).append(",").append(storeID).append(",")
                        .append(productDescription).append(",").append(quantityAvailable).append(",").append(price)
                        .append("@");
            }

        } catch (SQLException e) {
            // print the error message to the console and return an empty string
            System.err.println("Error searching for products: " + e.getMessage());
            return "";
        }

        // return the result StringBuilder as a String
        return result.toString();
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

    /**
     * This method will update the quantity of a product
     * @param choice a product to be updated, example: 1,Vodka,5,Vodka alcohol 1L bottle,50,100,1
     * @return a String message
     */
    public synchronized String updateProduct(String choice) {
        String[] fields = choice.split(",");
        String productId = fields[0];
        int amountToBuy = Integer.parseInt(fields[fields.length - 1]);

        try (Statement stmt = con.createStatement()) {
            // Check if the product exists and has enough quantity available
            ResultSet rs = stmt.executeQuery("SELECT Quantity_available FROM Products WHERE Product_ID = " + productId);
            if (!rs.next()) {
                return "Product not found";
            }
            int quantityAvailable = rs.getInt("Quantity_available");
            if (quantityAvailable < amountToBuy) {
                return "Not enough quantity available";
            }

            // Update the quantity available
            int newQuantityAvailable = quantityAvailable - amountToBuy;
            stmt.executeUpdate("UPDATE Products SET Quantity_available = " + newQuantityAvailable + " WHERE Product_ID = " + productId);
            return "Order placed";
        } catch (SQLException e) {
            return "Product not found";
        }
    }

    /**
     * this method will return the cart information of a certain customer
     * by taking in the buyerID
     * @param customerId the ID of the customer
     * @return
     */
    public String searchCartByCustomer(String customerId) {
        String result = "";
        try {
            Statement statement = con.createStatement();
            String query = "SELECT * FROM Cart WHERE Buyer_ID = '" + customerId + "'";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                int productId = rs.getInt("Product_ID");
                String productName = rs.getString("Product_Name");
                String buyerId = rs.getString("Buyer_ID");
                int productQuantity = rs.getInt("Product_Quantity");
                double productPrice = rs.getDouble("Product_Price");
                int storeID = rs.getInt("Store_ID");

                String row = id + "," + productId + "," + productName + "," + buyerId + "," + productQuantity + "," + productPrice + "," + storeID;
                result += row + "@";
            }
        } catch (Exception e) {
            System.out.println("Error executing query: " + e);
            result = "";
        }

        return result;
    }

    /**
     * this method will return the cart information of a store
     * by taking in the storeId
     * @param storeId the ID of the store
     * @return a string information contains the cart items like "10,1,Vodka,2,7,100.0,5@11,1,Vodka,2,7,100.0,5@"
     */
    public String searchCartByStoreID(String storeId) {
        String result = "";
        try {
            Statement statement = con.createStatement();
            String query = "SELECT * FROM Cart WHERE Store_ID = '" + storeId + "'";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                int productId = rs.getInt("Product_ID");
                String productName = rs.getString("Product_Name");
                String buyerId = rs.getString("Buyer_ID");
                int productQuantity = rs.getInt("Product_Quantity");
                double productPrice = rs.getDouble("Product_Price");

                String row = id + "," + productId + "," + productName + "," + buyerId + "," + productQuantity + "," + productPrice + "," + storeId;
                result += row + "@";
            }
        } catch (Exception e) {
            System.out.println("Error executing query: " + e);
            result = "";
        }

        return result;
    }

    /**
     * this method will load all the stores in the market
     * @return a string separated by a "@" of all stores
     */
    public String loadMarkets() {
        String result = "";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Markets");
            while (rs.next()) {
                String storeId = rs.getString("Store_ID");
                String storeName = rs.getString("Store_name");
                String sellerId = rs.getString("Seller_ID");
                String marketInfo = String.join(",", storeId, storeName, sellerId);
                result += marketInfo + "@";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * this method will add product to the Cart table
     * @param cartItem it should be like: 1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,2
     * @return a string message
     */
    public synchronized String addToCart(String cartItem) {
        String[] itemInfo = cartItem.split(",");
        if (itemInfo.length != 8) {
            return "Invalid request format"; // invalid input format
        }

        try {
            int productID = Integer.parseInt(itemInfo[0]);
            String productName = itemInfo[1];
            int storeID = Integer.parseInt(itemInfo[2]);
            int quantity = Integer.parseInt(itemInfo[6]);
            double price = Double.parseDouble(itemInfo[5]);
            int customerID = Integer.parseInt(itemInfo[7]);

            PreparedStatement ps = con.prepareStatement("INSERT INTO Cart(Product_ID, Product_Name, Buyer_ID, Product_Quantity, Product_Price, Store_ID) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, productID);
            ps.setString(2, productName);
            ps.setInt(3, customerID);
            ps.setInt(4, quantity);
            ps.setDouble(5, price);
            ps.setInt(6, storeID);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                return "Added to cart successfully!";
            } else {
                return "Error adding to cart.";
            }
        } catch (Exception e) {
            System.out.println("Error adding item to cart: " + e.getMessage());
            return "Exception thrown";
        }
    }

    /**
     * this method will delete a cart item
     * @param id the cart id that will be deleted
     * @return a string message
     */
    public synchronized String deleteCartItem(String id) {
        try {
            Statement stmt = con.createStatement();
            String query = "DELETE FROM Cart WHERE id = " + id;
            int rowsAffected = stmt.executeUpdate(query);
            if (rowsAffected == 0) {
                return "No such an item";
            } else {
                return "Item deleted successfully";
            }
        } catch (SQLException e) {
            System.out.println("Error deleting item from cart: " + e.getMessage());
            return "Exception thrown";
        }
    }

    /**
     * this method will add purchase history by taking in a string
     * @param purchaseInfo it is like
     * "1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,3,CVS"
     * they are product id, product name, store id, product description, available quantity, price,
     * amount brought, buyer id, store, name
     * @return a string message
     */
    public synchronized String addPurchaseHistory(String purchaseInfo) {
        System.out.println(purchaseInfo);
        String[] fields = purchaseInfo.split(",");
        if (fields.length != 9) {
            return "Invalid format";
        }
        try {
            System.out.println("Purchase history running");
            String query = "INSERT INTO PurchaseHistory (Product_ID, Store_ID, Quantity, Buyer_ID, Product_Price, Product_Name, Store_Name) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(fields[0]));
            pstmt.setInt(2, Integer.parseInt(fields[2]));
            pstmt.setInt(3, Integer.parseInt(fields[6]));
            pstmt.setInt(4, Integer.parseInt(fields[7]));
            pstmt.setDouble(5, Double.parseDouble(fields[5]));
            pstmt.setString(6, fields[1]);
            pstmt.setString(7, fields[8]);
            pstmt.executeUpdate();
            return "Purchase history added successfully.";
        } catch (SQLException e) {
            System.err.println("Error adding purchase history: " + e.getMessage());
            return "Exception thrown";
        }
    }

    /**
     * this method will get the store name by taking in a string
     * @param purchaseInfo like "1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,3"
     * @return a string of the store name
     */
    public String getStoreName(String purchaseInfo) {
        String[] fields = purchaseInfo.split(",");
        if (fields.length != 8) {
            return "Invalid format";
        }
        int storeId = Integer.parseInt(fields[2]);
        try {
            String query = "SELECT Store_name FROM Markets WHERE Store_ID=?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storeName = rs.getString("Store_name");
                return storeName;
            } else {
                return "Store not found";
            }
        } catch (SQLException e) {
            System.err.println("Error getting store name: " + e.getMessage());
            return "Exception thrown";
        }
    }

    /**
     * This method searches for products by market choice and returns a single string separated by "@".
     * If any unexpected error occurs, an empty string will be returned.
     * @param storeID the choice of market to search for products
     * @return a single string separated by "@" containing the product data
     */
    public String searchProductsByStoreID(String storeID) {
        StringBuilder result = new StringBuilder();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Products WHERE Store_ID='" + storeID + "'");
            while (rs.next()) {
                String productInfo = String.join(",", rs.getString("Product_ID"), rs.getString("Product_name"), rs.getString("Store_ID"), rs.getString("Product_description"), rs.getString("Quantity_available"), rs.getString("Price"));
                result.append(productInfo).append("@");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return result.toString();
    }
    /**
     * This method searches for all markets owned by a certain seller by the ID and returns a string separated by "@"
     * containing "Store_ID,Store_name,Seller_ID". For example: "1,Walmart,100@2,Target,100@4,Kroger,100".
     * @param sellerId the ID of the seller who owns the markets to be searched
     * @return a string separated by "@" containing the desired information
     */
    public String searchMarketsBySellerId(String sellerId) {
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
            return "";
        }
        return String.join("@", marketData);
    }

    /**
     * This method will remove a product by product ID
     * @param productIndex the product ID
     */
    public synchronized String removeProduct(String productIndex) {
        try {
            Statement stmt = con.createStatement();
            String sql = "DELETE FROM Products WHERE Product_ID = " + productIndex;
            int rowsAffected = stmt.executeUpdate(sql);
            if (rowsAffected == 0) {
                return "No product found with ID " + productIndex;
            } else {
                return "Product removed successfully.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * this method will add a product to the market
     * @param productInfo the product information like "red pen,5,a red pen,49,2.5"
     * @return a message
     */
    public synchronized String addProductBySeller(String productInfo) {
        String[] productFields = productInfo.split(",");
        if (productFields.length != 5) {
            return "Invalid input: Expected 5 values separated by commas.";
        }
        String productName = productFields[0];
        String storeId = productFields[1];
        String productDescription = productFields[2];
        int quantityAvailable = Integer.parseInt(productFields[3]);
        double price = Double.parseDouble(productFields[4]);

        try {
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO Products (Product_name, Store_ID, Product_description, Quantity_available, Price) VALUES ('" + productName + "', '" + storeId + "', '" + productDescription + "', " + quantityAvailable + ", " + price + ")";
            stmt.executeUpdate(sql);
            return "Product added successfully";
        } catch (SQLException e) {
            return "Product added failed" + e;
        }
    }

    /**
     * this will search the purchase history related to a store
     * @param storeId the store id
     * @return the purchase history string
     */
    public String searchPurchaseHistoryByStoreId(String storeId) {
        StringBuilder purchaseHistoryData = new StringBuilder();
        try {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM PurchaseHistory WHERE Store_ID='" + storeId + "'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int productId = rs.getInt("Product_ID");
                int storeIdFromDb = rs.getInt("Store_ID");
                int quantity = rs.getInt("Quantity");
                int buyerId = rs.getInt("Buyer_ID");
                double productPrice = rs.getDouble("Product_Price");
                String productName = rs.getString("Product_Name");
                String storeName = rs.getString("Store_Name");
                int orderId = rs.getInt("OrderID");

                String purchaseHistoryInfo = String.join(",", String.valueOf(productId), String.valueOf(storeIdFromDb), String.valueOf(quantity), String.valueOf(buyerId), String.valueOf(productPrice), productName, storeName, String.valueOf(orderId));
                purchaseHistoryData.append(purchaseHistoryInfo).append("@");
            }
            if (purchaseHistoryData.length() > 0) {
                purchaseHistoryData.deleteCharAt(purchaseHistoryData.length() - 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return purchaseHistoryData.toString();
    }

    /**
     * this will add a store for a seller by a store name a seller id
     * @param storeInfo example "aStore,1"
     * @return a message
     */
    public synchronized String addMarket (String storeInfo) {
        String[] storeField = storeInfo.split(",");
        if (storeField.length != 2) {
            return "Invalid input: Expected 2 values separated by commas.";
        }
        String storeName = storeField[0];
        String SellerID = storeField[1];
        try {
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO Markets (Store_name, Seller_ID) VALUES ('" + storeName + "', '" + SellerID + "')";
            stmt.executeUpdate(sql);
            return "Store added successfully.";
        } catch (SQLException e) {
            return "Store added failed: " + e.getMessage();
        }
    }
    
}
