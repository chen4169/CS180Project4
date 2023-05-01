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
    private final BufferedReader in;
    private final PrintWriter out;
    String id, username, password, trueName;
    Database db;
    Socket socket;
    String sellerStoreChoice;
    String[] singleMarket;
    String storeId;
    ArrayList<String> stores;
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

        try {

            stores = ;//TODO: |DATABASE| need an arraylist that gives each store name and store ID for the seller. Format: "(store name) - Store ID: (store ID)"

            String sellerChoice = sellerChoiceInputDialog(); //what the seller wants to do

            if (sellerChoice.equals("View Products")) { // seller chooses to view products

                singleMarket = new String[stores.size()]; //creates an empty market the size of the stores arraylist

                for (String store : stores) {  //singleMarket is updated with stores from stores arraylist
                    singleMarket = store.split(","); //splits the string by the commas to be inputted into the array
                }

                //dropdown menu JOptionPane that gives the choice in the format of (store name), Store ID: (store ID)
                String sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                        "Select a Store To View",
                        "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, singleMarket,
                        singleMarket[0]);

                String storeId = sellerStoreChoice.substring(sellerStoreChoice.indexOf(" " + 1)); //gets the store ID
                String chosenStore = sellerStoreChoice.substring(0, sellerStoreChoice.indexOf(" ")); //gets the store name

                productsWithDescriptions = ;//TODO: |DATABASE| get an arraylist of products pertaining to a specific store ID.
                // TODO: Format: "(product ID)-1-(product name)-2-(description)-3-(quantity)-4-(price)"

                String[][] products;
                String[] columnNames;

                products[0][0] = "Product ID"; //product id as title
                products[0][1] = "Name"; //product name as title
                products[0][2] = "Description"; //product description as title
                products[0][3] = "Quantity"; //product quantity as title
                products[0][4] = "Price"; //product price as title
                //loops through product array and adds substrings to their position within the 2D array
                for (int i = 0; i < productsWithDescriptions.size(); i++) {
                    products[i + 1][0] = productsWithDescriptions.get(i).substring(0, productsWithDescriptions.get(i).indexOf("1") - 1);
                    products[i + 1][1] = productsWithDescriptions.get(i).substring(productsWithDescriptions.get(i).indexOf("1") + 2, productsWithDescriptions.get(i).indexOf("2") - 1);
                    products[i + 1][2] = productsWithDescriptions.get(i).substring(productsWithDescriptions.get(i).indexOf("2") + 2, productsWithDescriptions.get(i).indexOf("3") - 1);
                    products[i + 1][3] = productsWithDescriptions.get(i).substring(productsWithDescriptions.get(i).indexOf("3") + 2, productsWithDescriptions.get(i).indexOf("4") - 1);
                    products[i + 1][4] = productsWithDescriptions.get(i).substring(productsWithDescriptions.get(i).indexOf("4"));
                }
                
                JOptionPane.showMessageDialog(null, products,
                        "Seller Menu", JOptionPane.INFORMATION_MESSAGE);

            } else if (sellerChoice.equals("Remove Products")) { // remove products option

                sellerProductsWithID = ;//TODO: |DATABASE| get an arraylist of products pertaining to a specific store ID. Format: "(product name) - Product ID: (product ID)"  *formatted exactly*

                sellerProducts = new String[sellerProductsWithID.size()]; //arraylist of products determines size of array

                for (String line : sellerProductsWithID) { //products + ID are assigned to array by splitting the commas
                    sellerProducts = line.split(",");
                }

                //drop down menu of what product the seller wants to remove
                String sellerProductRemoval = (String) JOptionPane.showInputDialog(null,
                        "Select a Product to Remove",
                        "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, sellerProducts,
                        sellerProducts[0]);

                String productID = sellerProductRemoval.substring(sellerProductRemoval.indexOf(" " + 1)); //gets the product ID from the seller product choice

                db.removeProduct(Integer.parseInt(productID)); //TODO: |DATABASE| remove product from the database by input of the product index

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

                storeId = sellerStoreChoice.substring(sellerStoreChoice.indexOf(" " + 1)); //store ID input
                String productName = addProductNameInputDialog(); //product name input
                String productDescription = addProductDescriptionInputDialog(); //product description input
                int productQuantity = Integer.parseInt(addProductQuantityInputDialog()); //quantity input is turned into an INT
                double productPrice = Double.parseDouble(addProductPriceInputDialog()); //price input is turned into an INT

                String productId = db.getMaxProductId();//TODO: |DATABASE| get a new product ID, +1 from the current highest ID

                //TODO: |DATABASE| add product to the database for the seller
                db.addProductBySeller(productId, productName, storeId, productDescription, productQuantity, productPrice); //adds product to the databse

            } else if (sellerChoice.equals("View Sales")) { // checking sales
                //lists all the sellers stores, giving a dropdown menu to choose one to view details
                sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                        "Select a Store",
                        "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, singleMarket,
                        singleMarket[0]);
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

            } else if (sellerChoice.equals("Quit")) { // Quits loop of program
                thankYouMessageDialog(); //goodbye message window "Thank you for using the seller menu!"
            }

        } catch (Exception e) {
            randomErrorMessageDialog(); //Any exception that occurs throws an error "Error, invalid input!"
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
            "View Sales", "View Products in Cart", "Quit"};

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

//    public static String productToRemoveInputDialog() {
//        boolean redo;
//        String productIndexText;
//        do {
//            do {
//                productIndexText = JOptionPane.showInputDialog(null,
//                        "Pick a product to remove by enter the product index:",
//                        "Seller Menu - Remove Product", JOptionPane.QUESTION_MESSAGE);
//                if ((productIndexText.isEmpty())) {
//                    JOptionPane.showMessageDialog(null, "Product index cannot be empty!", "Seller Menu - Remove Product",
//                            JOptionPane.ERROR_MESSAGE);
//                }
//            } while ((productIndexText.isEmpty()));
//
//            redo = false;
//            try {
//                Integer.parseInt(productIndexText);
//            } catch (NumberFormatException e) {
//                JOptionPane.showMessageDialog(null, "Please enter a valid product index!",
//                        "Seller Menu - Remove Product", JOptionPane.ERROR_MESSAGE);
//                redo = true;
//            }
//        } while (redo);
//        return productIndexText;
//    }

    public static void randomErrorMessageDialog() {
        JOptionPane.showMessageDialog(null, "Error, invalid input!", "Seller Menu",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void thankYouMessageDialog() {
        JOptionPane.showMessageDialog(null, "Thank You For Using The Seller Menu!",
                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
    }

}
