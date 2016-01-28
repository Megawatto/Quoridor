package server.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import server.domain.GameModel;
import server.domain.GameObjModel;
import server.domain.PlayerModel;
import server.domain.RoomModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 23.01.2016.
 */
public class DBlayer {

    private static Connection connection;
    private static ConnectionSource connectionSource;
    private static Dao<RoomModel, Integer> rooms;
    private static Dao<GameModel, Integer> games;
    private static Dao<PlayerModel, String> players;
    private static Dao<GameObjModel, Integer> gameObjs;

    public DBlayer() {

    }

    public static void createConnectFromDB() {
        connection = null;
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

    public static boolean authorization(String login, String password) {
        try {
            Dao<PlayerModel, String> players = DaoManager.createDao(connectionSource, PlayerModel.class);
            players.queryForEq("login", login);
            PlayerModel player = players.queryBuilder().where().eq("login", login).queryForFirst();
            if (player == null) {
                players.create(new PlayerModel(login, password, true));
                System.out.println("CREATE NEW PLAYER >>> " + login);
                return true;
            } else {
                if (player.getPassword().equals(password) && !player.isActive()) {
                    player.setActive(true);
                    players.update(player);
                    System.out.println("ACCESS SUCCESS >>> " + login);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR LOGIN");
            e.printStackTrace();
            return false;
        }
    }


    public static int findRoom(String login) throws SQLException {
        RoomModel room = rooms.queryBuilder().where().lt("count_pl", 2).queryForFirst();
        if (room == null) {
            rooms.create(new RoomModel("test", "WAIT"));
            room = rooms.queryBuilder().where().lt("count_pl", 2).queryForFirst();
            System.out.println("CREATE NEW ROOM ID =" + room.getId());
        }
        games.create(new GameModel(room, players.queryForId(login), "WAIT"));
        room.setCountPlayer(games.queryForEq("room_id", room.getId()).size());
        if (room.getCountPlayer() == 2) {
            room.setStatus("START");
        }
        rooms.update(room);
        return room.getId();
    }

    public static void closeGame(int roomId, String login) {
        try {
            games.deleteBuilder().where().eq("room_id", roomId).and().eq("player_login", login);
            rooms.updateBuilder().updateColumnValue("status", "WAIT").updateColumnValue("count_pl", 0);
            players.updateBuilder().updateColumnValue("active", false).where().eq("login", login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("CLOSE GAME room =" + roomId + " user=" + login);
    }

    public synchronized static boolean statusRoom(int roomId) throws SQLException {
        RoomModel room = rooms.queryForId(roomId);
        return room.getStatus().equals("START");
    }

    public static void setPositions(int roomId, String login, GameObj gameObj) throws SQLException {

        if (gameObj.getType().equals("player")) {
            GameObjModel newGameObj = gameObjs.updateBuilder()
                    .where().eq("room_id", roomId).and().eq("player_login", login).and().eq("type", "player").queryForFirst();
            newGameObj.setObj(gameObj);
            gameObjs.update(newGameObj);
        } else {
            gameObjs.create(new GameObjModel(rooms.queryForId(roomId), players.queryForId(login), gameObj));
        }
        games.updateBuilder().updateColumnValue("status", "WAIT").where().eq("room_id", roomId).and().eq("player_login", login);
        games.updateBuilder().updateColumnValue("status", "MOVE").where().eq("room_id", roomId).and().ne("player_login", login);
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

    public static void clearData() {

    }
}

