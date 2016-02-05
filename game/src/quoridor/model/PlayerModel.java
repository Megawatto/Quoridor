package quoridor.model;

/**
 * Created by Valera on 28.01.2016.
 */

public class PlayerModel {

    private String login;

    private String password;

    private boolean active;

    public PlayerModel() {
    }

    public PlayerModel(String login, String password, boolean active) {
        this.login = login;
        this.password = password;
        this.active = active;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
