package server.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import server.domain.GameModel;
import server.domain.GameObjModel;
import server.domain.PlayerModel;
import server.domain.RoomModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 23.01.2016.
 */
public class DBlayer {

    private static Dao<RoomModel, Integer> rooms;
    private static Dao<GameModel, Integer> games;
    private static Dao<PlayerModel, String> players;
    private static Dao<GameObjModel, Integer> gameObjs;
    private static ConnectionSource connectionSource;

    public DBlayer() {

    }

    public static void createConnectFromDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            SQLiteDataSource sqLiteDataSource = new SQLiteDataSource(config);

            connectionSource = new JdbcConnectionSource("jdbc:sqlite:C:/Users/Valera/IdeaProjects/Quoridor/server_quoridor/test.db");

            rooms = DaoManager.createDao(connectionSource, RoomModel.class);
            games = DaoManager.createDao(connectionSource, GameModel.class);
            players = DaoManager.createDao(connectionSource, PlayerModel.class);
            gameObjs = DaoManager.createDao(connectionSource, GameObjModel.class);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public static PlayerModel authorization(String login, String password) {
        try {
            players.queryForEq("login", login);
            PlayerModel player = players.queryBuilder().where().eq("login", login).queryForFirst();
            if (player == null) {
                PlayerModel newPlayer = new PlayerModel(login, password, true);
                players.create(newPlayer);
                System.out.println("CREATE NEW PLAYER >>> " + login);
                return newPlayer;
            } else {
                if (player.getPassword().equals(password) && !player.isActive()) {
                    player.setActive(true);
                    players.update(player);
                    System.out.println("ACCESS SUCCESS >>> " + login);
                    return player;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR AUTHORIZATION");
            e.printStackTrace();
            return null;
        }
    }


    public static RoomModel findRoom(PlayerModel player) throws SQLException {
        RoomModel room = rooms.queryBuilder().where().lt("count_pl", 2).queryForFirst();
        if (room == null) {
            rooms.create(new RoomModel("test", "WAIT"));
            room = rooms.queryBuilder().where().lt("count_pl", 2).queryForFirst();
            System.out.println("CREATE NEW ROOM ID =" + room.getId());
        }
        games.create(new GameModel(room, players.queryForId(player.getLogin()), room.getCountPlayer() == 0 ? "MOVE" : "WAIT"));
        room.setCountPlayer(games.queryForEq("room_id", room.getId()).size());
        if (room.getCountPlayer() == 2) {
            room.setStatus("START");
        }
        rooms.update(room);
        return room;
    }

    public static void closeGame(int roomId, String login) {
        try {

            DeleteBuilder<GameModel, Integer> delBul = games.deleteBuilder();
            delBul.where().eq("room_id", roomId);
            delBul.delete();

            DeleteBuilder<GameObjModel, Integer> gameObjDeleteBuilder = gameObjs.deleteBuilder();
            gameObjDeleteBuilder.where().eq("room_id", roomId);
            gameObjDeleteBuilder.delete();

            UpdateBuilder<RoomModel, Integer> roomUpdateBuilder = rooms.updateBuilder();
            roomUpdateBuilder.updateColumnValue("status", "WAIT").updateColumnValue("count_pl", 0).where().eq("id", roomId);
            roomUpdateBuilder.update();

            UpdateBuilder<PlayerModel, String> playerUpdateBuilder = players.updateBuilder();
            playerUpdateBuilder.updateColumnValue("active", false).where().eq("login", login);
            playerUpdateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("CLOSE GAME room =" + roomId + " user=" + login);
    }

    public synchronized static boolean statusRoom(int roomId) throws SQLException {
        RoomModel room = rooms.queryForId(roomId);
        return room.getStatus().equals("START");
    }

    public synchronized static void initGameObj(PlayerModel player, RoomModel room) throws SQLException {
//        TODO Придумать более нормальный способ
        if (games.queryBuilder().where()
                .eq("status", "MOVE")
                .and().eq("room_id", room.getId())
                .and().eq("player_login", player.getLogin())
                .queryForFirst() != null) {
            GameModel opponent = games.queryBuilder().where().eq("room_id", room.getId()).and().ne("player_login", player.getLogin()).queryForFirst();
            gameObjs.create(new GameObjModel(room, player, "player", 5, 1, 0, 0));
            gameObjs.create(new GameObjModel(room, opponent.getPlayerLogin(), "player", 5, 9, 0, 0));
        }
    }

    public static void setPositions(int roomId, String login, GameObj gameObj) throws SQLException {

        if (gameObj.getType().equals("player")) {
            GameObjModel newGameObj = gameObjs.queryBuilder()
                    .where().eq("room_id", roomId).and().eq("player_login", login).and().eq("type", "player").queryForFirst();
            newGameObj.setObj(gameObj);
            gameObjs.update(newGameObj);
        } else {
            gameObjs.create(new GameObjModel(rooms.queryForId(roomId), players.queryForId(login), gameObj));
        }
        UpdateBuilder<GameModel, Integer> gameUpdateBuilder = games.updateBuilder();
        gameUpdateBuilder.updateColumnValue("status", "WAIT").where().eq("room_id", roomId).and().eq("player_login", login);
        gameUpdateBuilder.update();
        gameUpdateBuilder = games.updateBuilder();
        gameUpdateBuilder.updateColumnValue("status", "MOVE").where().eq("room_id", roomId).and().ne("player_login", login);
        gameUpdateBuilder.update();
        System.out.println("UPDATE POSITON");

    }

    public static String getPlayerStatus(int roomId, String login) throws SQLException {
        return games.queryBuilder().where().eq("room_id", roomId).and().eq("player_login", login).queryForFirst().getStatus();
    }

    public static List<GameObj> getGameObjList(int roomId) throws SQLException {
        List<GameObjModel> plObj = gameObjs.queryForEq("room_id", roomId);
        List<GameObj> result = new ArrayList<>();
        for (GameObjModel gameObjModel : plObj) {
            result.add(new GameObj(gameObjModel));
        }
        System.out.println(result);
        return result;
    }

    public static GameObj getPlayerObj(String login, boolean opponent) throws SQLException {
        GameObjModel objModel;
        if (!opponent) {
            objModel = gameObjs.queryBuilder().where().eq("player_login", login).and().eq("type", "player").queryForFirst();
        } else {
            objModel = gameObjs.queryBuilder().where().ne("player_login", login).and().eq("type", "player").queryForFirst();
        }
        return new GameObj(objModel);
    }

    public static List<GameObjModel> getGameObjModelList(int roomId) throws SQLException {
        return gameObjs.queryForEq("room_id", roomId);
    }

    public static void clearData() {
        try {
            TableUtils.clearTable(connectionSource, GameModel.class);
            TableUtils.clearTable(connectionSource, GameObjModel.class);
            TableUtils.clearTable(connectionSource, RoomModel.class);
            UpdateBuilder updPlayer = players.updateBuilder();
            updPlayer.updateColumnValue("active", false);
            updPlayer.update();
            System.out.println("DB CLEAN");
        } catch (SQLException e) {
            throw new RuntimeException("FACK");
        }

    }
}

