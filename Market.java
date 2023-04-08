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
        boolean isCustomer;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Enter Username (or 0 to create account):  ");
            username = scanner.nextLine();

            // TODO -> Here look to see if username exists (do-while loop should continue until valid entry)
            // Also should be able to tell whether user has buyer or seller privs
            boolean exists = false;
            isCustomer = true; // Maybe use this boolean to keep track of user type - false if seller


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

                if (username.equals("exists")) {  // FIXME -> If statement should make sure that the username chosen does not already exist
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
                String id = "";  // FIXME -> Should generate seller id for the user
                seller = new Seller(username, password, id, name);
                seller.addAccount();
            }  else {  // Buyer
                String id = "";  // FIXME -> Should generate buyer id for the user
                customer = new Customer(username, password, id, name);
                customer.addAccount("babab");  // Parameter is file name, but I'm not sure what to pass in
            }


        }
        else {  // User has existing account
            String truePassword = "";  // FIXME -> should retrieve the user password based on their username
            String id = "";  // FIXME -> should retrieve the id number corresponding to entered username
            String name = "";  // FIXME -> should retrieve the name corresponding to entered username

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
            ArrayList<String> productData = new ArrayList<>(); // FIXME -> Should load the product data from database
            ArrayList<String> storeData = new ArrayList<>(); // fixme -> Should load store data (To match products to stores)

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
                    System.out.println("Store: " + "");  // FIXME -> Not sure how to link the store name to product through data base
                    System.out.println("Product: " + singleProduct[2]);
                    System.out.println("Description: " + singleProduct[3]);
                    System.out.println("Quantity: " + singleProduct[4]);
                    System.out.println("Price: $" + singleProduct[5]);
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
                    ArrayList<String> results = new ArrayList<>();  // FIXME -> Search with the search term and add the lines to this

                    counter = 1;
                    for (String line: results) {
                        singleProduct = line.split(",");
                        System.out.printf("---------[%d]---------\n", counter);
                        System.out.println("Store: " + "");  // FIXME -> Not sure how to link the store name to product through data base
                        System.out.println("Product: " + singleProduct[2]);
                        System.out.println("Description: " + singleProduct[3]);
                        System.out.println("Quantity: " + singleProduct[4]);
                        System.out.println("Price: $" + singleProduct[5]);
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
                    ArrayList<String> customerCart = new ArrayList<>();  // Load the user's shopping cart

                    System.out.printf("Customer %s's cart:\n", customer.name);
                    String[] tempProd = new String[6]; // indexing mentioned above
                    for (String s: customerCart) {
                        System.out.println("Store" + ""); // FIXME -> again don't know how to match this
                        System.out.println("Product: " + singleProduct[2]);
                        System.out.println("Description: " + singleProduct[3]);
                        System.out.println("Quantity: " + singleProduct[4]);
                        System.out.println("Price: $" + singleProduct[5]);
                    }

                    // TODO: From here add an ability to buy items off the cart (and then remove from the cart if bought)
                } else if (choice.equals("4")) {
                    break;
                }
            }

        } else {  // seller interface

        }

        System.out.println("Thank you for using the market!");
    }
}

class Seller {
    String username;
    String password;
    String id;
    String name;
    public Seller(String username, String password, String id, String name) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.name = name;

    }

    public void addAccount() {
        System.out.println("-------");
    }
}
