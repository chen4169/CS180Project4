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
 * @version 1.1 2020/4/15
 * @author Libin Chen
 */
public class Marketplace {
    private static String goodbyeMessage = "Thanks for using our App! Goodbye!";
    private static String getUserData = "01"; //command index to tell the Server to get user data

    private static String dataBasePath =
            "C://Users//Xince//IdeaProjects//CS18000//Database1.accdb";
    private static String hostName = "localhost";
    private static int port = 4242;
    private static Database db;

    public static void main(String[] args) {
        // create a new Database instance
        db = new Database(dataBasePath);

        // create a connection to the server
        try (Socket socket = new Socket(hostName, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // show a message dialog if the connection is successful
            JOptionPane.showMessageDialog(null, "Successfully connected to the server!");

            while (true) {
                // ask the user for their username
                String username = JOptionPane.showInputDialog(null, "Enter your username", "Marketplace Entry",
                        JOptionPane.QUESTION_MESSAGE);
                if (username == null) { // if the user click the cross sign "x", end the program
                    JOptionPane.showMessageDialog(null, goodbyeMessage);
                    return;
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
                        return;
                    }
                } else { // if there is an account matched
                    JOptionPane.showMessageDialog(null, "Username: " + response);
                    if (response.startsWith("S")) {
                        // response starts with "S"
                        SellerClient client = new SellerClient(db, socket, response.substring(1)); // pass the account information accordingly
                        client.start(); // start the client
                    } else if (response.startsWith("C")) {
                        // response starts with "C"
                        CustomerClient client = new CustomerClient(db, socket, response.substring(1)); // pass the account information accordingly
                        client.start(); // start the client
                    }
                    break;
                }
            } // while loop end
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to the server: " + e.getMessage());
            }
    }
}

