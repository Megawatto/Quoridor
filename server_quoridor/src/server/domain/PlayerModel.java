package server.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Valera on 28.01.2016.
 */

@DatabaseTable(tableName = "player")
public class PlayerModel {

    @DatabaseField(id = true)
    private String login;

    @DatabaseField
    private String password;

    @DatabaseField
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
