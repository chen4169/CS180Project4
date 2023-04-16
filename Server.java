package Project5;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
/**
 * This is the Server class that will create connection and interact with the database.
 * This is a simple version that show how this class can be written for now.
 * @Version 2023/4/16 1.2
 * @author Libin Chen
 */
public class Server {
    private static String dataBasePath =
            "C://Users//Xince//IdeaProjects//CS18000//Database1.accdb";
    private static int port = 4242;
    private static Database db;

    public static void main(String[] args) throws IOException {

        // create a new Database instance
        db = new Database(dataBasePath);

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Waiting for clients to connect...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");
            // create a new thread to handle client requests
            new Thread(new ClientHandler(socket)).start();
        }

    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    if (request.substring(0, 2).equals("01")) {
                        System.out.println("Processing getUserData...");
                        // handle request from Marketplace to check username
                        String username = request.substring(2);
                        String response = db.getUserData(username);
                        out.println(response);
                    }
                    else if (request.substring(0, 2).equals("02")) {
                        System.out.println("Processing addUserData...");
                        // handle request from Marketplace to add new user data
                        String userData = request.substring(2); // remove "02"
                        String response = db.addUserData(userData); // pass the user data as a single input string to the addUserData method
                        out.println(response);
                    }
                    else if (request.equals("updateProductPrice")) {
                        // handle request from SellerClient to update product price

                    } // you might create more else if here........

                } // while loop

            } catch (IOException e) {
                System.out.println("Error handling client request: " + e);
            } finally { // ensure the socket is closed regardless of whether an exception is thrown
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e);
                }
            }
        }
    }
}

