package project4;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This is a every early version of the program, we will break the codes down into small pieces after enough discussions
 */
public class Market {
    public static void main(String[] args) {
        Customer customer = null;
        Seller seller = null;
        String username;
        String id;
        boolean isCustomer = false;
        String truePassword = null, name = null, buyerId = null, sellerId = null;
        boolean exists = true;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter filepath for the database that will be used:");
        String filepath = scanner.nextLine();

        // create the connection to the database
        Database db = new Database(filepath);

        // ask users if they want to log in or sigh up
        do {
            exists = true;
            username = "-1";
            System.out.println("Enter Username (or 0 to create account):  ");
            username = scanner.nextLine();
            if (!username.equals("0")) { // user want to log in
                String buyerUserInfo = db.searchBuyerData(username);
                String sellerUserInfo = db.searchSellerData(username);
                //String buyerUser = String.valueOf(db.searchDB("Buyers.Buyer_Username", ("Buyers.Buyer_Username = " + username)));
                //String sellerUser = String.valueOf(db.searchDB("Sellers.sellers_Username", ("Sellers.Sellers_Username = " + username)));
                //System.out.println("Welcome Customer: " + buyerUserInfo);
                //System.out.println("Welcome Seller: " + sellerUserInfo);
                if (!sellerUserInfo.isEmpty()) {
                    String[] sellerInfoArray = null;
                    isCustomer = false;
                    //System.out.println("Welcome Seller!");
                    sellerInfoArray = sellerUserInfo.split(",");
                    System.out.println(sellerInfoArray.length);
                    sellerId = sellerInfoArray[0];
                    truePassword = sellerInfoArray[2];
                    name = sellerInfoArray[3];
                } else if (!buyerUserInfo.isEmpty()) {
                    isCustomer = true;
                    //System.out.println("Welcome Customer!");
                    String[] buyerInfoArray = buyerUserInfo.split(",");
                    buyerId = buyerInfoArray[0];
                    //username = buyerInfoArray[1];
                    truePassword = buyerInfoArray[2];
                    name = buyerInfoArray[3];
                } else if (buyerUserInfo.isEmpty() && sellerUserInfo.isEmpty()) { // if no such as an account
                    exists = false;
                    System.out.println("Username not found, please try again(Enter -1 to quit).");
                }
            }
            if (username.equals("-1")) {
                System.out.println("Goodbye!");
                return;
            }
        } while (!username.equals("0") && !exists);

        // User wants to create a new account
        if (username.equals("0")) {
            while (true) {
                System.out.println("Enter User Name: ");
                username = scanner.nextLine();
                String buyerUserInfo = db.searchBuyerData(username);
                String sellerUserInfo = db.searchSellerData(username);
                //String checkUser = String.valueOf(db.searchDB("Buyers.Buyer_Username,Sellers.Sellers_Username", ("Buyers.Buyer_Username = \"" + username + "\" or Sellers.Sellers_Username = \"" + username + "\"")));
                if (buyerUserInfo.isEmpty() && sellerUserInfo.isEmpty()) {
                    System.out.println("User Name is valid.");
                    break;
                } else {
                    System.out.println("User Name has been taken, please enter another one.");
                    username = "-1";
                }
            }
            String accountType;
            System.out.println("Enter Password: ");
            String password = scanner.nextLine();
            System.out.println("Enter Name: ");
            name = scanner.nextLine();
            do {
                System.out.println("Do you want a (1) Seller or (2) Customer account? Enter 1 or 2: ");
                accountType = scanner.nextLine();
                if (!(accountType.equals("1") || accountType.equals("2"))) {
                    System.out.println("Error.  Invalid!");
                }
            } while (!(accountType.equals("1") || accountType.equals("2")));
            if (accountType.equals("1")) {  // Add a Seller account
                String sellerID = db.findMasSellerID();
                db.addSeller(sellerID, username, password, name);
                //id = String.valueOf(db.searchDB("Sellers.Sellers_ID", ("Sellers.Sellers_Username = \"" + username + "\"")));
                seller = new Seller(username, password, sellerID, name);
            } else {  // Add a Buyer account
                String BuyerID = db.findBuyerCounter();
                db.addBuyer(BuyerID, username, password, name);
                //id = String.valueOf(db.searchDB("Buyers.Id", ("Buyers.Buyer_Username = \"" + username + "\"")));
                customer = new Customer(username, password, BuyerID, name);
            }

        } // User wants to create a new account section

        // User has existing account section
        else {
            while(true) {
                System.out.println("Enter Password: ");
                String password = scanner.nextLine();
                if (password.equals(-1)) {
                    return;
                } else if (isCustomer && password.equals(truePassword)) { // create object
                    customer = new Customer(username, truePassword, buyerId, name);
                    break;
                } else if (!isCustomer && password.equals(truePassword)) {
                    seller = new Seller(username, truePassword, sellerId, name);
                    break;
                } else {
                    System.out.println("incorrect password! Please try again (Enter -1 to quit)");
                }
            }

            // TODO: START OF MAIN LOOPS
            if (isCustomer) { // Buyer interface
                ArrayList<String> productData = db.searchProduct(); // all products in the database
                ArrayList<String> marketData = db.loadMarkets(); // all markets in the database
                String choice; // choice of the user

                String[] singleMarket = new String[3]; //to store temp info of market
                String[] singleProduct = new String[6]; //to store temp info of product
                // Index as follows; 0 = Product_ID, 1 = Product_name, 2 = Store_ID, 3 = Product_description,
                // 4 = quantity available, 5 = price)
                System.out.println("====================== Welcome to the Market ======================");

                while (true) {
                    do {
                        int counter = 1; // index for the array list productData
                        for (String line : marketData) { // print out all the markets
                            singleMarket = line.split(","); // an array of a product with info
                            System.out.printf("---------[%d]---------\n", counter);
                            System.out.println("Store ID: " + singleMarket[0]);
                            System.out.println("Store name: " + singleMarket[1]);
                            System.out.println("Seller ID: " + singleMarket[2]);
                            counter++;
                        }

                        System.out.println("What would you like to do?");
                        System.out.println("(1) Search A Product");
                        System.out.println("(2) Choose A Markct (Store) ");
                        System.out.println("(3) View cart");
                        System.out.println("(4) Anything else to quit");
                        System.out.println("ENTER CHOICE: ");
                        choice = scanner.nextLine();

                        if (choice.equals("1")) {  // SEARCHING product
                            System.out.println("Search: ");
                            String search = scanner.nextLine().strip();
                            ArrayList<String> foundProducts = new ArrayList<String>();
                            for (String product : productData) {
                                if (product.contains(search)) {
                                    foundProducts.add(product);
                                }
                            }
                            counter = 1;
                            for (String line : foundProducts) {
                                singleProduct = line.split(",");
                                System.out.printf("---------[%d]---------\n", counter);
                                System.out.println("Store: " + singleProduct[2]);
                                System.out.println("Product ID: " + singleProduct[0]);
                                System.out.println("Product Name: " + singleProduct[1]);
                                System.out.println("Description: " + singleProduct[3]);
                                System.out.println("Quantity: " + singleProduct[4]);
                                System.out.println("Price: $" + singleProduct[5]);
                                counter++;
                            }
                            // CHOOSING PRODUCT FROM SEARCH
                            boolean success;
                            Product product = null;
                            String chosenProductId = null, chosenStoreId = null;
                            double chosenProductPrice = -1;
                            String chosenProductQuantity = null;
                            int chosenProductQuantityInt = -1;
                            do {
                                success = true;
                                System.out.println("Choose a product (Enter number above corresponding product): ");
                                String productChoice = scanner.nextLine();

                                try {
                                    // Transforms choice to index in results and gets/splits the product information into array
                                    int productIndex = Integer.parseInt(productChoice) - 1;
                                    System.out.println("You chosen: " + foundProducts.get(productIndex));
                                    String[] strProd = foundProducts.get(productIndex).split(",");

                                    product = new Product(strProd[0], strProd[1], strProd[3], Integer.parseInt(strProd[4]),
                                            Double.parseDouble(strProd[5]), strProd[2]);
                                    productData.remove(product);  // Temporarily removes the info from productData -> adds back later if quantity did not run out
                                    chosenProductId = strProd[0];
                                    chosenStoreId = strProd[2];
                                    chosenProductPrice = Double.parseDouble(strProd[5]);
                                } catch (Exception e) {
                                    System.out.println("Invalid entry, try again!\n");
                                    success = false;
                                }
                            } while (!success);

                            System.out.println("What would you like to do?");  // FIXME -> do-while error check maybe
                            System.out.println("(1) Add Item to Cart");
                            System.out.println("(2) Purchase Item");
                            System.out.println("(3) Quit Search");
                            choice = scanner.nextLine();

                            if (choice.equals("1")) { //Add Item to Cart
                                //customer.addToCart(product);
                                db.addToCart(buyerId, chosenProductId);
                            } else if (choice.equals("2")) { // Purchase Item
                                do {
                                    try {
                                        System.out.println("How many products you want to buy?");
                                        chosenProductQuantity = scanner.nextLine();
                                        chosenProductQuantityInt = Integer.parseInt(chosenProductQuantity);
                                        if (chosenProductQuantityInt > 0) {
                                            break;
                                        } else {
                                            System.out.println("Invalid input!");
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Input must be a positive integer!");
                                    }

                                } while (true);
                                //customer.addToHistory(product);
                                db.addPurchaseHistory(chosenProductId, chosenStoreId, chosenProductQuantityInt, buyerId, chosenProductPrice);
                                //product.setQuantity(product.getQuantity() - 1);  // replaces the quantity
                                boolean updateProductQuantitySuccess = db.updateProductQuantity(chosenProductId, chosenProductQuantityInt);

                                if (!updateProductQuantitySuccess) {
                                    System.out.println("Product ran out!");
                                } else {
                                    System.out.println("Purchase successed!");
                                    //productData.add(product.toString());
                                }
                            } else {
                                System.out.println("Thank you for using the market!");
                                return; // anything but these two inputs are treated as a quit search
                            }

                        } else if (choice.equals("2")) { // Choose A Markct (Store)
                            while(true) {
                                System.out.println("Choose a market (Enter number above corresponding market): ");
                                String marketChoice = scanner.nextLine();
                                try {
                                    int marketChoiceInt = Integer.parseInt(marketChoice);
                                    if ((marketChoiceInt <= counter) && (marketChoiceInt >= 0)) {
                                        String[] marketChosen = null;
                                        String MarketChosenId = null;
                                        ArrayList<String> foundProducts = new ArrayList<String>();
                                        for (String market : marketData) {
                                            String[] marketArray = market.split(",");
                                            if (marketArray[0].equals(marketChoice)) {
                                                marketChosen = marketArray;
                                                foundProducts = db.searchProductsByMarket(marketChoice);
                                                counter = 1;
                                                for (String line : foundProducts) {
                                                    singleProduct = line.split(",");
                                                    System.out.printf("---------[%d]---------\n", counter);
                                                    System.out.println("Store: " + singleProduct[2]);
                                                    System.out.println("Product ID: " + singleProduct[0]);
                                                    System.out.println("Product Name: " + singleProduct[1]);
                                                    System.out.println("Description: " + singleProduct[3]);
                                                    System.out.println("Quantity: " + singleProduct[4]);
                                                    System.out.println("Price: $" + singleProduct[5]);
                                                    counter++;
                                                }
                                                if (foundProducts.isEmpty()) {
                                                    System.out.println("This store has no products currently.");
                                                    return;
                                                }
                                                boolean success;
                                                Product product = null;
                                                String chosenProductId = null, chosenStoreId = null;
                                                double chosenProductPrice = -1;
                                                String chosenProductQuantity = null;
                                                int chosenProductQuantityInt = -1;
                                                do {
                                                    success = true;
                                                    System.out.println("Choose a product (Enter number above corresponding product) (Enter -1 to quit): ");
                                                    String productChoice = scanner.nextLine();
                                                    if (productChoice.equals("-1")) {
                                                        System.out.println("Goodbye!");
                                                        return;
                                                    }

                                                    try {
                                                        // Transforms choice to index in results and gets/splits the product information into array
                                                        int productIndex = Integer.parseInt(productChoice) - 1;
                                                        //System.out.println("You chosen: " + foundProducts.get(productIndex));
                                                        String[] strProd = foundProducts.get(productIndex).split(",");

                                                        product = new Product(strProd[0], strProd[1], strProd[3], Integer.parseInt(strProd[4]),
                                                                Double.parseDouble(strProd[5]), strProd[2]);
                                                        //productData.remove(product);  // Temporarily removes the info from productData -> adds back later if quantity did not run out
                                                        chosenProductId = strProd[0];
                                                        chosenStoreId = strProd[2];
                                                        chosenProductPrice = Double.parseDouble(strProd[5]);
                                                    } catch (Exception e) {
                                                        System.out.println("Invalid entry, try again!\n");
                                                        success = false;
                                                    }
                                                } while (!success);

                                                System.out.println("What would you like to do?");  // FIXME -> do-while error check maybe
                                                System.out.println("(1) Add Item to Cart");
                                                System.out.println("(2) Purchase Item");
                                                System.out.println("(3) Quit Search");
                                                choice = scanner.nextLine();

                                                if (choice.equals("1")) { //Add Item to Cart
                                                    //customer.addToCart(product);
                                                    db.addToCart(buyerId, chosenProductId);
                                                    System.out.println("Want to continue shoping? (Y/N)");
                                                    String keepShopingChoice = scanner.nextLine();
                                                    if (!keepShopingChoice.equalsIgnoreCase("Y")) {
                                                        System.out.println("Goodbye!");
                                                        return;
                                                    }
                                                } else if (choice.equals("2")) { // Purchase Item
                                                    do {
                                                        try {
                                                            System.out.println("How many products you want to buy?");
                                                            chosenProductQuantity = scanner.nextLine();
                                                            chosenProductQuantityInt = Integer.parseInt(chosenProductQuantity);
                                                            if (chosenProductQuantityInt > 0) {
                                                                break;
                                                            } else {
                                                                System.out.println("Invalid input!");
                                                            }
                                                        } catch (Exception e) {
                                                            System.out.println("Input must be a positive integer!");
                                                        }

                                                    } while (true);
                                                    //customer.addToHistory(product);
                                                    db.addPurchaseHistory(chosenProductId, chosenStoreId, chosenProductQuantityInt, buyerId, chosenProductPrice);
                                                    //product.setQuantity(product.getQuantity() - 1);  // replaces the quantity
                                                    boolean updateProductQuantitySuccess = db.updateProductQuantity(chosenProductId, chosenProductQuantityInt);

                                                    if (!updateProductQuantitySuccess) {
                                                        System.out.println("Product ran out!");
                                                    } else {
                                                        System.out.println("Purchase successed!");
                                                        //productData.add(product.toString());
                                                    }
                                                } else {
                                                    System.out.println("Thank you for using the market!");
                                                    return; // anything but these two inputs are treated as a quit search
                                                }
                                            }
                                        }
                                        break;
                                    }
                                } catch (Exception e) {
                                    System.out.println("Invalid choice!");
                                }
                                System.out.println("No such a market!");
                            } // while loop

                        } else if (choice.equals("3")) { //View cart
                            ArrayList<String> customerCart = db.searchCart(buyerId);
                            System.out.printf("Customer %s's cart:\n", customer.name);
                            for (String s : customerCart) {
                                String[] singleCart = s.split(",");
                                System.out.println("ID: " + singleCart[0]);
                                System.out.println("Buyer ID: " + singleCart[1]);
                                System.out.println("Product ID: " + singleCart[2]);
                            }
                            System.out.println("Go back to the home page? (Y/N)");
                            String backChoice = scanner.nextLine();
                            if (!backChoice.equalsIgnoreCase("Y")) {
                                System.out.println("Thank you for using the market!");
                                return;
                            }

                        } else {
                            System.out.println("Thank you for using the market!");
                            return;
                        }
                    } while(true);
                } // buyer interface ends
            } else {  // seller interface
                while (true) {
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
                        System.out.println("Choose a store (Enter store ID) (Enter -1 to quit): ");
                        String MarketChosenId = scanner.nextLine();
                        if (MarketChosenId.equals("-1")) {
                            System.out.println("Goodbye!");
                            return;
                        }
                        try {
                            int marketChosenIdInt = Integer.parseInt(MarketChosenId);
                            if (marketChosenIdInt >= 0 && marketChosenIdInt <= storeCounter) {
                                String[] marketChosen = null;
                                ArrayList<String> foundProducts = new ArrayList<String>();
                                foundProducts = db.searchProductsByMarket(MarketChosenId);
                                if (foundProducts == null) {
                                    System.out.println("Store not found.");
                                } else {
                                    storeCounter = 1;
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
                                    System.out.println("What would you like to do?");
                                    System.out.println("(1) Remove Products");
                                    System.out.println("(2) Add Products");
                                    System.out.println("(3) View Sales");
                                    System.out.println("(4) Quit");
                                    System.out.println("Enter Choice: ");
                                    String sellerChoice = scanner.nextLine().strip();
                                    if (sellerChoice.equals("1")) { // remove products
                                        System.out.println("Pick a product to remove by enter the product index:");
                                        int productIndex = scanner.nextInt(); // no error check
                                        db.removeProduct(productIndex);
                                    } else if (sellerChoice.equals("2")) { // add item
                                        for (String store : stores) {
                                            System.out.printf("[%d]  STORE NAME\n", storeCounter);
                                            singleMarket = store.split(","); // an array of a product with info
                                            System.out.printf("---------[%d]---------\n", storeCounter);
                                            System.out.println("Store ID: " + singleMarket[0]);
                                            System.out.println("Store name: " + singleMarket[1]);
                                            System.out.println("Seller ID: " + singleMarket[2]);
                                            storeCounter++;
                                        }
                                        System.out.println("Pick a store to add product to (enter Store ID): ");
                                        String storeId = scanner.nextLine();
                                        System.out.println("Enter prodcut name:");
                                        String productName = scanner.nextLine();
                                        System.out.println("Enter prodcut description:");
                                        String productDescription = scanner.nextLine();
                                        String productQuntity = null;
                                        do {
                                            productQuntity = "";
                                            System.out.println("Enter prodcut quntity:");
                                            productQuntity = scanner.nextLine();
                                            if (Integer.parseInt(productQuntity) < 0) {
                                                System.out.println("quntity can't be smaller then zero!");
                                            }
                                        } while (Integer.parseInt(productQuntity) < 0);
                                        System.out.println("Enter prodcut price:");
                                        double productPrice = Double.parseDouble(scanner.nextLine());
                                        String productId = db.getMaxProductId();
                                        db.addProductBySeller(productId, productName, storeId, productDescription, Integer.parseInt(productQuntity), productPrice);
                                    } else if (sellerChoice.equals("3")) { // checking sales
                                        for (String store : stores) {
                                            singleMarket = store.split(","); // an array of stores
                                            db.searchPurchaceHistoryByStoreId(singleMarket[0]);
                                        }
                                    } else if (sellerChoice.equals("4")) { // anything else quits the loop
                                        System.out.println("Thank you for using the market!");
                                        return;
                                    }
                                }
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println("Error, invalid input!");
                        }
                    } while (true);
                }
            }
        }
            System.out.println("Thank you for using the market!");
    }
}
