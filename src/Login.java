import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login {
    public Map <String, String> lista;

    public Login() {
        this.lista = new HashMap<>();
        lista.put("luis","1234");
    }

    public boolean checkCredentials(String username, String password) {
        //System.out.println(lista);
        return Objects.equals(lista.get(username), password);
    }

    public void addUser(String username, String password) {
        lista.put(username,password);
    }
}
