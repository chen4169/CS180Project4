package Project5;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
/**
 * This is the Server class that will create connection and interact with the database.
 * This is a simple version that show how this class can be written for now.
 * @Version 2023/4/24 1.5
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




    private static String dataBasePath =
            "C://Users//owenw//Desktop//Proj5//src//Database1.accdb";
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
                    if (request.substring(0, 2).equals(getUserData)) {
                        System.out.println("Processing getUserData...");
                        // handle request from Marketplace to check username
                        String username = request.substring(2);
                        String response = db.getUserData(username);
                        out.println(response);
                    }
                    else if (request.substring(0, 2).equals(addUserData)) {
                        System.out.println("Processing addUserData...");
                        // handle request from Marketplace to add new user data
                        String userData = request.substring(2); // remove "02"
                        String response = db.addUserData(userData); // pass the user data as a single input string to the addUserData method
                        out.println(response);
                    }
                    else if (request.substring(0, 2).equals(searchPurchaseHistoryByBuyerID)) {
                        System.out.println("Processing searchPurchaseHistoryByBuyerID...");
                        // handle request from CustomerClient to search purchase history
                        int customerID = Integer.parseInt(request.substring(2)); // remove "03"
                        String[] purchaseHistory = db.searchPurchaseHistoryByBuyerID(customerID); // retrieve purchase history from the database
                        String historyString = String.join("@", purchaseHistory); // convert to a string separated by @
                        out.println(historyString); // sent the string to client
                        out.flush(); // ensure that all data is sent immediately
                        // historyString example: "1,100,Apple,1,Walmart,5,2,0.99@2,101,Banana,2,Target,5,3,1.25@4,103,Carrot,4,Kroger,5,4,0.75"
                    }
                    else if (request.substring(0, 2).equals(productSearchEngine)) {
                        System.out.println("Processing productSearchEngine...");
                        // handle request from CustomerClient to search for a certain product
                        String searchWord = request.substring(2).toLowerCase(); // remove "04" and convert to lowercase
                        ArrayList<String> matchingProducts = db.searchProducts(searchWord); // retrieve matching products from the database
                        String results = "";

                        for (String s: matchingProducts) {
                            if (matchingProducts.indexOf(s) == matchingProducts.size() - 1) {
                                results += s;
                            } else {
                                results += s + "//";
                            }
                        }

                        out.print(results);
                        out.flush();
                    }
                    else if (request.substring(0,2).equals(searchCartByID)) {
                        System.out.println("Processing searchCartByID");
                        String id = request.substring(2);
                        ArrayList<String> cart = db.searchCart(id); // getting info in array list
                        // Making into string
                        String cartString = "";
                        for (String s: cart) {
                            if (cart.indexOf(s) != cart.size() - 1) {
                                cartString += s + "//"; // USING // AS SEPARATOR
                            } else {
                                cartString += s;
                            }
                        }
                        out.print(cartString);
                        out.flush();
                    }
                    else if (request.substring(0, 2).equals(updateProduct)) {
                        String prodId = request.substring(2, 4);
                        if (prodId.substring(0, 1).equals("0")) {  // Sheds 0's
                            prodId = request.substring(3, 4);
                        }
                        int quantity = Integer.parseInt(request.substring(4));
                        db.updateProductQuantity(prodId, quantity);
                    }
                    else if (request.substring(0, 2).equals(updateHistory)) {
                        // product_ID, store_ID, quantity, buyer_id, price -> COULD FIX WITH CSV SPLIT
                        String prodId = request.substring(2, 4);
                        if (prodId.substring(0, 1).equals("0")) {
                            prodId = request.substring(3, 4);
                        }
                        String storeId = request.substring(4, 6);
                        if (storeId.substring(0, 1).equals("0")) {
                            storeId = request.substring(5, 6);
                        }
                        String quant = request.substring(6,9); // allow three digits for quantity
                        String buyerId = request.substring(9, 11);
                        if (buyerId.substring(0, 1).equals("0")) {
                            buyerId = request.substring(10, 11);
                        }
                        String price = request.substring(11, 17); // allow 5 digits (and decimal) for price
                        db.addPurchaseHistory(prodId, storeId, Integer.parseInt(quant),
                                buyerId, Double.parseDouble(price));
                    } else if (request.substring(0, 2).equals(addToCart)) {
                        // Witerawy woot wake fwom fowtnite
                    }


                } // while loop

            } catch (IOException e) {
                System.out.println("Error handling client request: " + e);
            } finally { // ensure the socket is closed regardless of whether an exception is thrown
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e);
                }
            }
        }
    }
}
