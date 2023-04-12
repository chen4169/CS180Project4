package project4;
import java.sql.*;
import java.util.ArrayList;

/**
 * Important: the file path 'url' must be in the form 'jdbc:ucanaccess://C://path1//path2//path3//path4//Database1.accdb'
 */
public class Database {
    private Connection con;
    private String sellerCounter;
    private String buyerCounter;

    public Database(String filepath) {
        //String url = "jdbc:ucanaccess:" + filepath;
        // Now is for test only, modify the below url to the file path in this format, else, it won't work.
        String url = "jdbc:ucanaccess://C://Users//Libin//IdeaProjects//CS18000//Database1.accdb";
        try {
            this.con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
