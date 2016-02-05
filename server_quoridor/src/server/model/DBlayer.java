package server.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import server.domain.DTO.GameObj;
import server.domain.GameModel;
import server.domain.GameObjModel;
import server.domain.PlayerModel;
import server.domain.RoomModel;
import server.utils.GameObjUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Valera on 23.01.2016.
 */
public class DBlayer {

    private static Dao<RoomModel, Integer> rooms;
    private static Dao<GameModel, Integer> games;
    private static Dao<PlayerModel, String> players;
    private static Dao<server.domain.GameObjModel, Integer> gameObjs;
    private static ConnectionSource connectionSource;

    public DBlayer() {

    }

    public static void createConnectFromDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:C:/Users/Valera/IdeaProjects/Quoridor/server_quoridor/test.db");
            rooms = DaoManager.createDao(connectionSource, RoomModel.class);
            games = DaoManager.createDao(connectionSource, GameModel.class);
            players = DaoManager.createDao(connectionSource, PlayerModel.class);
            gameObjs = DaoManager.createDao(connectionSource, server.domain.GameObjModel.class);
            rooms.executeRaw("PRAGMA foreign_keys = ON;");
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
        games.create(new GameModel(room, players.queryForId(player.getLogin()), room.getCountPlayer() == 0 ? "MOVE" : "WAIT", room.getCountPlayer()));
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

            DeleteBuilder<server.domain.GameObjModel, Integer> gameObjDeleteBuilder = gameObjs.deleteBuilder();
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

    public synchronized static boolean statusRoom(RoomModel room) throws SQLException {
        rooms.update(room);
        return room.getStatus().equals("START");
    }

    public static void initGameObj(RoomModel room) throws SQLException {
        List<GameModel> gameModels = games.queryForEq(GameModel.ROOM_ID_FIELD_NAME, room.getId());
        Collections.sort(gameModels, new Comparator<GameModel>() {
            @Override
            public int compare(GameModel o1, GameModel o2) {
                return o1.getQueue() < o2.getQueue() ? -1 : 1;
            }
        });

        for (GameModel gameModel : gameModels) {
            if (gameModel.getQueue() == 1) {
                gameModel.setStatus("MOVE");
                games.update(gameModel);
            }
            gameObjs.create(GameObjUtils.createStartPlayerObj(room, gameModel.getPlayer(), gameModel.getQueue()));
        }
    }

    public static void setPositions(GameModel game, GameObj gameObj) throws SQLException {
        GameObjModel newGameObjModel;
        if (gameObj.getType().equals(GameObjUtils.TYPE_OBJ_PLAYER)) {
            newGameObjModel = gameObjs.queryBuilder()
                    .where()
                    .eq(GameObjModel.ROOM_ID_FIELD_NAME, game.getRoom().getId())
                    .and()
                    .eq(GameObjModel.PLAYER_LOGIN_FIELD_NAME, game.getPlayer().getLogin())
                    .and()
                    .eq("type", "player").queryForFirst();
            newGameObjModel.setObj(gameObj);
            gameObjs.update(newGameObjModel);
        } else {
            gameObjs.create(new GameObjModel(game, gameObj));
        }
        UpdateBuilder<GameModel, Integer> gameUpdateBuilder = games.updateBuilder();
        gameUpdateBuilder.updateColumnValue("status", "WAIT").where().eq("room_id", game.getRoom()).and().eq("player_login", game.getPlayer());
        gameUpdateBuilder.update();
        gameUpdateBuilder = games.updateBuilder();
        gameUpdateBuilder.updateColumnValue("status", "MOVE").where().eq("room_id", game.getRoom()).and().ne("player_login", game.getPlayer());
        gameUpdateBuilder.update();
        System.out.println("UPDATE POSITON");

    }

    public static String getPlayerStatus(int roomId, String login) throws SQLException {
        return games.queryBuilder().where().eq("room_id", roomId).and().eq("player_login", login).queryForFirst().getStatus();
    }

    public static List<GameObjModel> getGameObjList(int roomId) throws SQLException {
        List<GameObjModel> plObj = gameObjs.queryForEq("room_id", roomId);
        List<GameObjModel> result = new ArrayList<>();
        for (GameObjModel gameObjModel : plObj) {
            result.add(gameObjModel);
        }
        System.out.println(result);
        return result;
    }

    public static GameObjModel getPlayerObj(String login, boolean opponent) throws SQLException {
        GameObjModel objModel;
        if (!opponent) {
            objModel = gameObjs.queryBuilder().where().eq("player_login", login).and().eq("type", "player").queryForFirst();
        } else {
            objModel = gameObjs.queryBuilder().where().ne("player_login", login).and().eq("type", "player").queryForFirst();
        }
        return objModel;
    }

    public static void setStatusPlayer(TypeStatusMsg statusMsg) {
        switch (statusMsg) {
            case MOVE:
                break;
            case WAIT:
                break;
            case WIN:
                break;
            case LOSE:
                break;
        }
    }

    public static void clearData() {
        try {
            TableUtils.clearTable(connectionSource, GameModel.class);
            TableUtils.clearTable(connectionSource, server.domain.GameObjModel.class);
            TableUtils.clearTable(connectionSource, RoomModel.class);
            UpdateBuilder updPlayer = players.updateBuilder();
            updPlayer.updateColumnValue("active", false);
            updPlayer.update();
            System.out.println("DB CLEAN");
        } catch (SQLException e) {
            throw new RuntimeException("FACK");
        }

    }

    public static RoomModel createRoom() throws SQLException {
        RoomModel newRoom = new RoomModel("test", "WAIT");
        rooms.create(newRoom);
        newRoom.setCountPlayer(0);
        System.out.println("CREATE NEW ROOM ID =" + newRoom.getId());
        return newRoom;
    }

    public static GameModel addPlayerFromGame(RoomModel room, PlayerModel player, String status, int queue) throws SQLException {
        GameModel model = new GameModel(room, player, status, queue);
        games.create(model);
        return model;
    }

    public static void updateStatusRoom(RoomModel room) throws SQLException {
        rooms.update(room);
    }
}

