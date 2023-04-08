import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class Market {
    public static void main(String[] args) {
        int viewChoice;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Are you a customer (1) or seller (2)? ");
            viewChoice = scanner.nextInt();
            scanner.nextLine();

            if (viewChoice != 1 && viewChoice != 2) {
                System.out.println("Error.  Please enter either 1 or 2.");
            }
        } while (viewChoice != 1 && viewChoice != 2);

        // Split of how different types of users have different views
        // Think every user should have these fields
        String username;
        String password;


        if (viewChoice == 1) {  // CUSTOMER
            // loading existing customers
            Customer customer;
            ArrayList<ArrayList<String>> existingUsers = loadCustomers();

            int existingAccount;
            do {
                System.out.println("Do you have an existing account (1 = Yes, 2 = No)? ");
                existingAccount = scanner.nextInt();
                scanner.nextLine();

                if (existingAccount != 1 && existingAccount != 2) {
                    System.out.println("Error.  Please enter either 1 or 2.");
                }
            } while (existingAccount != 1 && existingAccount != 2);



            if (existingAccount == 1) {  // There is an existing account
                int userIndex;  // user index relating username, password, and ids to a specific user

                while (true) {  // USERNAME
                    System.out.println("Enter Username: ");
                    username = scanner.nextLine().strip();
                    if (existingUsers.get(0).contains(username)) {
                        userIndex = existingUsers.get(0).indexOf(username);
                        break;
                    } else {
                        System.out.println("User not found. Try again!\n");
                    }
                }
                while (true) {  // PASSWORD
                    System.out.println("Enter Password: ");
                    password = scanner.nextLine();
                    if (existingUsers.get(1).get(userIndex).equals(password)) {  // If password is correct
                        break;
                    } else {
                        System.out.println("Incorrect Password. Try Again!\n");
                    }
                }

                customer = new Customer(existingUsers.get(0).get(userIndex),
                        existingUsers.get(1).get(userIndex), existingUsers.get(2).get(userIndex));

            } else {  // Creating new account
                do {
                    System.out.println("Enter Username: ");
                    username = scanner.nextLine().strip();
                    if (existingUsers.get(0).contains(username)) {
                        System.out.println("\n ERROR. Username already in use.\n");
                    }
                } while (existingUsers.get(0).contains(username));
                System.out.println("Enter Password: ");
                password = scanner.nextLine().strip();
                int size = existingUsers.get(2).size();
                int newID = Integer.parseInt(existingUsers.get(2).get(size - 1) + 1); // makes an ID 1 more than prev
                String strID = String.format("%s", newID);


                customer = new Customer(username, password, strID);
                customer.addNewCustomer("C:\\Users\\owenw\\OneDrive\\Desktop\\CS 180\\Proj4" +
                        "\\src\\customers.csv");
            }

            // Both should converge here: Should show the market and allow the user to purchase/add to cart
            System.out.println("\n\n\n\n\n"); // just spacing
            ArrayList<ArrayList<String>> marketData = loadMarket();
            String cont;
            while (true) {  // MAIN CUSTOMER LOOP
                String choice = "";
                for (ArrayList<String> line: marketData) {
                    System.out.println("Store: " + line.get(0));
                    System.out.println("Product: " + line.get(1));
                    System.out.println("Description: " + line.get(2));
                    System.out.println("Quantity: " + line.get(3));
                    System.out.println("Price: $" + line.get(4));
                    System.out.println("-----------------");
                }

                System.out.println("What would you like to do?");
                System.out.println("(1) Search");
                System.out.println("(2) Buy");
                System.out.println("(3) Add item to cart");
                System.out.println("ENTER CHOICE: ");
                choice = scanner.nextLine();

                if (choice.equals("1")) {
                    System.out.println("Search: ");
                    String search = scanner.nextLine().strip();
                    ArrayList<ArrayList<String>> results = new ArrayList<>();

                    for (ArrayList<String> line: marketData) {
                        for (String s: line) {
                            if (s.contains(search)) {
                                results.add(line);
                            }
                        }
                    }

                    int counter = 1;
                    for (ArrayList<String> product: results) {
                        System.out.printf("--------[%d]--------\n", counter);
                        System.out.println("Store: " + product.get(0));
                        System.out.println("Product: " + product.get(1));
                        System.out.println("Description: " + product.get(2));
                        System.out.println("Quantity: " + product.get(3));
                        System.out.println("Price: $" + product.get(4));
                        counter++;
                    }


                } else if (choice.equals("2")) {
                    System.out.println("Yay");
                } else {
                    System.out.println("yay");
                }


                System.out.println("\n\nContinue? (1) yes (2) no: ");
                cont = scanner.nextLine();
                if (cont.equals("2")) {
                    break;
                }
            }



        } else {  // SELLER

        }
    }

    public static ArrayList<ArrayList<String>> loadCustomers() {  // Static Method *******
        // This method reads the file and creates arraylist of arraylists of strings
        // index 0 of array list is arraylist holding usernames
        // index 1 = passwords
        // index 2 = id's
        // the second-dimension index is what matches the user's information between inner arraylists

        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<String> passwords = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        String s;
        try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw" +
                "\\OneDrive\\Desktop\\CS 180\\Proj4\\src\\customers.csv"))) {
            while (true) {
                s = bfr.readLine();
                if (s == null) {
                    break;
                }
                usernames.add(s.split(",")[0]);
                passwords.add(s.split(",")[1]);
                ids.add(s.split(",")[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<String>> users = new ArrayList<>();
        users.add(usernames);
        users.add(passwords);
        users.add(ids);

        return users;
    }

    public static ArrayList<ArrayList<String>> loadMarket() {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        String s;
        try (BufferedReader bfr = new BufferedReader(new FileReader("C:\\Users\\owenw\\OneDrive\\Desktop" +
                "\\CS 180\\Proj4\\src\\market.csv"))) {
            while (true) {
                s = bfr.readLine();
                if (s == null) {
                    break;
                }
                ArrayList<String> temp = new ArrayList<String>(Arrays.asList(s.split(",")));
                data.add(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


}
