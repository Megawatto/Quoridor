package server.domain.DTO;

import org.json.simple.JSONObject;
import server.domain.GameObjModel;

/**
 * Created by Valera on 26.01.2016.
 */

public class GameObj {
    String login;
    String type;
    private int x;
    private int y;
    private int x2;
    private int y2;

    public GameObj() {
    }

    public GameObj(GameObjModel object) {
        this.login = object.getPlayerLogin().getLogin();
        this.type = object.getType();
        this.x = object.getX();
        this.y = object.getY();
        this.x2 = object.getX2();
        this.y2 = object.getY2();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "GameObj{" +
                "login='" + login + '\'' +
                ", type='" + type + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameObj gameObj = (GameObj) o;

        if (x != gameObj.x) return false;
        if (y != gameObj.y) return false;
        if (x2 != gameObj.x2) return false;
        if (y2 != gameObj.y2) return false;
        return type.equals(gameObj.type);

    }
}
