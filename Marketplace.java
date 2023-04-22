package Project5;

import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This is the entry of the program, it will determine the user type and
 * pass the Database object and the Server connection to whether CustomerClient or SellerClient
 * @version 1.2 2020/4/21
 * @author Libin Chen
 */
public class Marketplace {
    private static String goodbyeMessage = "Thanks for using our App! Goodbye!";
    private static String getUserData = "01"; //command index to tell the Server to get user data
    private static String addUserData = "02"; //command index to tell the Server to add user data

    private static String dataBasePath =
            "C://Users//Xince//IdeaProjects//CS18000//Database1.accdb";
    private static String hostName = "localhost";
    private static int port = 4242;
    private static Database db;

    public static void main(String[] args) {
        // create a new Database instance
        if (dataBasePath.equals("")) { // if the database address is empty
            dataBasePath = JOptionPane.showInputDialog(null, "Enter the database path", "Marketplace initialization",
                    JOptionPane.QUESTION_MESSAGE);
            if (dataBasePath == null) { // if the user click the cross sign "x", end the program
                JOptionPane.showMessageDialog(null, goodbyeMessage);
                return;
            }
        }
        db = new Database(dataBasePath);

        // create a connection to the server
        try (Socket socket = new Socket(hostName, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // show a message dialog if the connection is successful
            JOptionPane.showMessageDialog(null, "Successfully connected to the server!");

            // ask users if they want to sign up or log in
            int choice = JOptionPane.showOptionDialog(null, "Choose an option:", "Marketplace Entry",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[] {"Sign up", "Log in"}, "Log in");
            if (choice == JOptionPane.YES_OPTION) {
                // handle Sign up button clicked
                boolean success = signup(in, out, socket);
                if (!success) {
                    return;
                }
                login(in, out, socket);
            } else if (choice == JOptionPane.NO_OPTION) {
                // handle Log in button clicked
                login(in, out, socket);
            } else {
                JOptionPane.showMessageDialog(null, goodbyeMessage);
                return;
            }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to the server: " + e.getMessage());
            }
    }

    public static boolean login(BufferedReader in, PrintWriter out, Socket socket) throws IOException {
        while (true) {
            // ask the user for their username
            String username = JOptionPane.showInputDialog(null, "Enter your username", "Marketplace Login",
                    JOptionPane.QUESTION_MESSAGE);
            if (username == null) { // if the user click the cross sign "x", end the program
                JOptionPane.showMessageDialog(null, goodbyeMessage);
                return false;
            }
            // send the username to the server with a leading command index
            out.println(getUserData + username);

            // get the username returned
            String response = in.readLine(); // it will be a string separated by "," and leading by "S" or "C"
            // S means Seller, C means Customer, for example: "S1,SellerTest1@gmail,123456,SellerTest1"

            // print it out with GUI interface
            if (response.equals("")) { // if no account found
                int choice = JOptionPane.showConfirmDialog(null,
                        "Account not found. Do you want to try again?",
                        "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    // if the user want to leave
                    JOptionPane.showMessageDialog(null, goodbyeMessage);
                    return false;
                }
            } else { // if there is an account matched
                JOptionPane.showMessageDialog(null, "Username: " + response);
                if (response.startsWith("S")) {
                    // response starts with "S"
                    //SellerClient client = new SellerClient(socket, response.substring(1)); // pass the account information accordingly
                    //client.start(); // start the client
                } else if (response.startsWith("C")) {
                    // response starts with "C"
                    CustomerClient client = new CustomerClient(socket, response.substring(1)); // pass the account information accordingly
                    client.start(); // start the client
                }
                break;
            }
        } // while loop end
        return true;
    }

    public static boolean signup(BufferedReader in, PrintWriter out, Socket socket) throws IOException {
        String username = JOptionPane.showInputDialog(null, "Enter your username", "Marketplace Signup",
                JOptionPane.QUESTION_MESSAGE);
        if (username == null) { // if the user click the "cancel" button or the input is null
            JOptionPane.showMessageDialog(null, goodbyeMessage);
            return false;
        }

        // send the username to the server with a leading command index
        out.println(getUserData + username);

        // get the username returned
        String response = in.readLine(); // it will be a string separated by "," and leading by "S" or "C"
        // S means Seller, C means Customer, for example: "S1,SellerTest1@gmail,123456,SellerTest1"

        while (!response.equals("")) { // if the username has been taken, let the user to try another username
            JOptionPane.showMessageDialog(null, "This username has already been taken. Please try another one.");
            username = JOptionPane.showInputDialog(null, "Enter your username", "Marketplace Signup",
                    JOptionPane.QUESTION_MESSAGE);
            if (username == null) { // if the user click the "cancel" button or the input is null
                JOptionPane.showMessageDialog(null, goodbyeMessage);
                return false;
            }
            out.println(getUserData + username);
            response = in.readLine();
        }

        // if the username is available, ask for password and trueName
        String password = JOptionPane.showInputDialog(null, "Enter your password", "Marketplace Signup",
                JOptionPane.QUESTION_MESSAGE);
        if (password == null) { // if the user click the "cancel" button or the input is null
            JOptionPane.showMessageDialog(null, goodbyeMessage);
            return false;
        }
        String trueName = JOptionPane.showInputDialog(null, "Enter your full name", "Marketplace Signup",
                JOptionPane.QUESTION_MESSAGE);
        if (trueName == null) { // if the user click the "cancel" button or the input is null
            JOptionPane.showMessageDialog(null, goodbyeMessage);
            return false;
        }
        String data = username + "," + password + "," + trueName;

        // ask the user to choose Customer or Seller
        Object[] options = {"Customer", "Seller"};
        int choice = JOptionPane.showOptionDialog(null, "Are you a Customer or a Seller?", "Marketplace Signup",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.CLOSED_OPTION) { // if the user click the "cancel" button or the input is null
            JOptionPane.showMessageDialog(null, goodbyeMessage);
            return false;
        } else if (choice == JOptionPane.YES_OPTION) { // if the user chooses Customer
            out.println(addUserData + "C" + data); // "02C,username,password,trueName"
            response = in.readLine();
            JOptionPane.showMessageDialog(null, response);
        } else if (choice == JOptionPane.NO_OPTION) { // if the user chooses Seller
            out.println(addUserData + "S" + data);
            response = in.readLine();
            JOptionPane.showMessageDialog(null, response);
        }
        return true;
    }

}

