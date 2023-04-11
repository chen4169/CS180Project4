/**
* A class with normal setter and getter methods used for creating seller object with information (username, password, id, name). 
* It is like a seller account.
* Sellers will be able to create stores to sell their products and maintain relationships with customers. 
* Users can create, edit, and delete accounts for themselves.
* There should be an email and password associated with each account.
*/
public class Seller {
    private String username;
    private String password;
    private String id;
    private String name;

    public Seller(String username, String password, String id, String name) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.name = name;
    }

    // Getter methods
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    // Setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

