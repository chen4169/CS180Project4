import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This is a client class that provides interface for a seller to exchange information with the server.
 */
public class SellerClient {
    private static String searchMarketsBySellerId = "13";
    private static String searchProductsByStoreID = "12";

    private final BufferedReader in;
    private final PrintWriter out;
    String id, username, password, trueName;
    Database db;
    Socket socket;
    String sellerStoreChoice;
    String[] singleMarket;
    String storeId;
    String[] stores;
    ArrayList<String> productsWithDescriptions;
    ArrayList<String> sellerProductsWithID;
    String[] sellerProducts;

    public SellerClient(Socket socket, String userData, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        String[] userDataArray = userData.split(",");
        this.id = userDataArray[0];
        this.username = userDataArray[1];
        this.password = userDataArray[2];
        this.trueName = userDataArray[3];
        this.in = in;
        this.out = out;
    }

    public boolean passwordTest() {
        boolean correct = false; // status if the password is entered correctly
        String inputPassword; // the input password

        // prompt the user to enter password
        while (!correct) {
            inputPassword = JOptionPane.showInputDialog(null, "Enter password:");
            if (inputPassword == null) {
                // User clicked the "x" button, so end the program
                String goodbyeMessage = "Thanks for using our App! Goodbye!";
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

    public boolean start() throws IOException {
        boolean correct = passwordTest();
        if (!correct) { // if the user click to exit
            return false;
        }

        //-----------------------------------------Seller Interface---------------------------------------------------

        while (true) {
            try {
                // Getting the Stores associated with seller
                out.println(searchMarketsBySellerId + id);
                out.flush();

                try {
                    stores = in.readLine().split("@");
                } catch (IOException err) {
                    err.printStackTrace();
                }


                String sellerChoice = sellerChoiceInputDialog(); //what the seller wants to do

                if (sellerChoice == null) {  // EXITING OPERATION
                    break;
                } if (sellerChoice.equals("View Products")) { // seller chooses to view products

                    //dropdown menu JOptionPane that gives the choice in the format of (store name), Store ID: (store ID)
                    String sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store To View",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, stores,
                            stores[0]);

                    if (sellerStoreChoice == null) { //CANCELLED
                        break;
                    }


                    String storeId = sellerStoreChoice.split(",")[0]; //gets the store ID
                    String chosenStore = sellerStoreChoice.split(",")[0]; //gets the store name


                    // Getting products associated with stores
                    out.println(searchProductsByStoreID + storeId);

                    String[] products = in.readLine().split("@");

                    JOptionPane.showMessageDialog(null, products,
                            "Seller Menu", JOptionPane.INFORMATION_MESSAGE);


                } else if (sellerChoice.equals("Remove Products")) { // remove products option


                    // sellerProductsWithID =;//TODO: |DATABASE| get an arraylist of products pertaining to a specific store ID. Format: "(product name) - Product ID: (product ID)"  *formatted exactly*

                    sellerProducts = new String[sellerProductsWithID.size()]; //arraylist of products determines size of array

                    for (String line : sellerProductsWithID) { //products + ID are assigned to array by splitting the commas
                        sellerProducts = line.split(",");
                    }

                    //drop down menu of what product the seller wants to remove
                    String sellerProductRemoval = (String) JOptionPane.showInputDialog(null,
                            "Select a Product to Remove",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, sellerProducts,
                            sellerProducts[0]);

                    if (sellerProductRemoval == null) { //CANCELLED
                        break;
                    }

                    String productID = sellerProductRemoval.substring(sellerProductRemoval.indexOf(" " + 1)); //gets the product ID from the seller product choice

                    // db.removeProduct(Integer.parseInt(productID)); //TODO: |DATABASE| remove product from the database by input of the product index


                } else if (sellerChoice.equals("Add Products")) { // adds product


                    //adds stores from the arraylist to an array
                    for (String store : stores) {
                        singleMarket = store.split(",");
                    }

                    //gets the store ID from the seller store choice, the store products will be added to
                    sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store to Add Products",
                            "Seller Menu - Add Products", JOptionPane.QUESTION_MESSAGE, null, singleMarket,
                            singleMarket[0]);

                    if (sellerStoreChoice == null) { //CANCELLED
                        break;
                    }

                    storeId = sellerStoreChoice.substring(sellerStoreChoice.indexOf(" " + 1)); //store ID input
                    String productName = addProductNameInputDialog(); //product name input
                    String productDescription = addProductDescriptionInputDialog(); //product description input
                    int productQuantity = Integer.parseInt(addProductQuantityInputDialog()); //quantity input is turned into an INT
                    double productPrice = Double.parseDouble(addProductPriceInputDialog()); //price input is turned into an INT

                    // String productId = db.getMaxProductId();//TODO: |DATABASE| get a new product ID, +1 from the current highest ID

                    // db.addProductBySeller(productId, productName, storeId, productDescription, productQuantity, productPrice);//TODO: |DATABASE| add product to the database for the seller


                } else if (sellerChoice.equals("View Sales")) { // checking sales
                    //lists all the sellers stores, giving a dropdown menu to choose one to view details


                    sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store to View Sales",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, singleMarket,
                            singleMarket[0]);

                    if (sellerStoreChoice == null) { //CANCELLED
                        break;
                    }

                    storeId = sellerStoreChoice.substring(sellerStoreChoice.indexOf(" " + 1));
                    String storeName = sellerStoreChoice.substring(0, sellerStoreChoice.indexOf(" "));
                    String sellerSalesChoice = sellerSalesChoiceInputDialog();

                    if (sellerSalesChoice.equals("View Sales by Product")) {
                        //TODO: "Product ID:" (product ID), "Number of Sales:" (number of sales)
                    } else if (sellerSalesChoice.equals("View Sales by Customer")) {
                        //TODO: "Customer ID:" (customer ID), "Number of Products Purchased:" (number of products purchased)
                    }


                } else if (sellerChoice.equals("View Products in Cart")) {


                    //TODO: view products in all customer carts


                } else if (sellerChoice.equals("Add Store")) {


                    //TODO: add a store


                } else if (sellerChoice.equals("Quit")) { // Quits loop of program

                    thankYouMessageDialog(); //goodbye message window "Thank you for using the seller menu!"

                }

            } catch (Exception e) {
                randomErrorMessageDialog(); //Any exception that occurs throws an error "Error, invalid input!"
            }
        }
        return true;
    }

    //--------------------------------------    GUI METHODS   ------------------------------------------------------

    public static String addProductNameInputDialog() {
        String productNameText;
        do {
            productNameText = JOptionPane.showInputDialog(null, "Enter product name:",
                    "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
            if ((productNameText.isEmpty())) {
                JOptionPane.showMessageDialog(null, "Product name cannot be empty!",
                        "Seller Menu - Add Product",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while ((productNameText.isEmpty()));

        return productNameText;
    }

    public static String addProductDescriptionInputDialog() {
        String productDescriptionText;
        do {
            productDescriptionText = JOptionPane.showInputDialog(null,
                    "Enter product description:", "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
            if ((productDescriptionText.isEmpty())) {
                JOptionPane.showMessageDialog(null, "Product description cannot be empty!",
                        "Seller Menu - Add Product",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while ((productDescriptionText.isEmpty()));

        return productDescriptionText;
    }

    public static String addProductQuantityInputDialog() {
        boolean redo;
        String productQuantityText;
        do {
            do {
                productQuantityText = JOptionPane.showInputDialog(null,
                        "Enter product quantity:", "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
                if ((productQuantityText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Product Quantity cannot be empty!",
                            "Seller Menu - Add Product",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((productQuantityText.isEmpty()));

            redo = false;
            try {
                Integer.parseInt(productQuantityText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid quantity number!",
                        "Seller Menu - Add Product", JOptionPane.ERROR_MESSAGE);
                redo = true;
            }

            if (Integer.parseInt(productQuantityText) < 0) {
                JOptionPane.showMessageDialog(null, "Quantity can't be less than zero!",
                        "Seller Menu - Add Product", JOptionPane.ERROR_MESSAGE);
                redo = true;
            }
        } while (redo);
        return productQuantityText;
    }

    public static String addProductPriceInputDialog() {
        boolean redo;
        String productPriceText;
        do {
            do {
                productPriceText = JOptionPane.showInputDialog(null,
                        "Enter product price:", "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
                if ((productPriceText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Product price cannot be empty!",
                            "Seller Menu - Add Product",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((productPriceText.isEmpty()));

            redo = false;
            try {
                Double.parseDouble(productPriceText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid price!",
                        "Seller Menu - Add Product", JOptionPane.ERROR_MESSAGE);
                redo = true;
            }

            if (Double.parseDouble(productPriceText) < 0) {
                JOptionPane.showMessageDialog(null, "Price can't be less than zero!",
                        "Seller Menu - Add Product", JOptionPane.ERROR_MESSAGE);
                redo = true;
            }
        } while (redo);
        return productPriceText;
    }


    private static final String[] sellerOptions = {"", "View Products", "Remove Products", "Add Products",
            "View Sales", "View Products in Cart", "Add Store", "Quit"};

    public static String sellerChoiceInputDialog() {
        String sellerChoice;

        sellerChoice = (String) JOptionPane.showInputDialog(null,
                "What would you like to do?",
                "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, sellerOptions,
                sellerOptions[0]);

        return sellerChoice;
    }

    private static final String[] sellerSalesChoices = {"", "View Sales by Product", "View Sales by Customer"};

    public static String sellerSalesChoiceInputDialog() {
        String sellerChoice;

        sellerChoice = (String) JOptionPane.showInputDialog(null,
                "What would you like to do?",
                "Seller Menu - Sales", JOptionPane.QUESTION_MESSAGE, null, sellerSalesChoices,
                sellerSalesChoices[0]);

        return sellerChoice;
    }

    public static void randomErrorMessageDialog() {
        JOptionPane.showMessageDialog(null, "Error, invalid input!", "Seller Menu",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void thankYouMessageDialog() {
        JOptionPane.showMessageDialog(null, "Thank You For Using The Seller Menu!",
                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
    }

}
