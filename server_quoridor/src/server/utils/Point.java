package server.utils;

/**
 * Created by Valera on 02.02.2016.
 */
public class Point {
    private int x;
    private int y;
    private int numberPlayer;

    public Point(int x, int y, int numberPlayer) {
        this.x = x;
        this.y = y;
        this.numberPlayer = numberPlayer;
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

    public int getNumberPlayer() {
        return numberPlayer;
    }

    public void setNumberPlayer(int numberPlayer) {
        this.numberPlayer = numberPlayer;
    }
}
