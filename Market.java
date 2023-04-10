import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Market {
    public static void main(String[] args) {
        Customer customer = null;
        Seller seller = null;
        String username;
        String id;
        boolean isCustomer = true;
        boolean exists = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter filepath for the database that will be used:");
        String filepath = scanner.nextLine();

        // FIXME -> Remove after testing
        filepath = "C:\\Users\\owenw\\OneDrive\\Desktop\\CS 180\\Proj4\\src\\Database1.accdb";
        Database db = new Database(filepath);
        do {
            System.out.println("Enter Username (or 0 to create account):  ");
            username = scanner.nextLine();
            String buyerUser = String.valueOf(db.searchDB("Buyers.Buyer_Username", ("Buyers.Buyer_Username = " + username) ));
            String sellerUser = String.valueOf(db.searchDB("Sellers.sellers_Username", ("Sellers.Sellers_Username = " + username) ));
            if (!buyerUser.isEmpty()) {
                isCustomer = false;
            } else if (!sellerUser.isEmpty()) {
                isCustomer = true;
            } else {
                exists = false;
            }
            //
            // Also should be able to tell whether user has buyer or seller privs

            if (!username.equals("0") && !exists) {
                System.out.println("Error.  Please enter either 1 or 2.");
            }
        } while (!username.equals("0") && !exists);

        // Split of how different types of users have different views
        // Think every user should have these fields

        if (username.equals("0")) {  // User wants to create a new account

            // User information -> Same for buyer/seller accounts
            while (true) {
                System.out.println("Enter User Name: ");
                username = scanner.nextLine();
                String checkUser = String.valueOf(db.searchDB("Buyers.Buyer_Username,Sellers.sellers_Username", ("Buyers.Buyer_Username = \"" + username + "\" or Sellers.sellers_Username = \"" + username + "\"")));
                if (!checkUser.isEmpty()) {
                    break;
                }
            }
            System.out.println("Enter Password: ");
            String password = scanner.nextLine();
            System.out.println("Enter Name: ");
            String name = scanner.nextLine();


            // Choosing account type
            String accountType;
            do {
                System.out.println("Do you want a (1) Seller or (2) Customer account? ");
                accountType = scanner.nextLine();
                if (!(accountType.equals("1") || accountType.equals("2"))) {
                    System.out.println("Error.  Invalid!");
                }
            } while (!(accountType.equals("1") || accountType.equals("2")));


            if (accountType.equals("1")) {  // Seller
                db.addSeller(username, password, name);
                id = String.valueOf(db.searchDB("Sellers.Sellers_ID", ("Sellers.Sellers_Username = \"" + username + "\"")));
                seller = new Seller(username,password,id,name);
            }  else {  // Buyer
                db.addBuyer(username, password, name);
                id = String.valueOf(db.searchDB("Buyers.Id", ("Buyers.Buyer_Username = \"" + username + "\"")));
                customer = new Customer(username, password, id, name);
            }


        }
        else {  // User has existing account
            String truePassword = "";
            String name = "";
            if (isCustomer) {
                truePassword = String.valueOf(db.searchDB("Buyers.Buyer_Password", ("Buyers.Buyer_Username = \"" + username + "\"")));
                id = String.valueOf(db.searchDB("Buyers.Id", ("Buyers.Buyer_Username = \"" + username + "\"")));
                name = String.valueOf(db.searchDB("Buyers.Buyer_Name", ("Buyers.Buyer_Username = \"" + username + "\"")));
            } else {
                truePassword = String.valueOf(db.searchDB("Sellers.Sellers_Password", ("Sellers.Sellers_Username = \"" + username + "\"")));
                id = String.valueOf(db.searchDB("Sellers.Sellers_ID", ("Sellers.Sellers_Username = \"" + username + "\"")));
                name =  String.valueOf(db.searchDB("Sellers.Sellers_Name", ("Sellers.Sellers_Username = \"" + username + "\"")));
            }


            String guessPassword;
            while (true) {  // Entering PASSWORD
                System.out.println("Enter Password: ");
                guessPassword = scanner.nextLine();
                if (guessPassword.equals(truePassword)) {  // If password is correct
                    break;
                } else {
                    System.out.println("\tIncorrect Password. Try Again!\n");
                }
            }

            if (isCustomer) {  // creating customer object if user is a customer
                customer = new Customer(username, truePassword, id, name);
            } else {  // creating seller object if user is seller
                seller = new Seller(username, truePassword, id, name);
            }
        }


        // TODO: START OF MAIN LOOPS
        if (isCustomer) {
            ArrayList<String> productData = db.searchDB("Products.Product_name, Products.Product_description, " +
                    "Products.Quantity_available, Products.Price, Markets.Store_name", "");

            String choice;

            String[] singleProduct = new String[6];
            // Index as follows; 0 = Product_ID, 1 = Product_name, 2 = Store_ID, 3 = Product_description,
            // 4 = quantity available, 5 = price)
            System.out.println("====================== Welcome to the Market ======================");
            int counter = 1;

            while (true) {
                for (String line: productData) {
                    singleProduct = line.split(",");
                    System.out.printf("---------[%d]---------\n", counter);
                    System.out.println("Store: " + singleProduct[4]);
                    System.out.println("Product: " + singleProduct[0]);
                    System.out.println("Description: " + singleProduct[1]);
                    System.out.println("Quantity: " + singleProduct[2]);
                    System.out.println("Price: $" + singleProduct[3]);
                    counter++;
                }

                System.out.println("What would you like to do?");
                System.out.println("(1) Search");
                System.out.println("(2) Choose Product");
                System.out.println("(3) View cart");
                System.out.println("(4) Quit");
                System.out.println("ENTER CHOICE: ");
                choice = scanner.nextLine();

                if (choice.equals("1")) {  // SEARCHING
                    System.out.println("Search: ");
                    String search = scanner.nextLine().strip();
                    ArrayList<String> results = db.searchDB("Products.Product_name, Products.Product_description, " +
                            "Products.Quantity_available, Products.Price, Markets.Store_name", ("Products.Product_name = \"" + search + "\""));

                    counter = 1;
                    for (String line: results) {
                        singleProduct = line.split(",");
                        System.out.printf("---------[%d]---------\n", counter);
                        System.out.println("Store: " + singleProduct[4]);
                        System.out.println("Product: " + singleProduct[0]);
                        System.out.println("Description: " + singleProduct[1]);
                        System.out.println("Quantity: " + singleProduct[2]);
                        System.out.println("Price: $" + singleProduct[3]);
                        counter++;
                    }

                    // CHOOSING PRODUCT FROM SEARCH
                    boolean success;
                    Product product = null;
                    do {
                        success = true;
                        System.out.println("Choose a product (Enter number above corresponding product): ");
                        String productChoice = scanner.nextLine();

                        try {
                            // Transforms choice to index in results and gets/splits the product information into array
                            int productIndex = Integer.parseInt(productChoice) - 1;
                            String[] strProd = results.get(productIndex).split(",");

                            product = new Product(strProd[0], strProd[1], strProd[3], Integer.parseInt(strProd[4]),
                                    Double.parseDouble(strProd[5]), strProd[2]);
                            productData.remove(product);  // Temporarily removes the info from productData -> adds back later if quantity did not run out
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

                    if (choice.equals("1")) {
                        customer.addToCart(product);
                    } else if (choice.equals("2")) {
                        customer.addToHistory(product);
                        product.setQuantity(product.getQuantity() - 1);  // replaces the quantity
                        if (product.getQuantity() > 0) {
                            System.out.println("Product ran out!");
                        } else {
                            productData.add(product.toString());
                        }

                    }  // anything but these two inputs are treated as a quit search

                } else if (choice.equals("2")) { // BUYING
                    boolean success;
                    Product product = null;
                    do {
                        success = true;
                        System.out.println("Choose a product (Enter number above corresponding product): ");
                        String productChoice = scanner.nextLine();

                        try {
                            // Transforms choice to index in results and gets/splits the product information into array
                            int productIndex = Integer.parseInt(productChoice) - 1;
                            String[] strProd = productData.get(productIndex).split(",");

                            product = new Product(strProd[0], strProd[1], strProd[3], Integer.parseInt(strProd[4]),
                                    Double.parseDouble(strProd[5]), strProd[2]);
                            productData.remove(product);  // Temporarily removes the info from productData -> adds back later if quantity did not run out
                        } catch (Exception e) {
                            System.out.println("Invalid entry, try again!\n");
                            success = false;
                        }
                    } while (!success);

                    System.out.println("(1) Add Item to Cart");
                    System.out.println("(2) Purchase Item");
                    System.out.println("(3) Quit");
                    choice = scanner.nextLine();

                    if (choice.equals("1")) {  // Add to cart
                        customer.addToCart(product);
                    } else if (choice.equals("2")) {  // Buy
                        customer.addToHistory(product);
                        product.setQuantity(product.getQuantity() - 1);  // replaces the quantity
                        if (product.getQuantity() > 0) {  // Puts quantity back in the market arraylist if item isn't out of stock
                            System.out.println("Product ran out!");
                        } else {
                            productData.add(product.toString());
                        }
                    } // else quit and continue
                } else if (choice.equals("3")) {
                    ArrayList<String> customerCart = db.searchDB(" Markets.Store_name, Products.Product_name, Products.Product_description, Products.Quantity_available, Products.Price", ("Buyers.Id = " + id));  // Load the user's shopping cart

                    System.out.printf("Customer %s's cart:\n", customer.name);
                    String[] tempProd = new String[6]; // indexing mentioned above
                    for (String s: customerCart) {
                        System.out.println("Store" + singleProduct[0]);
                        System.out.println("Product: " + singleProduct[1]);
                        System.out.println("Description: " + singleProduct[2]);
                        System.out.println("Quantity: " + singleProduct[3]);
                        System.out.println("Price: $" + singleProduct[4]);
                    }

                } else if (choice.equals("4")) {
                    ArrayList<String> results = db.searchDB("Products.Product_name, ", (" Buyers.Id = " + id + ";"));
                    System.out.println("What Item would you like to buy from: " + results);
                    String productBuy = scanner.nextLine();
                    System.out.println("How many items would you like to buy");
                    int numberProducts = scanner.nextInt();
                    scanner.nextLine();
                    int Availability = Integer.parseInt(String.valueOf(db.searchDB("Products.Quantity_available", ("Products.Product_name = \"" + productBuy + "\", Buyers.Id = \"" + id + "\";"))));
                    if (numberProducts <= Availability) {
                        String productID = String.valueOf(db.searchDB("Products.Product_ID", ("Products.Product_name = \"" + productBuy + "\", Buyers.Id = \"" + id + "\";")));
                        db.Update("Products", ("" + (Availability - numberProducts)), productID);
                        System.out.println("Product bought.");
                    } else {
                        System.out.println("Not enough Items available for purchase");
                    }
                    break;
                }
            }

        } else {  // seller interface
            id = seller.getId();
            while (true) {
                int prodCounter = 1;
                ArrayList<String> products = db.searchDB("Products.Product_name, Products.Product_description, Markets.Store_name, Products.Price, Products.Quantity_available", ("Buyers.Id = \"" + id + "\";"));
                String[] tempProd = new String[6]; // indexing mentioned above

                for (String product : products) {
                    System.out.printf("=============={Prod #: %d}==============\n", prodCounter);
                    tempProd = product.split(",");
                    System.out.println("Store" + tempProd[2]);
                    System.out.println("Product: " + tempProd[0]);
                    System.out.println("Description: " + tempProd[1]);
                    System.out.println("Quantity: " + tempProd[4]);
                    System.out.println("Price: $" + tempProd[3]);
                }


                System.out.println("What would you like to do?");
                System.out.println("(1) Remove Products");
                System.out.println("(2) Add Products");
                System.out.println("(3) View Sales");
                System.out.println("(4) Add Store");
                System.out.println("(5) Quit");
                System.out.println("Enter Choice: ");
                String sellerChoice = scanner.nextLine().strip(); // AGAiN COULD DO_WHILE TO ERROR CHECK (MAYBE WASTE OF TIME FOR GUI)

                if (sellerChoice.equals("1")) { // remove products
                    /*
                    System.out.println("Pick a store to remove product from (enter # associated w/ store): ");
                    int storeIndex = scanner.nextInt() - 1; // no error check
                    scanner.nextLine();
                    String[] store = new String[3];

                    int storeId = Integer.parseInt(store[0]);


                    int prodCounter = 1;
                    ArrayList<String> storeProducts = db.searchDB("Products.Product_name, Products.Product_description, Products.Price, Products.Quantity_available, Markets.Store_name", ("Markets.Store_ID = \"" + storeId + "\";")); // Get products attached to the store id
                    store = storeProducts.get(storeIndex).split(",");
                    for (String strProd: storeProducts) {
                        System.out.printf("---------[%d]---------\n", prodCounter);
                        String[] tempProd = new String[6]; // indexing mentioned above
                        tempProd = strProd.split(",");
                        System.out.println("Store:" + store[4]);
                        System.out.println("Product: " + tempProd[0]);
                        System.out.println("Description: " + tempProd[1]);
                        System.out.println("Quantity: " + tempProd[3]);
                        System.out.println("Price: $" + tempProd[2]);
                        prodCounter++;
                    }
                     */

                    System.out.println("Enter Number associated with product you want to remove: ");
                    int removeIndex = scanner.nextInt() - 1;
                    scanner.nextLine();
                    int removeID = Integer.parseInt(products.get(removeIndex).split(",")[0]);
                    db.delete("Markets", ("Markets.Store_ID = " + removeID));

                } else if (sellerChoice.equals("2")) { // add item

                    ArrayList<String> storeNames = new ArrayList<>();  // FIXME -> GET ALL STORE NAMES ASSOCIATED TO SELLER -> format: "storeName,storeID"
                    storeNames = db.searchDB("Markets.Store_name, Markets.Store_ID", ("Buyers.Id = \"" + id + "\";"));

                    int storeCounter = 1;
                    for (String s: storeNames) {
                        System.out.printf("=============={Store #: %d}==============\\n\"", storeCounter);
                        System.out.println(s.split(",")[0]);
                        storeCounter++;
                    }

                    System.out.println("Pick a store to add product to (enter # corresponding to): ");
                    int storeIndex = scanner.nextInt() - 1;
                    scanner.nextLine();

                    String[] chosenStore = storeNames.get(storeIndex).split(",");
                    int storeID = Integer.parseInt(chosenStore[1]); // no error check

                    System.out.println("Enter Product Name: ");
                    String name = scanner.nextLine();
                    System.out.println("Enter Product Description: ");
                    String desc = scanner.nextLine();
                    System.out.println("Enter Quantity Available: ");
                    int quant = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Price: $");
                    int price = scanner.nextInt();
                    scanner.nextLine();

                    db.addProduct(name, storeID, desc, quant, price);

                } else if (sellerChoice.equals("3")) { // checking sales by store
                    // FIXME - SHOULD READ FROM THE USER PURCHASE HISTORY AND MATCH STUFF FROM SELLER OWNED STORES
                    double entireRev = 0;
                    ArrayList<String> stores = db.searchDB("Markets.Store_name, Markets.Store_ID",
                            ("Buyers.Id = \"" + id + "\";"));

                    for (String s: stores) {
                        String[] store = s.split(",");
                        String sid = store[1]; // store id
                        System.out.println("SALES FOR: " + store[0]); // store's name
                        
                        double totalRev = 0;
                        ArrayList<String> storeProducts = db.searchDB("Products.Product_ID, Products.Product_name", ("Products.Store_ID = \"" + sid + "\";"));;  // Use store ID And name to display sales by store
                        
                        for (String prod: storeProducts) {
                            tempProd = prod.split(",");
                            ArrayList<String> purchases = new ArrayList<>(); // FIXME -> NEEDS TO BE ADDED TO THE DATABASE - should find all the user purchase
                            for (String p: purchases) {
                                String[] pur = p.split(",");  // INDEXING: 0 = product ID, 1 = buyer ID, 2 = buyer username, 3 = store ID, 4 = quantity, 5 = price, 6 = product name
                                
                                totalRev += Double.parseDouble(pur[3]) * Double.parseDouble(pur[4]);
                                System.out.printf("\t%s Purchased %s of %s\n", pur[2], pur[4], tempProd[1]);
                            }
                        }
                        System.out.printf("Total Revenue for %s: %.2f\n\n", store[0], totalRev);
                        entireRev += totalRev;
                    }
                    System.out.printf("Overall Seller Revenue: %.2f\n\n", entireRev);

                } else if (sellerChoice.equals("4")) {
                    // TODO: Check this is correct
                    System.out.println("Enter Name of store: ");
                    String newStoreName = scanner.nextLine();

                    db.addMarket(newStoreName, Integer.parseInt(seller.getId()));
                } else { // anything else quits the loop
                    break;
                }
            }
        }

        System.out.println("Thank you for using the market!");
    }
}
