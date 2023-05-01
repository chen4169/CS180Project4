import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This is a seller class that provides the interface for a seller to exchange information with the server.
 * @Version 2023/4/30 1.2
 * @author Thomas Eggers, Owen Willis
 */

public class SellerClient {
    private static String searchCartByStoreID = "10";
    private static String searchProductsByStoreID = "12";
    private static String searchMarketsBySellerId = "13";
    private static String removeProduct = "14";
    private static String addProductBySeller = "15";
    private static String searchPurchaseHistoryByStoreId = "16";
    private static String addStore = "17";

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
                } else if (sellerChoice.equals("View Products")) { // seller chooses to view products

                    //dropdown menu JOptionPane that gives the choice in the format of (store name), Store ID:(store ID)
                    String sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store To View",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, stores,
                            stores[0]);

                    if (sellerStoreChoice == null) { //CANCELLED
                        continue;
                    }


                    String storeId = sellerStoreChoice.split(",")[0]; //gets the store ID


                    // Getting products associated with stores
                    out.println(searchProductsByStoreID + storeId);

                    String[] products = in.readLine().split("@");

                    if (!products[0].isEmpty()) {
                        JOptionPane.showMessageDialog(null, products,
                                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "No Products", "Seller Menu",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } else if (sellerChoice.equals("Remove Products")) { // remove products option

                    String sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store To View",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, stores,
                            stores[0]);


                    String storeId = sellerStoreChoice.split(",")[0]; //gets the store ID

                    // Getting products associated with stores
                    out.println(searchProductsByStoreID + storeId);

                    String[] products = in.readLine().split("@");

                    //drop down menu of what product the seller wants to remove
                    String sellerProductRemoval = (String) JOptionPane.showInputDialog(null,
                            "Select a Product to Remove",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, products,
                            products[0]);

                    if (sellerProductRemoval == null) { //CANCELLED
                        break;
                    }

                    String productID = sellerProductRemoval.split(",")[0];
                    out.println(removeProduct + productID);
                    out.flush();

                    try {
                        String resp = in.readLine();
                        JOptionPane.showMessageDialog(null, "Product Removed",
                                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException err) {
                        err.printStackTrace();
                    }


                } else if (sellerChoice.equals("Add Products")) { // adds product



                    //gets the store ID from the seller store choice, the store products will be added to
                    sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store to Add Products",
                            "Seller Menu - Add Products", JOptionPane.QUESTION_MESSAGE, null, stores,
                            stores[0]);

                    if (sellerStoreChoice == null) { //CANCELLED
                        continue;
                    }

                    storeId = sellerStoreChoice.split(",")[0]; // getting store ID form chosen store
                    String productName = addProductNameInputDialog(); //product name input
                    String productDescription = addProductDescriptionInputDialog(); //product description input
                    String productQuantity = addProductQuantityInputDialog(); //quantity input is turned into an INT
                    String productPrice = addProductPriceInputDialog(); //price input is turned into an INT

                    String product = String.join(",", productName, storeId, productDescription,
                            productQuantity, productPrice);

                    out.println(addProductBySeller + product);
                    out.flush();

                    try {
                        String resp = in.readLine(); //
                        JOptionPane.showMessageDialog(null, "Product Added!", "Seller Menu"
                                , JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException err) {
                        err.printStackTrace();
                    }


                } else if (sellerChoice.equals("View Sales")) { // checking sales

                    //select a store to view sales for
                    String sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store To View Sales",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, stores,
                            stores[0]);

                    if (sellerStoreChoice == null) { //CANCELLED
                        break;
                    }

                    String storeId = sellerStoreChoice.split(",")[0]; //gets the store ID

                    String resp = "KEY: \nproductId,storeId,quantity,buyerId,price,name,storeName,orderId\n\n";

                    out.println(searchPurchaseHistoryByStoreId + storeId); //asks for purchase history
                    out.flush();
                    try {
                         resp += in.readLine(); // retrieves purchase history
                    } catch (IOException err) {
                        JOptionPane.showMessageDialog(null, "Error Retrieving Sales",
                                "Seller Menu", JOptionPane.ERROR_MESSAGE);
                    }

                    String[] products = resp.split("@");
                    double revenue = 0;
                    boolean t = true;
                    for (String product: products) {
                        if (product.contains("KEY: \nproductId,storeId,quantity,buyerId" +
                                ",price,name,storeName,orderId\n\n")) {
                            String[] temp = product.split("\n\n");
                            if (temp.length == 1) {
                                t = false;
                                break;
                            }
                            product = temp[1];

                        }
                        revenue += Integer.parseInt(product.split(",")[2]) *
                                Double.parseDouble(product.split(",")[4]);
                    }

                    if (t)
                    { //as long as purchases are not empty
                        JOptionPane.showMessageDialog(null, products,
                                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
                        JOptionPane.showMessageDialog(null,
                                String.format("Store's Revenue: $%.2f",revenue),
                                "Seller Menu - Revenue", JOptionPane.INFORMATION_MESSAGE);
                    } else { //purchases are empty
                        JOptionPane.showMessageDialog(null, "No Sales Made", "Seller Menu",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } else if (sellerChoice.equals("View Products in Cart")) {
                    // id + "," + productId + "," + productName + "," + buyerId + "," + productQuantity + "," +
                    // productPrice + "," + storeId;

                    //gets the store ID from the seller store choice, the store products will be added to
                    sellerStoreChoice = (String) JOptionPane.showInputDialog(null,
                            "Select a Store to View Products From",
                            "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, stores,
                            stores[0]);

                    if (sellerStoreChoice == null) { //CANCELLED
                        continue;
                    }
                    storeId = sellerStoreChoice.split(",")[0]; //store ID input

                    out.println(searchCartByStoreID + storeId);
                    out.flush();

                    String resp = "KEY: \nid,productId,productName,buyerId,productQuantity,productPrice,storeId\n\n";

                    try {
                        resp += in.readLine();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    if (resp.equals("KEY: \nid,productId,productName,buyerId,productQuantity,productPrice,storeId\n\n"))
                    {
                        JOptionPane.showMessageDialog(null, "No Items in Customers' Carts",
                                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        String[] cartStuff = resp.split("@");
                        JOptionPane.showMessageDialog(null, cartStuff,
                                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
                    }


                } else if (sellerChoice.equals("Add Store")) {

                    String storeName = addStoreNameInputDialog();

                    out.println(addStore + storeName + "," + id);
                    out.flush();

                    try {
                        String resp = in.readLine();
                        JOptionPane.showMessageDialog(null, "Store Added!", "Seller Menu",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                } else if (sellerChoice.equals("Import Products")) { // Quits loop of program
                    String filePath = JOptionPane.showInputDialog(null,
                            "Enter FilePath for Import File", "Seller Menu", JOptionPane.QUESTION_MESSAGE);

                    try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
                        // Getting info from the products
                        String prodString = "";
                        String s;
                        while (true) {
                            s = bfr.readLine();
                            if (s == null) {
                                break;
                            }
                            prodString += s + "@";
                        }

                        // Adding Products to Store
                        String[] products = prodString.split("@");

                        for (String product: products) {
                            out.println(addProductBySeller + product);
                            out.flush();

                            try {
                                in.readLine();
                            } catch (IOException err) {
                                JOptionPane.showMessageDialog(null, "Error Adding " + product,
                                        "Seller Menu", JOptionPane.ERROR_MESSAGE);
                            }
                        }



                    } catch (IOException err) {
                        JOptionPane.showMessageDialog(null, "No File Found with Specified Path",
                                "Seller Menu", JOptionPane.QUESTION_MESSAGE);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                randomErrorMessageDialog(); //Any exception that occurs throws an error "Error, invalid input!"
            }
        }
        thankYouMessageDialog(); //goodbye message window "Thank you for using the seller menu!"
        return false;
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
                    "Enter product description:", "Seller Menu - Add Product",
                    JOptionPane.QUESTION_MESSAGE);
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
                        "Enter product quantity:", "Seller Menu - Add Product",
                        JOptionPane.QUESTION_MESSAGE);
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


    private static final String[] sellerOptions = {"View Products", "Remove Products", "Add Products",
            "View Sales", "View Products in Cart", "Add Store", "Import Products"};

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

    public static String addStoreNameInputDialog() {
        String storeName;
        do {
            storeName = JOptionPane.showInputDialog(null, "Enter Store Name:",
                    "Seller Menu - Add Store", JOptionPane.QUESTION_MESSAGE);
            if ((storeName.isEmpty())) {
                JOptionPane.showMessageDialog(null, "Store name cannot be empty!",
                        "Seller Menu - Add Store",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while ((storeName.isEmpty()));

        return storeName;
    }

}
