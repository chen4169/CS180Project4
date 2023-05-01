import javax.swing.*;
import java.io.*;
import java.net.Socket;


/**
 * This is a client class that provides the interface for a customer to exchange information with the server.
 * @Version 2023/5/1 1.6
 * @author Owen Willis, Libin Chen
 */
public class CustomerClient {
    private static String goodbyeMessage = "Thanks for using our App! Goodbye!";
    private static String productSearchEngine = "04"; //command index to search a certain product
    private static String listAllProducts = "09";
    private static String updateProduct = "06"; //command index to purchase a product
    private static String updateHistory = "07";
    private BufferedReader in;
    private PrintWriter out;
    String id, username, password, trueName;
    Socket socket;


    public CustomerClient(Socket socket, String userData, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        String[] userDataArray = userData.split(",");
        this.id = userDataArray[0];
        this.username = userDataArray[1];
        this.password = userDataArray[2];
        this.trueName = userDataArray[3];
        this.in = in;
        this.out = out;

    }

    /**
     * This method is used to test if the user entered password correctly
     * @return a boolean indicate if the user passed the password test
     */
    public boolean passwordTest() {
        boolean correct = false; // status if the password is entered correctly
        String inputPassword; // the input password

        // prompt the user to enter password
        while (!correct) {
            inputPassword = JOptionPane.showInputDialog(null, "Enter password:");
            if (inputPassword == null) {
                // User clicked the "x" button, so end the program
                JOptionPane.showMessageDialog(null, goodbyeMessage);
                return false;
            } else if (inputPassword.equals(password)) {
                // Password is correct, so continue with the rest of the code
                correct = true;
            } else {
                // Password is incorrect, so show an error message and prompt again
                JOptionPane.showMessageDialog(null, "Incorrect password. Please try again.");
            }
        }
        return true;
    }

    public boolean start()  {
        boolean correct = passwordTest();

        if (!correct) {
            return false;
        }


        String[] products = null;
        String choice = "";
        while (true) {
            if (!choice.equals("Sort Products")) {
                // ask the Server to get all products
                String productList = "";
                try {
                    out.println(listAllProducts);
                    out.flush();
                    productList = in.readLine();
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "Can't connect to the Server", "Internet issue", JOptionPane.PLAIN_MESSAGE);
                    err.printStackTrace();
                    return false;
                }
                // example: 1,Vodka,5,Vodka alcohol 1L bottle,50,100@2,Vodka,3,Vodka alcohol 1L bottle,24,125@3,tomato,1,A red fruit,100,5

                // parse the productList string
                products = productList.split("@");
                // example: 1,Vodka,5,Vodka alcohol 1L bottle,50,100

            }
            //  USER CHOOSES WHAT THEY WANT TO DO
            choice = "";
            String[] options = {"Search", "View Market", "View Cart", "Sort Products", "Export History"};
            choice = (String) JOptionPane.showInputDialog(null, "Search",
                    "Search", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            // Choice Branch
            if (choice == null) {
                JOptionPane.showMessageDialog(null, goodbyeMessage, "Market", JOptionPane.PLAIN_MESSAGE);
                break;
            } else if (choice.equals("Search")) {
                searchEngine(products);
            } else if (choice.equals("View Market")) {
                viewMarket(products);
            } else if (choice.equals("View Cart")) {
                viewCart(products);
            } else if (choice.equals("Sort Products")) {
                sortProducts(products);
            } else if (choice.equals("Export History")) {
                exportHistory();
            }

        }
        return false;
    }

    public static String[] searchProducts(String searchTerm, String[] products) {
        int numMatchingProducts = 0;
        for (String product : products) {
            String[] fields = product.split(",");
            String productName = fields[1];
            String productDescription = fields[3];
            if (productName.toLowerCase().contains(searchTerm.toLowerCase())
                    || productDescription.toLowerCase().contains(searchTerm.toLowerCase())) {
                numMatchingProducts++;
            }
        }

        String[] matchingProducts = new String[numMatchingProducts];
        int index = 0;
        for (String product : products) {
            String[] fields = product.split(",");
            String productName = fields[1];
            String productDescription = fields[3];
            if (productName.toLowerCase().contains(searchTerm.toLowerCase())
                    || productDescription.toLowerCase().contains(searchTerm.toLowerCase())) {
                matchingProducts[index] = product;
                index++;
            }
        }

        return matchingProducts;
    }

    public void searchEngine(String[] products) {
        String searchWord;

        while (true) {
            searchWord = JOptionPane.showInputDialog(null, "Search",
                    "Search", JOptionPane.QUESTION_MESSAGE);

            if (searchWord == null) {  // Cancelled
                break;
            } else if (searchWord.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error! Please enter Valid Input.",
                        "search", JOptionPane.ERROR_MESSAGE);
            } else {  // Valid Entry
                break;
            }
        }

        if (searchWord != null) {
            String[] results = searchProducts(searchWord, products);
            JOptionPane.showMessageDialog(null, results);

            // Displaying results in dropdown window
            String choice = (String) JOptionPane.showInputDialog(null, "Search",
                    "Search", JOptionPane.PLAIN_MESSAGE, null, results, results[0]);
            // example: 1,Vodka,5,Vodka alcohol 1L bottle,50,100
            JOptionPane.showMessageDialog(null, "choice = " + choice);
            if (choice == null) {
                return;
            }

            // Choosing what to do with the product
            Object[] options = {"Buy", "Add to Cart", "Cancel"};
            int option = JOptionPane.showOptionDialog(null,
                    "What would you like to do with the product?", "Search",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

            // Quantity Available
            int quantAvail = Integer.parseInt(choice.split(",")[4]);

            if (option == 0) {  // BUYING

                // Getting amount
                int amt = 0;
                while (true) {
                    try {
                        String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                        if (amtAsString == null) { // USER CANCELLING
                            break;
                        } else {
                            amt = Integer.parseInt(amtAsString);
                        }


                        if (amt <= quantAvail) {  // if there is enough
                            break;
                        } else {  // if there isn't
                            JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception er) {
                        JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (amt != 0) {
                    JOptionPane.showMessageDialog(null, updateProduct + choice + "," + amt);
                    out.println(updateProduct + choice + "," + amt);
                    out.flush();
                    try {
                        String resp = in.readLine();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    // UPDATING HISTORY -> FORMAT = 1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,3
                    String[] c = choice.split(",");
                    String request = updateHistory + choice + "," + amt + "," + id;
                    out.println(request);
                    out.flush();

                    try {
                        String resp = in.readLine();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, "Order Placed" + ": " + choice, "Market", JOptionPane.PLAIN_MESSAGE);
                } else {  // Order was cancelled
                    JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                }

            } else if (option == 1) { // ADD TO CART
                // Getting amount
                int amt = 0;
                while (true) {
                    try {
                        String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                        if (amtAsString == null) { // USER CANCELLING
                            break;
                        } else {
                            amt = Integer.parseInt(amtAsString);
                        }

                        if (amt <= quantAvail) {  // if there is enough
                            break;
                        } else {  // if there isn't
                            JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException er) {
                        JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                    }
                }


                if (amt != 0) {
                    // TODO: Send request to server to add specified quantity to the cart
                    String request = choice + "," + amt + "," + id;
                    out.println("08" + request);
                    out.flush();

                    try {
                        String resp = in.readLine();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, String.format("%d of %s added to cart!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                }

            } else { // CANCEL
                JOptionPane.showMessageDialog(null, "Cancelling Order...", "Market",
                        JOptionPane.PLAIN_MESSAGE);
            }

        }
    }

    public void viewMarket(String[] products) {
        String choice = (String) JOptionPane.showInputDialog(null, "Products:",
                "Market", JOptionPane.PLAIN_MESSAGE, null, products, products[0]);

        if (choice != null) {
            int quantAvail = Integer.parseInt(choice.split(",")[4]);

            String[] op = {"Buy", "Add to Cart"};
            int buyOrCart = JOptionPane.showOptionDialog(null,
                    "Would you like to Buy or Add to cart?", "Market", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, op, op[1]);

            if (buyOrCart == 0) { // BUYING
                int amt = 0;
                while (true) {
                    try {
                        String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                        if (amtAsString == null) { // USER CANCELLING
                            break;
                        } else {
                            amt = Integer.parseInt(amtAsString);
                        }


                        if (amt <= quantAvail) {  // if there is enough
                            break;
                        } else {  // if there isn't
                            JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException er) {
                        JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (amt != 0) {
                    // TODO: send server request to specified quantity of product (amt)
                    //    also add to purchase history

                    // UPDATING HISTORY -> FORMAT = 1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,3
                    String[] c = choice.split(",");
                    String request = updateHistory + choice + "," + amt + "," + id;
                    out.println(request);
                    out.flush();

                    try {
                        String resp = in.readLine();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    // request = 06prodidQUANT -> UPDATING PRODUCT INFORMATION
                    out.println(updateProduct + choice + "," + amt);
                    out.flush();

                    try {
                        String resp = in.readLine();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, String.format("%d of %s bought!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                }


            } else if (buyOrCart == 1) { // adding to cart
                int amt = 0;
                while (true) {
                    try {
                        String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                        if (amtAsString == null) { // USER CANCELLING
                            break;
                        } else {
                            amt = Integer.parseInt(amtAsString);
                        }


                        if (amt <= quantAvail) {  // if there is enough
                            break;
                        } else {  // if there isn't
                            JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException er) {
                        JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                    }
                }


                if (amt != 0) {
                    // TODO: Send request to server to add specified quantity to the cart
                    String request = choice + "," + amt + "," + id;
                    out.println("08" + request);
                    out.flush();

                    try {
                        String resp = in.readLine();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, String.format("%d of %s added to cart!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                }
            }
        }
    }

    public void viewCart(String[] products) {
        // TODO: Change to server request -> Request USER ID's cart
        out.println("05" + id);
        out.flush();
        try {

            String cartString = in.readLine();
            String[] cart = cartString.split("@");

            if (cart.length > 0) {
                String cartChoice = (String) JOptionPane.showInputDialog(null, "Search",
                        "Market", JOptionPane.PLAIN_MESSAGE, null, cart, cart[0]);

                if (cartChoice != null) {  // If they don't cancel

                    // Getting correct product based on the choice
                    String prodId = cartChoice.split(",")[1];
                    String choice = "";
                    for (String s: products) {
                        if (s.split(",")[0].equals(prodId)) {
                            choice = s;
                            break;
                        }
                    }


                    int quantAvail = Integer.parseInt(choice.split(",")[4]);

                    String[] op = {"Buy", "Remove"};
                    int buyOrCart = JOptionPane.showOptionDialog(null,
                            "Would you like to Buy or Remove?", "Market", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, op, op[1]);

                    if (buyOrCart == 0) { // BUYING
                        int amt = Integer.parseInt(cartChoice.split(",")[4]);

                        if (amt <= quantAvail) {  // if there is enough
                            // UPDATING HISTORY -> FORMAT = 1,Vodka,5,Vodka alcohol 1L bottle,50,100,7,3
                            String[] c = choice.split(",");
                            String request = updateHistory + choice + "," + amt + "," + id;
                            out.println(request);
                            out.flush();

                            try {
                                String resp = in.readLine();
                            } catch (IOException err) {
                                err.printStackTrace();
                            }

                            // request = 06prodidQUANT -> UPDATING PRODUCT INFORMATION
                            out.println(updateProduct + choice + "," + amt);
                            out.flush();

                            try {
                                String resp = in.readLine();
                            } catch (IOException err) {
                                err.printStackTrace();
                            }

                            // Removing from cart after bought
                            String cartId = cartChoice.split(",")[0];
                            out.println("11" + cartId);
                            out.flush();

                            try {
                                String resp = in.readLine();
                            } catch (IOException err) {
                                err.printStackTrace();
                            }



                            JOptionPane.showMessageDialog(null, String.format("%d of %s bought!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                        } else {  // if there isn't
                            JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                        }


                    } else if (buyOrCart == 1) { // REMOVING
                        String cartId = cartChoice.split(",")[0];
                        out.println("11" + cartId);
                        out.flush();

                        try {
                            String resp = in.readLine();
                            JOptionPane.showMessageDialog(null, resp, "Server Response", JOptionPane.PLAIN_MESSAGE);
                        } catch (IOException err) {
                            err.printStackTrace();
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Your Cart is Empty", "Market", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "Error Retrieving Cart", "Market", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sortProducts(String[] products) {
        String[] op = {"Quantity", "Price"};
        int sortType = JOptionPane.showOptionDialog(null,
                "How would you like to sort market?", "Market", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, op, op[1]);

        if (sortType == 0 || sortType == 1) {

            int imp;
            int index = 0;

            if (sortType == 0) { // By quantity
                imp = 4;
            } else {
                imp = 5;
            }

            while (true) {
                if (index == products.length - 1) {
                    break;
                }
                if (Double.parseDouble(products[index].split(",")[imp]) >
                        Double.parseDouble(products[index + 1].split(",")[imp])) {
                    String temp = products[index];
                    products[index] = products[index + 1];
                    products[index + 1] = temp;
                    if (index != 0) {
                        index--;
                    }
                } else {
                    index++;
                }
            }
            JOptionPane.showMessageDialog(null, "Market Sorted! Check 'View Market' to see.", "Market", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    public void exportHistory() {
        // Getting file information
        String exportFileName = JOptionPane.showInputDialog(null,
                "Enter file name you want to export to (before the '.'): ", "Market",
                JOptionPane.QUESTION_MESSAGE);
        if (!(exportFileName == null)) {
            exportFileName += ".csv";

            // TODO: server request for history related to id -> store in ArrayList "history" below
            out.println("03" + id);

            try {
                String[] history = in.readLine().split("@");

                // Writing to file
                try (PrintWriter pw = new PrintWriter(exportFileName)) {
                    // Key For users
                    pw.println("orderID,productID,productName,storeID,storeName,buyerIDStr,quantity,price;");

                    // Actual history
                    for (String s : history) {
                        pw.println(s);
                    }

                    JOptionPane.showMessageDialog(null, "File Written to!", "Market",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException er) {
                    JOptionPane.showMessageDialog(null, "Error when exporting", "Market",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException err) {
                JOptionPane.showMessageDialog(null, "Error when retrieving History", "Market", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Canceling Export", "Market",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }
}
