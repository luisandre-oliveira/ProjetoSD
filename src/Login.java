import java.util.*;

public class Login {
    public List<User> lista;

    public Login() {
        lista = new ArrayList<>();
    }

    public boolean checkCredentials(String username, String password) {
        boolean flag = false;

        for (User u:lista) {
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

        for (User u: lista) {
            if(Objects.equals(u.getUsername(), username)) {
                flag = u.isUserAdmin();
            }
        }
        return flag;
    }

    public void addUser(User user) {
        lista.add(user);
    }

    public List<User> getLista() {
        return new ArrayList<>(lista);
    }
}
