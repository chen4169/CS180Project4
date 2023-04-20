import javax.swing.*;
import java.util.ArrayList;

/**
 * This is a client class that provides interface for a seller to exchange information with the server.
 */
public class SellerClient {

    //TODO: implement ability to select "x" or "cancel" and have the program end without error

    public static void main(String[] args) {

        // seller interface
        while (true) {
            //TODO: put into GUI
            int storeCounter = 1;
            ArrayList<String> stores = db.searchMarketsBySellerId(sellerId);
            String[] singleMarket;
            for (String store : stores) {
                System.out.printf("[%d]  STORE NAME\n", storeCounter);
                singleMarket = store.split(","); // an array of a product with info
                System.out.printf("---------[%d]---------\n", storeCounter);
                System.out.println("Store ID: " + singleMarket[0]);
                System.out.println("Store name: " + singleMarket[1]);
                System.out.println("Seller ID: " + singleMarket[2]);
                storeCounter++;
            }

            do {
                String MarketChosenId = chooseStoreInputDialog();
                try {
                    int marketChosenIdInt = Integer.parseInt(MarketChosenId);
                    if (marketChosenIdInt >= 0 && marketChosenIdInt <= storeCounter) {
                        String[] marketChosen = null;
                        ArrayList<String> foundProducts = new ArrayList<String>();
                        foundProducts = db.searchProductsByMarket(MarketChosenId);
                        if (foundProducts == null) {
                            storeNotFoundMessageDialog();
                        } else {
                            storeCounter = 1;
                            //TODO: put into GUI
                            for (String line : foundProducts) {
                                String[] singleProduct = line.split(",");
                                System.out.printf("---------[%d]---------\n", storeCounter);
                                System.out.println("Store: " + singleProduct[2]);
                                System.out.println("Product ID: " + singleProduct[0]);
                                System.out.println("Product Name: " + singleProduct[1]);
                                System.out.println("Description: " + singleProduct[3]);
                                System.out.println("Quantity: " + singleProduct[4]);
                                System.out.println("Price: $" + singleProduct[5]);
                                storeCounter++;
                            }

                            String sellerChoice = sellerChoiceInputDialog();
                            if (sellerChoice.equals("Remove Products")) { // remove products
                                //TODO: no error check for invalid index
                                int productIndex = Integer.parseInt(productToRemoveInputDialog());
                                db.removeProduct(productIndex);
                            } else if (sellerChoice.equals("Add Products")) { // add item
                                //TODO: put into GUI
                                for (String store : stores) {
                                    System.out.printf("[%d]  STORE NAME\n", storeCounter);
                                    singleMarket = store.split(","); // an array of a product with info
                                    System.out.printf("---------[%d]---------\n", storeCounter);
                                    System.out.println("Store ID: " + singleMarket[0]);
                                    System.out.println("Store name: " + singleMarket[1]);
                                    System.out.println("Seller ID: " + singleMarket[2]);
                                    storeCounter++;
                                }
                                String storeId = addProductStoreIDInputDialog();
                                String productName = addProductNameInputDialog();
                                String productDescription = addProductDescriptionInputDialog();
                                int productQuantity = Integer.parseInt(addProductQuantityInputDialog());
                                double productPrice = Double.parseDouble(addProductPriceInputDialog());
                                String productId = db.getMaxProductId();
                                db.addProductBySeller(productId, productName, storeId, productDescription, productQuantity, productPrice);
                            } else if (sellerChoice.equals("View Sales")) { // checking sales
                                //TODO: implement this properly
                                for (String store : stores) {
                                    singleMarket = store.split(","); // an array of stores
                                    db.searchPurchaceHistoryByStoreId(singleMarket[0]);
                                }
                            } else if (sellerChoice.equals("Quit")) { // Quits loop of program
                                thankYouMessageDialog();
                                return;
                            }
                        }
                        break;
                    }
                } catch (Exception e) {
                    randomErrorMessageDialog();
                }
            } while (true);
        }
        thankYouMessageDialog();
    }

    //--------------------------------------    GUI METHODS   ------------------------------------------------------

    public static String addProductStoreIDInputDialog() {
        boolean redo;
        String storeIDText;
        do {
            do {
                storeIDText = JOptionPane.showInputDialog(null, "Pick a store " +
                        "to add product to (enter Store ID):", "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
                if ((storeIDText == null) || (storeIDText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Store ID cannot be empty!",
                            "Seller Menu - Add Product",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((storeIDText == null) || (storeIDText.isEmpty()));

            redo = false;
            try {
                Integer.parseInt(storeIDText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid store ID!",
                        "Seller Menu - Add Product", JOptionPane.ERROR_MESSAGE);
                redo = true;
            }
        } while (redo);
        return storeIDText;
    }

    public static String addProductNameInputDialog() {
        String productNameText;
            do {
                productNameText = JOptionPane.showInputDialog(null, "Enter product name:",
                        "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
                if ((productNameText == null) || (productNameText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Product name cannot be empty!",
                            "Seller Menu - Add Product",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((productNameText == null) || (productNameText.isEmpty()));

        return productNameText;
    }

    public static String addProductDescriptionInputDialog() {
        String productDescriptionText;
        do {
            productDescriptionText = JOptionPane.showInputDialog(null,
                    "Enter product description:", "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
            if ((productDescriptionText == null) || (productDescriptionText.isEmpty())) {
                JOptionPane.showMessageDialog(null, "Product description cannot be empty!",
                        "Seller Menu - Add Product",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while ((productDescriptionText == null) || (productDescriptionText.isEmpty()));

        return productDescriptionText;
    }

    public static String addProductQuantityInputDialog() {
        boolean redo;
        String productQuantityText;
        do {
            do {
                productQuantityText = JOptionPane.showInputDialog(null,
                        "Enter product quantity:", "Seller Menu - Add Product", JOptionPane.QUESTION_MESSAGE);
                if ((productQuantityText == null) || (productQuantityText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Product Quantity cannot be empty!",
                            "Seller Menu - Add Product",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((productQuantityText == null) || (productQuantityText.isEmpty()));

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
                if ((productPriceText == null) || (productPriceText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Product price cannot be empty!",
                            "Seller Menu - Add Product",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((productPriceText == null) || (productPriceText.isEmpty()));

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


    private static final String[] sellerOptions = {"", "Remove Products", "Add Products",
            "View Sales", "Quit"};

    public static String sellerChoiceInputDialog() {
        String sellerChoice;
        do {
            sellerChoice = (String) JOptionPane.showInputDialog(null,
                    "What would you like to do?(Select from dropdown menu options)",
                    "Seller Menu", JOptionPane.QUESTION_MESSAGE, null, sellerOptions,
                    sellerOptions[0]);
            if (sellerChoice == null || sellerChoice.equals("")) {
                JOptionPane.showMessageDialog(null, "Choice cannot be empty!", "Seller Menu",
                        JOptionPane.ERROR_MESSAGE);
            }

        } while (sellerChoice == null);

        return sellerChoice;
    }

    public static String chooseStoreInputDialog() {
        boolean redo;
        String storeIDText;
        do {
            do {
                storeIDText = JOptionPane.showInputDialog(null, "Choose a store " +
                        "(Enter store ID):", "Seller Menu", JOptionPane.QUESTION_MESSAGE);
                if ((storeIDText == null) || (storeIDText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Store ID cannot be empty!", "Seller Menu",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((storeIDText == null) || (storeIDText.isEmpty()));

            redo = false;
            try {
                Integer.parseInt(storeIDText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid store ID!",
                        "Seller Menu", JOptionPane.ERROR_MESSAGE);
                redo = true;
            }
        } while (redo);
        return storeIDText;
    }

    public static String productToRemoveInputDialog() {
        boolean redo;
        String productIndexText;
        do {
            do {
                productIndexText = JOptionPane.showInputDialog(null,
                        "Pick a product to remove by enter the product index:",
                        "Seller Menu - Remove Product", JOptionPane.QUESTION_MESSAGE);
                if ((productIndexText == null) || (productIndexText.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Product index cannot be empty!", "Seller Menu - Remove Product",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while ((productIndexText == null) || (productIndexText.isEmpty()));

            redo = false;
            try {
                Integer.parseInt(productIndexText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid product index!",
                        "Seller Menu - Remove Product", JOptionPane.ERROR_MESSAGE);
                redo = true;
            }
        } while (redo);
        return productIndexText;
    }

    public static void storeNotFoundMessageDialog() {
        JOptionPane.showMessageDialog(null, "Store Not Found!", "Seller Menu",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void randomErrorMessageDialog() {
        JOptionPane.showMessageDialog(null, "Error, invalid input!", "Seller Menu",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void thankYouMessageDialog() {
        JOptionPane.showMessageDialog(null, "Thank you for using the market!",
                "Seller Menu", JOptionPane.INFORMATION_MESSAGE);
    }

}
