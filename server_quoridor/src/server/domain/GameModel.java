package server.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Valera on 28.01.2016.
 */

@DatabaseTable(tableName = "game")
public class GameModel {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(columnName = "room_id", foreign = true)
    private RoomModel roomId;

    @DatabaseField(columnName = "player_login", foreign = true)
    private PlayerModel playerLogin;

    @DatabaseField
    private String status;

    public GameModel() {
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
}
