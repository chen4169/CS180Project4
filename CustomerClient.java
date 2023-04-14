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
 * @Version 2023/4/13 1.0
 * @author Libin Chen
 */
public class CustomerClient {
    private static String serverAddress = "localhost";
    private static int port = 4242;

    public static void main(String[] args) throws IOException {

        // create connection with the Server
        Socket socket = new Socket(serverAddress, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // send the request to the server
        String request = "getProductList";
        out.println(request);
        out.flush(); // ensures that data is sent to the server immediately

        // get the response from the server
        String response = in.readLine();

        // print it out with GUI interface
        JOptionPane.showMessageDialog(null, "Product List: " + response);

        // close socket
        socket.close();
    }
}

