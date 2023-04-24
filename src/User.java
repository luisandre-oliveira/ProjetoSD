public class User {
    private final String username;
    private final String password;
    private final boolean admin;

    public User(String username, String password, boolean admin ) {
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public boolean getAdmin() { return admin; }
}

