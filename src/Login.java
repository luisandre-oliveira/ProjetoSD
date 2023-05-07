import java.util.*;

public class Login {
    public final List<User> listUsers;

    public Login() {
        listUsers = new ArrayList<>();
    }

    public boolean checkCredentials(String username, String password) {
        boolean flag = false;

        for (User u: listUsers) {
            if(Objects.equals(u.getUsername(), username)) {
                if(Objects.equals(u.getPassword(), password)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    public boolean isUserAdmin(String username) {
        boolean flag = false;

        for (User u: listUsers) {
            if(Objects.equals(u.getUsername(), username)) {
                flag = u.isUserAdmin();
            }
        }
        return flag;
    }

    public void addUser(User user) {
        listUsers.add(user);
    }

    public List<User> getListUsers() {
        return new ArrayList<>(listUsers);
    }
}
