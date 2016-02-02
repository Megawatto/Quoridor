package server.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Valera on 28.01.2016.
 */

@DatabaseTable(tableName = "game")
public class GameModel {

    public static final String ROOM_ID_FIELD_NAME = "room_id";
    public static final String PLAYER_LOGIN_FIELD_NAME = "player_login";
    public static final String STATUS_FIELD_NAME = "status";
    public static final String QUEUE_FIELD_NAME = "queue";

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(columnName = ROOM_ID_FIELD_NAME, foreign = true)
    private RoomModel roomId;

    @DatabaseField(columnName = PLAYER_LOGIN_FIELD_NAME, foreign = true)
    private PlayerModel playerLogin;

    @DatabaseField(columnName = STATUS_FIELD_NAME)
    private String status;

    @DatabaseField(columnName = QUEUE_FIELD_NAME)
    private Integer queue;

    public GameModel() {
    }

    public GameModel(RoomModel roomId, PlayerModel playerLogin, String status, Integer queue) {
        this.roomId = roomId;
        this.playerLogin = playerLogin;
        this.status = status;
        this.queue = queue;
    }

    public GameModel(RoomModel roomId, PlayerModel playerLogin, String status) {
        this.roomId = roomId;
        this.playerLogin = playerLogin;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQueue() {
        return queue;
    }

    public void setQueue(Integer queue) {
        this.queue = queue;
    }
}
