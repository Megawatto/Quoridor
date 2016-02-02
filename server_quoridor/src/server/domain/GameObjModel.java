package server.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import server.utils.Point;

/**
 * Created by Valera on 28.01.2016.
 */

@DatabaseTable(tableName = "game_obj")
public class GameObjModel {

    public static final String ROOM_ID_FIELD_NAME = "room_id";
    public static final String PLAYER_LOGIN_FIELD_NAME = "player_login";

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(columnName = "room_id", foreign = true)
    private RoomModel roomId;

    @DatabaseField(columnName = "player_login", foreign = true)
    private PlayerModel playerLogin;

    @DatabaseField
    private String type;

    @DatabaseField
    private Integer x;

    @DatabaseField
    private Integer y;

    @DatabaseField
    private Integer x2;

    @DatabaseField
    private Integer y2;

    public GameObjModel() {
    }

    public GameObjModel(RoomModel roomId, PlayerModel playerLogin, String type, Point point) {
        this.roomId = roomId;
        this.playerLogin = playerLogin;
        this.type = type;
        this.x = x;
        this.x2 = x2;
        this.y = y;
        this.y2 = y2;
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
