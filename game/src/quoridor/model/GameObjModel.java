package quoridor.model;

/**
 * Created by Valera on 28.01.2016.
 */

public class GameObjModel {

    private Integer id;

    private RoomModel roomId;

    private PlayerModel playerLogin;

    private String type;

    private Integer x;

    private Integer y;

    private Integer x2;

    private Integer y2;

    public GameObjModel() {
    }

    public GameObjModel(RoomModel roomId, PlayerModel playerLogin, String type, Integer x, Integer y, Integer x2, Integer y2) {
        this.roomId = roomId;
        this.playerLogin = playerLogin;
        this.type = type;
        this.x = x;
        this.x2 = x2;
        this.y = y;
        this.y2 = y2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoomModel getRoomId() {
        return roomId;
    }

    public void setRoomId(RoomModel roomId) {
        this.roomId = roomId;
    }

    public PlayerModel getPlayerLogin() {
        return playerLogin;
    }

    public void setPlayerLogin(PlayerModel playerLogin) {
        this.playerLogin = playerLogin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getX2() {
        return x2;
    }

    public void setX2(Integer x2) {
        this.x2 = x2;
    }

    public Integer getY2() {
        return y2;
    }

    public void setY2(Integer y2) {
        this.y2 = y2;
    }

}
