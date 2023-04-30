
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
/**
 * This is the Server class that will create connection and interact with the database.
 * This is a simple version that show how this class can be written for now.
 * @Version 2023/4/30 1.7
 * @author Libin Chen
 */
public class Server {
    private static String getUserData = "01"; //command index to get user data
    private static String addUserData = "02"; //command index to add user data
    private static String searchPurchaseHistoryByBuyerID = "03"; //command index to search purchase history of a customer
    private static String productSearchEngine = "04"; //command index to search a certain product
    private static String searchCartByID = "05";
    private static String updateProduct = "06";
    private static String updateHistory = "07";
    private static String addToCart = "08";
    private static String listAllProducts = "09";
    private static String purchaseProduct = "10"; //command index to purchase a product
    private static String deleteCartItem = "11" ;//command index to delete a cart item
    private static String searchProductsByStoreID = "12"; //command index to search product belong to a seller
    private static String searchMarketsBySellerId = "13"; //command index to search stores belong to a seller
    private static String removeProduct = "14"; //command index to remove a product
    private static String addProductBySeller = "15"; //command index to add a product
    private static String searchPurchaseHistoryByStoreId = "16"; //command index to search purchase history related to a store
    private static String dataBasePath = "C://tmp//CSproject5//CSproject5//Database1.accdb";
    private static int port = 4242;
    private static Database db;

    public static void main(String[] args) throws IOException {

        boolean isValidPath = false;
        while (!isValidPath) {
            if (dataBasePath.equals("")) { // if the database address is empty
                dataBasePath = JOptionPane.showInputDialog(null, "Enter the database path", "Marketplace initialization",
                        JOptionPane.QUESTION_MESSAGE);
                if (dataBasePath == null) { // if the user click the cross sign "x", end the program
                    JOptionPane.showMessageDialog(null, "Program terminated.");
                    return;
                }
            }

            // check if the database file exists
            File file = new File(dataBasePath);
            if (file.exists() && !file.isDirectory()) {
                isValidPath = true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid database path. Please enter a valid path.");
                dataBasePath = "";
            }

        }

        try {
            // create a new Database instance
            db = new Database(dataBasePath);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage() + ". Program terminated.");
            return;
        }

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Waiting for clients to connect...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");
            // create a new thread to handle client requests
            new Thread(new ClientHandler(socket)).start();
        }

    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("new request: " + request);
                    if (request.substring(0, 2).equals(getUserData)) {
                        System.out.println("Processing getUserData...");
                        // handle request from Marketplace to check username
                        String username = request.substring(2);
                        String response = db.getUserData(username);
                        out.println(response);
                        out.flush(); // ensure that all data is sent immediately
                    } //Done "TestBuyer1" "C1,TestBuyer1,123456,TestBuyer1"
                    else if (request.substring(0, 2).equals(addUserData)) {
                        System.out.println("Processing addUserData...");
                        // handle request from Marketplace to add new user data
                        String userData = request.substring(2); // remove "02"
                        String response = db.addUserData(userData); // pass the user data as a single input string to the addUserData method
                        out.println(response);
                        out.flush(); // ensure that all data is sent immediately
                    } //Done "Susername,password,trueName" message
                    else if (request.substring(0, 2).equals(searchPurchaseHistoryByBuyerID)) {
                        System.out.println("Processing searchPurchaseHistoryByBuyerID...");
                        // handle request from CustomerClient to search purchase history
                        String customerID = request.substring(2); // remove "03"
                        String[] purchaseHistory = db.searchPurchaseHistoryByBuyerID(customerID); // retrieve purchase history from the database
                        String historyString = String.join("@", purchaseHistory); // convert to a string separated by @
                        out.println(historyString); // sent the string to client
                        out.flush(); // ensure that all data is sent immediately
                        // historyString example: "1,100,Apple,1,Walmart,5,2,0.99@2,101,Banana,2,Target,5,3,1.25@4,103,Carrot,4,Kroger,5,4,0.75"
                    } //Done  "13", could send empty "1,100,Apple,1,Walmart,5,2,0.99@2,101,Banana,2,Target,5,3,1.25@4,103,Carrot,4,Kroger,5,4,0.75"
                    else if (request.substring(0, 2).equals(productSearchEngine)) {
                        System.out.println("Processing productSearchEngine...");
                        // handle request from CustomerClient to search for a certain product
                        String searchWord = request.substring(2).toLowerCase(); // remove "04" and convert to lowercase
                        String response = db.searchProducts(searchWord);
                        out.print(response);
                        out.flush();
                    } //Done "vo" "1,Vodka,5,Vodka alcohol 1L bottle,49,100.0@2,Vodka,3,Vodka alcohol 1L bottle,24,125.0@"
                    else if (request.substring(0,2).equals(searchCartByID)) {
                        System.out.println("Processing searchCartByID...");
                        String id = request.substring(2);
                        String cartString = db.searchCartByCustomer(id); // getting info in array list
                        out.print(cartString);
                        out.flush();
                    } //Done "13", could send empty "10,1,Vodka,2,7,100.0@"
                    else if (request.substring(0, 2).equals(updateProduct)) {
                        System.out.println("Processing updateProduct...");
                        String choice = request.substring(2);
                        // choice example: 1,Vodka,5,Vodka alcohol 1L bottle,50,100,1
                        String response = db.updateProduct(choice);
                        out.print(response);
                        out.flush();
                    } //Done "1,Vodka,5,Vodka alcohol 1L bottle,50,100,7"
                    else if (request.substring(0, 2).equals(updateHistory)) {
                        System.out.println("Processing updateHistory...");
                        String purchaseInfo = request.substring(2); // remove "07" and convert to lowercase
                        String storeName = db.getStoreName(purchaseInfo);
                        purchaseInfo = purchaseInfo + "," + storeName;
                        String message = db.addPurchaseHistory(purchaseInfo);
                        out.print(message);
                        out.flush();
                    } //Done "1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,3" message
                    else if (request.substring(0, 2).equals(listAllProducts)) {
                        System.out.println("Processing listAllProducts...");
                        // handle request from CustomerClient to list all products
                        String productList = db.listAllProducts();
                        out.println(productList);
                        out.flush();
                    } //Done None "1,Vodka,5,Vodka alcohol 1L bottle,49,100@2,Vodka,3,Vodka alcohol 1L bottle,24,125"
                    else if (request.substring(0, 2).equals(purchaseProduct)) {
                        System.out.println("Processing purchaseProduct...");
                        String purchaseMessage = db.updateProduct(request);
                        out.println(purchaseMessage);
                        out.flush();
                    } //Done "1,Vodka,5,Vodka alcohol 1L bottle,50,100,1" message
                    else if (request.substring(0, 2).equals(addToCart)) {
                        System.out.println("Processing addToCart...");
                        String purchaseInfo = request.substring(2);
                        String message = db.updateProduct(purchaseInfo);
                        out.println(message);
                        out.flush();
                    } //Done "1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,2" message
                    else if (request.substring(0, 2).equals(deleteCartItem)) {
                        System.out.println("Processing deleteCartItem...");
                        String itemID = request.substring(2);
                        String message = db.updateProduct(itemID);
                        out.println(message);
                        out.flush();
                    } //Done "1" message
                    else if (request.substring(0, 2).equals(searchProductsByStoreID)) {
                        System.out.println("Processing searchProductsByStoreID...");
                        String StoreID = request.substring(2);
                        String response = db.searchProductsByStoreID(StoreID);
                        out.println(response);
                        out.flush();
                    } //Done "1" "3,tomato,1,A red fruit,100,5@5,tomato,1,A red fruit,200,5@"
                    else if (request.substring(0, 2).equals(searchMarketsBySellerId)) {
                        System.out.println("Processing searchMarketsBySellerId...");
                        String sellerID = request.substring(2);
                        String response = db.searchMarketsBySellerId(sellerID);
                        out.println(response);
                        out.flush();
                    } //Done "1" "1,CS shop,1@4,Target,1"
                    else if (request.substring(0, 2).equals(removeProduct)) {
                        System.out.println("Processing searchMarketsBySellerId...");
                        String productID = request.substring(2);
                        String message = db.removeProduct(productID);
                        out.println(message);
                        out.flush();
                    } //Done "4" message
                    else if (request.substring(0, 2).equals(addProductBySeller)) {
                        System.out.println("Processing addProductBySeller...");
                        String productInfo = request.substring(2);
                        String message = db.addProductBySeller(productInfo);
                        out.println(message);
                        out.flush();
                    } //Done "red pen,5,a red pen,49,2.5" message
                    else if (request.substring(0, 2).equals(searchPurchaseHistoryByStoreId)) {
                        System.out.println("Processing searchPurchaseHistoryByStoreId...");
                        String historyInfo = request.substring(2);
                        String response = db.addProductBySeller(historyInfo);
                        out.println(response);
                        out.flush();
                    } //Done "5" "1,5,7,3,100.0,Vodka,NC,14"
                } // while loop
            } catch (Exception e) {
                System.out.println("Error handling client request: " + e);
            } finally { // ensure the socket is closed regardless of whether an exception is thrown
                try {
                    socket.close();
                } catch (Exception e) {
                    System.out.println("Error closing socket: " + e);
                }
            }
        }
    }
}
