package quoridor.model;

import java.util.List;

/**
 * Created by Valera on 22.01.2016.
 */
public class Player {
    private String login;
    private List<GameObj> gameObjs;

    public Player(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<GameObj> getGameObjs() {
        return gameObjs;
    }

    public void setGameObjs(List<GameObj> gameObjs) {
        this.gameObjs = gameObjs;
    }
}
