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
        boolean isCustomer;
        boolean exists = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter filepath for the database that will be used:");
        String filepath = scanner.nextLine();
        Database db = new Database(filepath);
        do {
            System.out.println("Enter Username (or 0 to create an account):  ");
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
                System.out.println("Error.  Please enter a username or "0" to create an account.");
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
            
            String password;
            do {
                System.out.println("Enter Password: ");
                password = scanner.nextLine();
                if (password.equals("")) {
                    System.out.println("Error. Password cannot be empty!");
                }
            } while (password.equals(""));
            
            String name;
            do {
                System.out.println("Enter Name: ");
                name = scanner.nextLine();
                if (name.equals("")) {
                    System.out.println("Error. Name cannot be empty!");
                }
            } while(name.equals(""));

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
            if (isCustomer) {
                String truePassword = String.valueOf(db.searchDB("Buyers.Buyer_Password", ("Buyers.Buyer_Username = \"" + username + "\"")));
                id = String.valueOf(db.searchDB("Buyers.Id", ("Buyers.Buyer_Username = \"" + username + "\"")));
                String name = String.valueOf(db.searchDB("Buyers.Buyer_Name", ("Buyers.Buyer_Username = \"" + username + "\"")));
            } else {
                String truePassword = String.valueOf(db.searchDB("Sellers.Sellers_Password", ("Sellers.Sellers_Username = \"" + username + "\"")));
                id = String.valueOf(db.searchDB("Sellers.Sellers_ID", ("Sellers.Sellers_Username = \"" + username + "\"")));
                String name =  String.valueOf(db.searchDB("Sellers.Sellers_Name", ("Sellers.Sellers_Username = \"" + username + "\"")));
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

                    do {
                        System.out.println("What would you like to do?");  // do-while error check on client selections
                        System.out.println("(1) Add Item to Cart");
                        System.out.println("(2) Purchase Item");
                        System.out.println("(3) Quit Search");
                        choice = scanner.nextLine();
                        
                        if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                            System.out.println("Error. Please enter a valid selection: 1, 2, or 3.");
                        }
                    } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3"));

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

                   do {
                    System.out.println("(1) Add Item to Cart");
                    System.out.println("(2) Purchase Item");
                    System.out.println("(3) Quit");
                    choice = scanner.nextLine();

                    if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                        System.out.println("Error. Please enter a valid selection: 1, 2, or 3.");
                    }
                } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3"));

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
                    String produtcBuy = scanner.nextLine();
                    System.out.println("How many items would you like to buy");
                    int numberProducts = scanner.nextInt();
                    scanner.nextLine();
                    int Availability = Integer.parseInt(String.valueOf(db.searchDB("Products.Quantity_available", ("Products.Product_name = \"" + produtcBuy + "\", Buyers.Id = \"" + id + "\";"))));
                    if (numberProducts <= Availability) {
                        String productID = String.valueOf(db.searchDB("Products.Product_ID", ("Products.Product_name = \"" + produtcBuy + "\", Buyers.Id = \"" + id + "\";")));
                        db.Update("Products", ("" + (Availability - numberProducts)), productID);
                        System.out.println("Product bought.");
                    } else {
                        System.out.println("Not enough Items available for purchase");
                    }
                    break;
                }
            }

        } else {  // seller interface

            while (true) {
                int storeCounter = 1;
                for (String store : stores) {
                    System.out.printf("[%d]  STORE NAME\n", storeCounter);
                    ArrayList<String> products = db.searchDB("Products.Product_name, Products.Product_description, Markets.Store_name, Products.Price, Products.Quantity_available", ("Buyers.Id = \"" + id + "\";"));
                    String[] tempProd = new String[6]; // indexing mentioned above

                    for (String product : products) {
                        tempProd = product.split(",");
                        System.out.println("Store" + tempProd[2]);
                        System.out.println("Product: " + tempProd[0]);
                        System.out.println("Description: " + tempProd[1]);
                        System.out.println("Quantity: " + tempProd[4]);
                        System.out.println("Price: $" + tempProd[3]);
                    }
                    storeCounter++;
                }

                System.out.println("What would you like to do?");
                System.out.println("(1) Remove Products");
                System.out.println("(2) Add Products");
                System.out.println("(3) View Sales");
                System.out.println("(4) Quit");
                System.out.println("Enter Choice: ");
                String sellerChoice = scanner.nextLine().strip(); // AGAiN COULD DO_WHILE TO ERROR CHECK (MAYBE WASTE OF TIME FOR GUI)

                if (sellerChoice.equals("1")) { // remove products
                    System.out.println("Pick a store to remove product from (enter # associated w/ store): ");
                    int storeIndex = scanner.nextInt() - 1; // no error check
                    scanner.nextLine();
                    String[] store = new String[3];
                    store = stores.get(storeIndex).split(",");
                    int storeId = Integer.parseInt(store[0]);


                    int prodCounter = 1;
                    ArrayList<String> storeProducts = db.searchDB("Products.Product_name, Products.Product_description, Products.Price, Products.Quantity_available, Markets.Store_name", ("Markets.Store_ID = \"" + storeId + "\";")); // Get products attached to the store id
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

                    System.out.println("Enter Number associated with product you want to remove: ");
                    int removeIndex = scanner.nextInt() - 1;
                    scanner.nextLine();
                    int removeID = Integer.parseInt(storeProducts.get(removeIndex).split(",")[0]);
                    db.delete("Markets", ("Markets.Store_ID = " + removeID));

                } else if (sellerChoice.equals("2")) { // add item
                    System.out.println("Pick a store to add product to (enter # associated w/ store): ");
                    int storeIndex = scanner.nextInt() - 1; // no error check
                    scanner.nextLine();
                    String[] store = new String[3];
                    store = stores.get(storeIndex).split(",");
                    int storeId = Integer.parseInt(store[0]);

                    System.out.println("Enter Product Name: ");
                    String name = scanner.nextLine();
                    System.out.println("Enter Product Description: ");
                    String desc = scanner.nextLine();
                    System.out.println("Enter Quantity Available: ");
                    int quant = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter Price");
                    int price = scanner.nextInt();
                    scanner.nextLine();

                    db.addProduct(name, storeIndex, desc, quant, price);

                } else if (sellerChoice.equals("3")) { // checking sales
                    // FIXME - SHOULD READ FROM THE USER PURCHASE HISTORY AND MATCH STUFF FROM SELLER OWNED STORES

                } else { // anything else quits the loop
                    break;
                }
            }
        }

        System.out.println("Thank you for using the market!");
    }
}
