import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;


public class Market {
    public static void main(String[] args) {
        Customer customer = null;
        Seller seller = null;
        String username;
        String id;
        boolean isCustomer = true;
        boolean exists = false;
        Scanner scanner = new Scanner(System.in);

        HashMap<String, String> storeMap = new HashMap<>(); // Store Map matches store IDs with Store names
        HashMap<String, String> prodMap = new HashMap<>(); // matches product ID with the rest of the line from CSV

        ArrayList<String> buserNames = new ArrayList<>();
        ArrayList<String> bids = new ArrayList<>();
        ArrayList<String> bpasswords = new ArrayList<>();

        ArrayList<String> suserNames = new ArrayList<>();
        ArrayList<String> sids = new ArrayList<>();
        ArrayList<String> spasswords = new ArrayList<>();


        try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Markets.csv"))) {
            String s;

            while (true) {
                s = bfr.readLine();
                if (s == null) {
                    break;
                }
                String[] store = s.split(",");

                storeMap.put(store[0], store[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Buyers.csv"))) {

            String s;

            while (true) {
                s = bfr.readLine();
                if (s == null) {
                    break;
                }
                ArrayList<String> temp = new ArrayList<>();
                String[] user = s.split(",");
                buserNames.add(user[1]);
                bids.add(user[0]);
                bpasswords.add(user[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Sellers.csv"))) {

            String s;

            while (true) {
                s = bfr.readLine();
                if (s == null) {
                    break;
                }
                ArrayList<String> temp = new ArrayList<>();
                String[] user = s.split(",");
                suserNames.add(user[1]);
                sids.add(user[0]);
                spasswords.add(user[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        do {
            System.out.println("Enter Username (or 0 to create account):  ");
            username = scanner.nextLine();

            if (buserNames.contains(username)) {
                exists = true;
                isCustomer = true;
            } else if (suserNames.contains(username)) {
                exists = true;
                isCustomer = false;
            } else if (username.equals("0")) {
                break;
            }
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
                if (buserNames.contains(username) || suserNames.contains(username)) {
                    System.out.println("Username Taken");
                } else {
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

                suserNames.add(username);
                int newid = Integer.parseInt(sids.get(sids.size() - 1) + 1);
                sids.add(String.format("%d" , newid));
                spasswords.add(password);
                int accIndex = buserNames.size() - 1;

                String user = suserNames.get(accIndex);
                id = sids.get(accIndex);
                seller = new Seller(user, password, id, user);

            }  else {  // Buyer
                buserNames.add(username);
                int newid = Integer.parseInt(bids.get(bids.size() - 1) ) + 1;
                bids.add(String.format("%d" , newid));
                bpasswords.add(password);
                int accIndex = buserNames.size() - 1;

                String user = buserNames.get(accIndex);
                id = bids.get(accIndex);

                customer = new Customer(user, password, id, user);
            }


        }
        else {  // User has existing account
            String truePassword = "";
            String name = "";
            if (isCustomer) {
                int accIndex = buserNames.indexOf(username);
                truePassword = bpasswords.get(accIndex);
                id = bids.get(accIndex);

            } else {
                int accIndex = suserNames.indexOf(username);
                truePassword = spasswords.get(accIndex);
                id = bids.get(accIndex);
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
        // Customer -
        //          Right Now:  Allow customers to buy out of the cart
        //          sort by price and store name
        //          export csv file with purchase history
        //          write all the files back to the original locations (updating files) -> implement close method -> cart and buyer files working
        //          doesn't use name
        //          error checking terminal outputs need to be checked to make sure they are correctly describing error
        //
        //          Currently Working -> Login for buyer and creating account
        //                  the writing to cart and account files
        //                  printing out the products
        //
        // Seller -
        //          everything
        if (isCustomer) {
            // Creating arraylist of all product data from CSV files
            ArrayList<String> productData = new ArrayList<>();
            try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Products.csv"))) {

                String s;

                while (true) {
                    s = bfr.readLine();
                    if (s == null) {
                        break;
                    }
                    productData.add(s);
                    prodMap.put(s.split(",")[0], s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Creating arrayLists for user purchase history as well as the entire purchase history for rewriting later
            ArrayList<String> totalHistory = new ArrayList<>();
            try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw\\Desktop\\Proj4\\src\\PurchaseHistory.csv"))) {
                String s;

                while (true) {
                    s = bfr.readLine();
                    if (s == null) {
                        break;
                    }
                    totalHistory.add(s);
                    if (s.split(",")[0].equals(id)) {  // if it belongs to user
                        customer.addToHistory(s);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<String> totalCart = new ArrayList<>();
            try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Cart.csv"))) {
                String s;

                while (true) {
                    s = bfr.readLine();
                    if (s == null) {
                        break;
                    }

                    if (s.split(",")[1].equals(id)) {  // if it belongs to user
                        customer.addToCart(prodMap.get(s.split(",")[2]));
                    }
                    totalCart.add(prodMap.get(s.split(",")[2]) + "," + s.split(",")[1]);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }



            String[] singleProduct = new String[6];
            // Index as follows; 0 = Product_ID, 1 = Product_name, 2 = Store_ID, 3 = Product_description,
            // 4 = quantity available, 5 = price)
            System.out.println("====================== Welcome to the Market ======================");
            int counter;
            String choice;
            while (true) {
                counter = 1;
                for (String line: productData) {
                    singleProduct = line.split(",");
                    System.out.printf("---------[%d]---------\n", counter);
                    System.out.println("Store: " + storeMap.get(singleProduct[2]));
                    System.out.println("Product: " + singleProduct[1]);
                    System.out.println("Description: " + singleProduct[3]);
                    System.out.println("Quantity: " + singleProduct[4]);
                    System.out.println("Price: $" + singleProduct[5]);
                    counter++;
                }

                do { //error checking in do-while loop
                    System.out.println("What would you like to do?");
                    System.out.println("(1) Search");
                    System.out.println("(2) Choose Product");
                    System.out.println("(3) View cart");
                    System.out.println("(4) Export History");
                    System.out.println("(5) Quit");
                    System.out.println("ENTER CHOICE: ");
                    choice = scanner.nextLine();

                    if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4")
                            && !choice.equals("5")) {
                        System.out.println("Error. Please enter a valid selection: 1, 2, 3, or 4.");
                    }
                } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4")
                        && !choice.equals("5"));


                if (choice.equals("1")) {  // SEARCHING
                    System.out.println("Search: ");

                    String search = scanner.nextLine().strip();
                    search = search.toLowerCase();
                    ArrayList<String> results = new ArrayList<>();
                    for (String line: productData) {
                        if (line.toLowerCase().contains(search)) {
                            results.add(line);
                        }
                    }

                    if (results.size() == 0) {
                        System.out.println("No Results!");
                    } else {
                        counter = 1;
                        for (String line : results) {
                            singleProduct = line.split(",");
                            System.out.printf("---------[%d]---------\n", counter);
                            System.out.println("Store: " + storeMap.get(singleProduct[2]));
                            System.out.println("Product: " + singleProduct[1]);
                            System.out.println("Description: " + singleProduct[3]);
                            System.out.println("Quantity: " + singleProduct[4]);
                            System.out.println("Price: $" + singleProduct[5]);
                            counter++;
                        }
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
                        } catch (Exception e) {
                            System.out.println("Invalid entry, try again!\n");
                            success = false;
                        }
                    } while (!success);

                    System.out.println("What would you like to do?");
                    System.out.println("(1) Add Item to Cart");
                    System.out.println("(2) Purchase Item");
                    System.out.println("(3) Quit Search");
                    choice = scanner.nextLine();

                    if (choice.equals("1")) {
                        customer.addToCart(product.toString());
                        totalCart.add(product.toString());
                    } else if (choice.equals("2")) {
                        // # buying to check availability
                        System.out.println("How many would you like to buy: ");
                        int amt = scanner.nextInt();
                        scanner.nextLine();
                        productData.remove(product.toString());  // Temporarily removes the info from productData -> adds back later if quantity did not run out
                        prodMap.remove(product.getId());

                        if (amt <= product.getQuantity()) {

                            product.setQuantity(product.getQuantity() - amt);
                            System.out.println("Product bought!");
                            String newShit = String.format("%s,%s,%s,%s,%s", customer.getId(), product.getId(),
                                    product.getSeller(), amt, product.getPrice());
                            customer.addToHistory(newShit);
                            totalHistory.add(newShit);
                            if (product.getQuantity() != 0) {
                                productData.add(product.toString());
                                prodMap.put(product.getId(), product.toString());
                            }
                        } else {
                            System.out.println("Not enough Items available for purchase");
                        }

                    }  // anything but these two inputs are treated as a quit search

                } else if (choice.equals("2")) { // Choosing item
                    boolean success;
                    Product product = null;


                    do { //do while error check
                        System.out.println("(1) Add Item to Cart");
                        System.out.println("(2) Purchase Item");
                        System.out.println("(3) Quit");
                        choice = scanner.nextLine();
                        if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                            System.out.println("Error. Please enter a valid selection: 1, 2, or 3.");
                        }
                    } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3"));


                    if (choice.equals("1")) {  // Add to cart
                        customer.addToCart(product.toString());
                        totalCart.add(product.toString());

                    } else if (choice.equals("2")) {  // Buy
                        System.out.println("How many would you like to buy: ");
                        int amt = scanner.nextInt();
                        scanner.nextLine();

                        if (amt <= product.getQuantity()) {

                            product.setQuantity(product.getQuantity() - amt);
                            System.out.println("Product bought!");
                            String newShit = String.format("%s,%s,%s,%s,%s", customer.getId(), product.getId(),
                                    product.getSeller(), amt, product.getPrice());
                            customer.addToHistory(newShit);
                            totalHistory.add(newShit);

                            productData.add(product.toString());
                        } else {
                            System.out.println("Not enough Items available for purchase");
                        }
                    } // else quit and continue
                } else if (choice.equals("3")) { // view cart
                    if (customer.shoppingCart.size() != 0) {
                        System.out.printf("Customer %s's cart:\n", customer.username);
                        counter = 1;
                        for (String s : customer.getShoppingCart()) {
                            System.out.printf("---------[%d]---------\n", counter);
                            singleProduct = s.split(",");
                            System.out.println("Store: " + storeMap.get(singleProduct[2]));
                            System.out.println("Product: " + singleProduct[1]);
                            System.out.println("Description: " + singleProduct[3]);
                            System.out.println("Quantity: " + singleProduct[4]);
                            System.out.println("Price: $" + singleProduct[5]);
                            counter++;
                        }

                        System.out.println("Would you like to (1) Buy your cart or (2) remove item from cart or (3) quit? ");
                        choice = scanner.nextLine();

                        if (choice.equals("1")) { // Buying everything at once, assuming a 1 for quantity
                            Product product = null;
                            ArrayList<String> removalStuff = new ArrayList<>();  // need this to remove from total cart
                            for (String s : customer.getShoppingCart()) {

                                // Creating a product using hashmap and product ID #
                                String prodID = s.split(",")[0];
                                String[] strProd = prodMap.get(prodID).split(",");
                                product = new Product(strProd[0], strProd[1], strProd[3], Integer.parseInt(strProd[4]),
                                        Double.parseDouble(strProd[5]), strProd[2]);
                                productData.remove(product.toString());  // Temp removing product from overall list
                                prodMap.remove(prodID);
                                removalStuff.add(product.toString());

                                // Changing quantity and adding to history
                                product.setQuantity(product.getQuantity() - 1);
                                String newShit = String.format("%s,%s,%s,%s,%s", customer.getId(), product.getId(),
                                        product.getSeller(), 1, product.getPrice());
                                customer.addToHistory(newShit);
                                totalHistory.add(newShit);

                                if (!(product.getQuantity() == 0)) {  // only adds back to files if quantity != 0
                                    productData.add(product.toString());
                                    prodMap.put(prodID, product.toString());
                                }
                            }
                            // clear cart (everything bought) from both total and personal carts
                            for (String s : removalStuff) {
                                totalCart.remove(s + "," + customer.getId());
                            }
                            customer.clearCart();

                        } else if (choice.equals("2")) {  // Removing item
                            System.out.println("Select number corresponding to item you want to remove: ");
                            int index = scanner.nextInt() - 1;
                            scanner.nextLine();

                            // Removes product from total cart as well as the customers personal cart
                            totalCart.remove(customer.shoppingCart.get(index));
                            customer.shoppingCart.remove(index);
                        }
                    } else {
                        System.out.println("\n\nCart is empty!  Enter to continue");
                        scanner.nextLine();
                    }

                } else if (choice.equals("4")) { // view purchase history
                    System.out.println("Enter File Name (not including '.'): ");
                    String filename = scanner.nextLine();
                    try (PrintWriter pw = new PrintWriter(filename + ".csv")) {
                        pw.println("BuyerID,ProductID,StoreID,Quantity Bought,Price per Unit");
                        for (String s: customer.getPurchased()) {
                            pw.println(s);  // Writes the purchased to a file specified by the user
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }

            }



            // WRITING BACK TO FILES TO SAVE WHAT HAPPENS (BUYER)

            try (PrintWriter pw = new PrintWriter("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Buyers.csv")){
                int index = 0;
                for (String s: buserNames) {
                    if (index == buserNames.size() - 1) {
                        pw.printf("%s,%s,%s,%s", bids.get(index), s, bpasswords.get(index), s);
                    } else {
                        pw.printf("%s,%s,%s,%s\n", bids.get(index), s, bpasswords.get(index), s);
                    }
                    index++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (PrintWriter pw = new PrintWriter("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Cart.csv")){
                int index = 1;
                for (String s: totalCart) {
                    String[] temp = s.split(",");
                    if (index != totalCart.size()) {
                        pw.printf("%s,%s,%s\n", index, temp[6], temp[0]);
                    } else {
                        pw.printf("%s,%s,%s", index, temp[6], temp[0]);
                    }
                    index++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try (PrintWriter pw = new PrintWriter("C:\\Users\\owenw\\Desktop\\Proj4\\src\\PurchaseHistory.csv"))
            {
                int index = 1;
                for (String s: totalHistory) {
                    if (index != totalHistory.size()) {
                        pw.println(s);
                    } else {
                        pw.print(s);
                    }
                    index++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try (PrintWriter pw = new PrintWriter("C:\\Users\\owenw\\Desktop\\Proj4\\src\\Products.csv")){
                int index = 0;
                for (String s: productData) {
                    if (index != productData.size() - 1) {
                        pw.println(s);
                    } else {
                        pw.print(s);
                    }
                    index++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        else {  // seller interface

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


                String sellerChoice;
                do {
                    System.out.println("What would you like to do?");
                    System.out.println("(1) Remove Products");
                    System.out.println("(2) Add Products");
                    System.out.println("(3) View Sales");
                    System.out.println("(4) Add Store");
                    System.out.println("(5) Quit");
                    System.out.println("Enter Choice: ");
                    sellerChoice = scanner.nextLine().strip(); // AGAiN COULD DO_WHILE TO ERROR CHECK (MAYBE WASTE OF TIME FOR GUI)

                    if (!sellerChoice.equals("1") && !sellerChoice.equals("2") && !sellerChoice.equals("3") &&
                            !sellerChoice.equals("4") && !sellerChoice.equals("5")) {
                        System.out.println("Error. Please enter a valid selection: 1, 2, 3, or 4.");
                    }
                } while (!sellerChoice.equals("1") && !sellerChoice.equals("2") && !sellerChoice.equals("3") &&
                        !sellerChoice.equals("4") && !sellerChoice.equals("5")); // AGAiN COULD DO_WHILE TO ERROR CHECK (MAYBE WASTE OF TIME FOR GUI)

                if (sellerChoice.equals("1")) { // remove products
                    
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


                    System.out.println("Enter Number associated with product you want to remove: ");
                    int removeIndex = scanner.nextInt() - 1;
                    scanner.nextLine();
                    int removeID = Integer.parseInt(products.get(removeIndex).split(",")[0]);
                    db.delete("Markets", ("Markets.Store_ID = " + removeID));

                } else if (sellerChoice.equals("2")) { // add item

                    ArrayList<String> storeNames = db.searchDB("Markets.Store_name, Markets.Store_ID", ("Buyers.Id = \"" + id + "\";"));  // FIXME -> Will this work?

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

                    // FIXME -> Will this work?
                    ArrayList<String> stores = db.searchDB("Markets.Store_name, Markets.Store_ID",
                            ("Buyers.Id = \"" + id + "\";"));

                    for (String s: stores) {
                        String[] store = s.split(",");
                        String sid = store[1]; // store id
                        System.out.println("SALES FOR: " + store[0]); // store's name

                        double totalRev = 0;
                        // FIXME -> Will this work?
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
                    System.out.println("Enter Name of store: ");
                    String newStoreName = scanner.nextLine();

                    // FIXME -> Will this work?
                    db.addMarket(newStoreName, Integer.parseInt(seller.getId()));
                } else { // anything else quits the loop
                    break;
                }
            }
        }
       

        System.out.println("Thank you for using the market!");
    }
}
