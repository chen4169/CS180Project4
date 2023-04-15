package Project5;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;
/**
 * This is a client class that provides the interface for a customer to exchange information with the server.
 * This is a simple version that show how this class can be written for now.
 * @Version 2023/4/15 1.1
 * @author Libin Chen
 */
public class CustomerClient {
    private static String goodbyeMessage = "Thanks for using our App! Goodbye!";
    private Database database;
    private Socket socket;
    private int userID;
    private String username;
    private String password;
    private String trueName;

    /**
     * The userData will  be a string like "1,SellerTest1@gmail,123456,SellerTest1"
     */
    public CustomerClient(Database database, Socket socket, String userData) {
        this.database = database;
        this.socket = socket;
        String[] userDataArray = userData.split(",");
        this.userID = Integer.parseInt(userDataArray[0]);
        this.username = userDataArray[1];
        this.password = userDataArray[2];
        this.trueName = userDataArray[3];
    }

    public void start() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // you might continue to build the program below

            JOptionPane.showMessageDialog(null, "Welcome! Dear customer " + username + "!");
            JOptionPane.showMessageDialog(null, goodbyeMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

