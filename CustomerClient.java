import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * This is a client class that provides the interface for a customer to exchange information with the server.
 * @Version 2023/4/28 1.3
 * @author Owen Willis, Libin Chen
 */
public class CustomerClient extends JComponent implements Runnable {
    private static String goodbyeMessage = "Thanks for using our App! Goodbye!";
    private static String listAllProducts = "09";
    private BufferedReader in;
    private PrintWriter out;
    String id, username, password, trueName;
    Socket socket;


    public CustomerClient(Socket socket, String userData, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        String[] userDataArray = userData.split(",");
        this.id = userDataArray[0];
        this.username = userDataArray[1];
        this.password = userDataArray[2];
        this.trueName = userDataArray[3];
        this.in = in;
        this.out = out;

    }

    /**
     * This method is used to test if the user entered password correctly
     * @return a boolean indicate if the user passed the password test
     */
    public boolean passwordTest() {
        boolean correct = false; // status if the password is entered correctly
        String inputPassword; // the input password

        // prompt the user to enter password
        while (!correct) {
            inputPassword = JOptionPane.showInputDialog(null, "Enter password:");
            if (inputPassword == null) {
                // User clicked the "x" button, so end the program
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
    public void start() {
        SwingUtilities.invokeLater(this);
    }

    public void run()  {
        boolean correct = passwordTest();


        // ask the Server to get all products
        String productList = "";
        try {
            out.println(listAllProducts);
            productList = in.readLine();
            System.out.println(productList);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "Didn't work", "IDK", JOptionPane.PLAIN_MESSAGE);
            err.printStackTrace();
        }
        // example: 1,Vodka,5,Vodka alcohol 1L bottle,50,100@2,Vodka,3,Vodka alcohol 1L bottle,24,125@3,tomato,1,A red fruit,100,5

        // parse the productList string
        String[] products = productList.split("@");
        // example: 1,Vodka,5,Vodka alcohol 1L bottle,50,100
        //--------------------------------------------------------------------

        JOptionPane.showMessageDialog(null, "run1");
        CustGUI gui = new CustGUI();
        if (!correct) { // if the user click to exit
            JOptionPane.showMessageDialog(null, "Goodbye!", "Market", JOptionPane.PLAIN_MESSAGE);
            gui.dispose();
        } else {
            gui.setVisible(true);
        }



        // LISTENERS
        // FIXME - HIDES THE MAIN FRAME -> MAKE VISIBLE LATER (ALSO HANDLE DISPOSING FRAME -> Think is working)
        gui.search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                gui.setVisible(false);
                boolean suc = false;
                String searchWord;

                while (true) {
                    searchWord = JOptionPane.showInputDialog(null, "Search",
                            "Search", JOptionPane.QUESTION_MESSAGE);

                    if (searchWord == null) {  // Cancelled
                        break;
                    } else if (searchWord.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Error! Please enter Valid Input.",
                                "search", JOptionPane.ERROR_MESSAGE);
                    } else {  // Valid Entry
                        break;
                    }
                }
                System.out.println(searchWord);

                if (searchWord != null) {


                    // TODO: Send the search term to the server -> STORE IN ARRAYLIST "r" below (Check if works)
                    out.print("04" + searchWord);
                    out.flush();
                    String[] results;

                    try {
                        results = in.readLine().split("//");

                        // Displaying results in dropdown window
                        String choice = (String) JOptionPane.showInputDialog(null, "Search",
                                "Search", JOptionPane.PLAIN_MESSAGE, null, results, results[0]);

                        // Choosing what to do with the product
                        Object[] options = {"Buy", "Add to Cart", "Cancel"};
                        int option = JOptionPane.showOptionDialog(null,
                                "What would you like to do with the product?", "Search",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

                        // Quantity Available
                        int quantAvail = Integer.parseInt(choice.split(",")[4]);


                        if (option == 0) {  // BUYING

                            // Getting amount
                            int amt = 0;
                            while (true) {
                                try {
                                    String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                                    if (amtAsString == null) { // USER CANCELLING
                                        break;
                                    } else {
                                        amt = Integer.parseInt(amtAsString);
                                    }


                                    if (amt <= quantAvail) {  // if there is enough
                                        break;
                                    } else {  // if there isn't
                                        JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (Exception er) {
                                    JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                                }
                            }


                            if (amt != 0) {
                                // TODO: SEND request to the server to buy specified product and quantity (quantity bought is error checked above
                                //    also add to purchase history
                                String[] c = choice.split(",");
                                String request = String.format("07%s%s%3d%s%5.2f", c[0], c[2], amt, id,
                                        Double.parseDouble(c[5]));
                                out.print(request);
                                out.flush();

                                // request = 06prodidQUANT
                                String prodRequest = String.format("%s%3d", c[0], amt);
                                out.print(prodRequest);
                                out.flush();


                                JOptionPane.showMessageDialog(null, String.format("%d of %s bought!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                            } else {  // Order was cancelled
                                JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                            }

                        } else if (option == 1) { // ADD TO CART
                            // Getting amount
                            int amt = 0;
                            while (true) {
                                try {
                                    String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                                    if (amtAsString == null) { // USER CANCELLING
                                        break;
                                    } else {
                                        amt = Integer.parseInt(amtAsString);
                                    }


                                    if (amt <= quantAvail) {  // if there is enough
                                        break;
                                    } else {  // if there isn't
                                        JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (NumberFormatException er) {
                                    JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                                }
                            }


                            if (amt != 0) {
                                // TODO: Send request to server to add specified quantity to the cart

                                JOptionPane.showMessageDialog(null, String.format("%d of %s added to cart!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                            }

                        } else { // CANCEL
                            JOptionPane.showMessageDialog(null, "Cancelling Order...", "Market",
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                    } catch (Exception err) {
                        JOptionPane.showMessageDialog(null, "Error with Network", "Market", JOptionPane.ERROR_MESSAGE);
                    }
                }

                gui.setVisible(true);
            }
        });


        gui.viewMarket.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                gui.setVisible(false);

                String choice = (String) JOptionPane.showInputDialog(null, "Products:",
                        "Market", JOptionPane.PLAIN_MESSAGE, null, products, products[0]);

                if (choice != null) {
                    int quantAvail = Integer.parseInt(choice.split(",")[4]);

                    String[] op = {"Buy", "Add to Cart"};
                    int buyOrCart = JOptionPane.showOptionDialog(null,
                            "Would you like to Buy or Add to cart?", "Market", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, op, op[1]);

                    if (buyOrCart == 0) { // BUYING
                        int amt = 0;
                        while (true) {
                            try {
                                String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                                if (amtAsString == null) { // USER CANCELLING
                                    break;
                                } else {
                                    amt = Integer.parseInt(amtAsString);
                                }


                                if (amt <= quantAvail) {  // if there is enough
                                    break;
                                } else {  // if there isn't
                                    JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException er) {
                                JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                        if (amt != 0) {
                            // TODO: send server request to specified quantity of product (amt)
                            //    also add to purchase history

                            // ADDING TO HISTORY
                            String[] c = choice.split(",");
                            String request = String.format("07%s%s%3d%s%5.2f", c[0], c[2], amt, id,
                                    Double.parseDouble(c[5]));
                            out.print(request);
                            out.flush();

                            // request = 06prodidQUANT -> UPDATING PRODUCT INFORMATION
                            String prodRequest = String.format("%s%3d", c[0], amt);
                            out.print(prodRequest);
                            out.flush();

                            JOptionPane.showMessageDialog(null, String.format("%d of %s bought!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                        }


                    } else if (buyOrCart == 1) { // adding to cart
                        int amt = 0;
                        while (true) {
                            try {
                                String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                                if (amtAsString == null) { // USER CANCELLING
                                    break;
                                } else {
                                    amt = Integer.parseInt(amtAsString);
                                }


                                if (amt <= quantAvail) {  // if there is enough
                                    break;
                                } else {  // if there isn't
                                    JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException er) {
                                JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                            }
                        }


                        if (amt != 0) {
                            // TODO: Send request to server to add specified quantity to the cart

                            JOptionPane.showMessageDialog(null, String.format("%d of %s added to cart!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }

                gui.setVisible(true);
            }
        });

        gui.viewCart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.setVisible(false);

                // TODO: Change to server request -> Request USER ID's cart
                out.print("05" + id);
                out.flush();
                try {

                    String cartString = in.readLine();
                    String[] cart = cartString.split(",");

                    if (cart.length > 0) {


                        String cartChoice = (String) JOptionPane.showInputDialog(null, "Search",
                                "Search", JOptionPane.PLAIN_MESSAGE, null, cart, cart[0]);

                        if (cartChoice != null) {  // If they don't cancel
                            // TODO: Server request to get product based on product ID - store in "choice" variable
                            //      PRODUCT ID = cartChoice.split(",")[2];

                            String choice = "";

                            int quantAvail = Integer.parseInt(choice.split(",")[4]);

                            String[] op = {"Buy", "Add to Cart"};
                            int buyOrCart = JOptionPane.showOptionDialog(null,
                                    "Would you like to Buy or Add to cart?", "Market", JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, op, op[1]);

                            if (buyOrCart == 0) { // BUYING
                                int amt = 0;
                                while (true) {
                                    try {
                                        String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                                        if (amtAsString == null) { // USER CANCELLING
                                            break;
                                        } else {
                                            amt = Integer.parseInt(amtAsString);
                                        }


                                        if (amt <= quantAvail) {  // if there is enough
                                            break;
                                        } else {  // if there isn't
                                            JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } catch (NumberFormatException er) {
                                        JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                                    }
                                }

                                if (amt != 0) {
                                    // TODO: send server request to specified quantity of product (amt)
                                    //    also add to purchase history

                                    // ADDING TO HISTORY
                                    String[] c = choice.split(",");
                                    String request = String.format("07%s%s%3d%s%5.2f", c[0], c[2], amt, id,
                                            Double.parseDouble(c[5]));
                                    out.print(request);
                                    out.flush();

                                    // request = 06prodidQUANT -> UPDATING PRODUCT INFORMATION
                                    String prodRequest = String.format("%s%3d", c[0], amt);
                                    out.print(prodRequest);
                                    out.flush();

                                    JOptionPane.showMessageDialog(null, String.format("%d of %s bought!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                                }


                            } else if (buyOrCart == 1) { // adding to cart
                                int amt = 0;
                                while (true) {
                                    try {
                                        String amtAsString = JOptionPane.showInputDialog(null, "Enter Quantity", "Market", JOptionPane.QUESTION_MESSAGE);

                                        if (amtAsString == null) { // USER CANCELLING
                                            break;
                                        } else {
                                            amt = Integer.parseInt(amtAsString);
                                        }


                                        if (amt <= quantAvail) {  // if there is enough
                                            break;
                                        } else {  // if there isn't
                                            JOptionPane.showMessageDialog(null, "Not Enough Instock!", "Market", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } catch (NumberFormatException er) {
                                        JOptionPane.showMessageDialog(null, "Error! Invalid Entry!", "Market", JOptionPane.ERROR_MESSAGE);
                                    }
                                }


                                if (amt != 0) {
                                    // TODO: Send request to server to add specified quantity to the cart


                                    JOptionPane.showMessageDialog(null, String.format("%d of %s added to cart!", amt, choice), "Market", JOptionPane.PLAIN_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Order Canceled.", "Market", JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Your Cart is Empty", "Market", JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "Error Retrieving Cart", "Market", JOptionPane.ERROR_MESSAGE);
                }

                gui.setVisible(true);
            }
        });

        gui.export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.setVisible(false);

                // Getting file information
                String exportFileName = JOptionPane.showInputDialog(null,
                        "Enter file name you want to export to (before the '.'): ", "Market",
                        JOptionPane.QUESTION_MESSAGE);
                if (!(exportFileName == null)) {
                    exportFileName += ".csv";

                    // TODO: server request for history related to id -> store in ArrayList "history" below
                    out.print("03" + id);

                    try {
                        String[] history = in.readLine().split("@");

                        // Writing to file
                        try (PrintWriter pw = new PrintWriter(exportFileName)) {
                            pw.println("BuyerID,ProductID,StoreID,Quantity Bought,Price per Unit"); // Guidelines
                            for (String s : history) {
                                pw.println(s);
                            }

                            JOptionPane.showMessageDialog(null, "File Written to!", "Market",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException er) {
                            JOptionPane.showMessageDialog(null, "Error when exporting", "Market",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException err) {
                        JOptionPane.showMessageDialog(null, "Error when retrieving History", "Market", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Canceling Export", "Market",
                            JOptionPane.PLAIN_MESSAGE);
                }
                gui.setVisible(true);

            }
        });

        gui.sort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.setVisible(false);

                String[] op = {"Quantity", "Price"};
                int sortType = JOptionPane.showOptionDialog(null,
                        "How would you like to sort market?", "Market", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, op, op[1]);

                if (sortType == 0 || sortType == 1) {

                    int imp;
                    int index = 0;

                    if (sortType == 0) { // By quantity
                        imp = 4;
                    } else {
                        imp = 5;
                    }

                    while (true) {
                        if (index == products.length - 1) {
                            break;
                        }
                        if (Double.parseDouble(products[index].split(",")[imp]) >
                                Double.parseDouble(products[index + 1].split(",")[imp])) {
                            String temp = products[index];
                            products[index] = products[index + 1];
                            products[index + 1] = temp;
                            if (index != 0) {
                                index--;
                            }
                        } else {
                            index++;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Market Sorted! Check 'View Market' to see.", "Market", JOptionPane.INFORMATION_MESSAGE);


                }
                gui.setVisible(true);
            }
        });
    }

}
