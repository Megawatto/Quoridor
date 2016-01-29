package server.model;

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

    public GameObj(JSONObject object) {
        this.type = (String) object.get("type");
        this.x = Math.toIntExact((Long) object.get("x"));
        this.y = Math.toIntExact((Long) object.get("y"));
        this.x2 = Math.toIntExact((Long) object.get("x2"));
        this.y2 = Math.toIntExact((Long) object.get("y2"));
    }

    public GameObj(GameObjModel plObj) {
        this.login = plObj.getPlayerLogin().getLogin();
        this.type = plObj.getType();
        this.x = plObj.getX();
        this.y = plObj.getY();
        this.x2 = plObj.getX2();
        this.y2 = plObj.getY2();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameObj gameObj = (GameObj) o;

        if (x != gameObj.x) return false;
        if (y != gameObj.y) return false;
        if (x2 != gameObj.x2) return false;
        if (y2 != gameObj.y2) return false;
        if (!login.equals(gameObj.login)) return false;
        return type.equals(gameObj.type);

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
}
