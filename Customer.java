import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Customer {
    String username;
    String password;
    String ID;  // COULD BE INT
    ArrayList<Product> shoppingCart;

    ArrayList<Product> purchased;

    public Customer(String username, String password, String ID) {  // Constructor for if customer exists
        this.username = username;
        this.password = password;
        this.ID = ID;
    }

    public void close() {
        // TODO: This should write shopping cart and purchase history when called.  Read from the fields and write to the files
    }

    public void addNewCustomer(String fileName) {
        String info = String.format("%s,%s,%s\n", this.username, this.password, this.ID);
        String prev = "";
        String s;

        try (BufferedReader bfr = new BufferedReader(new FileReader(fileName))) {
            while (true) {
                s = bfr.readLine();
                if (s == null) {
                    break;
                }
                prev += s + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.println(prev + info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCart(String fileName) {  // FIXME -> This method uses lots of for loops, may be slow
        // Idea is that carts will be linked to users ID numbers in a shopping cart file
        // Can use hashmaps to take advantage of that
        // Link the User ID with the items in the cart -> would probably have to find a good way to store all of the
        // information -> or if products had ID's just link a customer ID w/ product ID's



        String prev = "";
        String s;
        String[] tempCart;
        String tempStr;
        ArrayList<String> keys = new ArrayList<>();


        HashMap<String, String> shoppingCarts = new HashMap<>();

        try (BufferedReader bfr = new BufferedReader(new FileReader(fileName))) {
            while (true) {
                tempStr = "";
                s = bfr.readLine();
                if (s == null) {
                    break;
                }
                tempCart = s.split(",");
                keys.add(tempCart[0]);

                for (int i = 1; i < tempCart.length; i++) {
                    if (i == tempCart.length - 1) {
                        tempStr += tempCart[i];
                    } else {
                        tempStr += tempCart[i] + ",";
                    }
                }
                shoppingCarts.put(tempCart[0], tempStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Gets the new cart based on what user added
        String newCart = "";
        for (int i = 0; i < this.shoppingCart.size(); i++) {
            if (i == this.shoppingCart.size() - 1) {
                newCart += this.shoppingCart.get(i).name;  // Adds product name (+ "," if not last item)
            } else {
                newCart += this.shoppingCart.get(i).name + ",";
            }
        }


        // Assigning or replacing items related to the key corresponding to the user
        String prevCart;
        try {
            prevCart = shoppingCarts.get(this.ID);
            shoppingCarts.replace(this.ID, prevCart + newCart);
        } catch (Exception e) {
            keys.add(this.ID);
            shoppingCarts.put(this.ID, newCart);
        }



        try (PrintWriter pw = new PrintWriter(fileName)) {
            for (String key: keys) {
                pw.printf("%s,%s\n", key, shoppingCarts.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToCart(Product product) {
        this.shoppingCart.add(product);
    }

    public void setShoppingCart(ArrayList<Product> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ArrayList<Product> getShoppingCart() {
        return shoppingCart;
    }

    public void removeFromCart(Product product) {
        this.shoppingCart.remove(product);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getID() {
        return ID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}

class Product {
    String name;
    public Product(String name) {
        this.name = name;
    }
}

class Seller {

}
