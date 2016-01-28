package server.model;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 23.01.2016.
 */
public class DBlayer {

    private static Connection connection;

    public DBlayer() {

    }

    public static void createConnectFromDB() {
        connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Valera/IdeaProjects/Quoridor/server_quoridor/test.db", config.toProperties());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public static boolean authorization(String login, String password) {
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("SELECT * FROM player WHERE login = (?) and password = (?)");
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean("active")) {
                    ps.close();
                    return false;
                } else {
                    ps = connection.prepareStatement("UPDATE player SET active = 1 WHERE login = ?");
                    ps.setString(1, login);
                    ps.execute();
                }
                System.out.println("ACCESS SUCCESS >>> " + login);
                return true;
            } else {
                ps.close();
            }
            ps = connection.prepareStatement("INSERT INTO player(login,password , active) VALUES (?,?,0)");
            ps.setString(1, login);
            ps.setString(2, password);
            ps.execute();
            ps.close();
            System.out.println("CREATE USER " + login);
            return true;
        } catch (SQLException e) {
            System.out.println("ACCESS DENIED");
            e.printStackTrace();
            return false;
        }
    }


    public static int findRoom(String login) throws SQLException {
        PreparedStatement ps;
        int roomId;
        ps = connection.prepareStatement("SELECT * FROM room WHERE count_pl < 2");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            roomId = rs.getInt("id");
            ps = connection.prepareStatement("INSERT INTO game (room_id, player_login,status) VALUES (?,?,?)");
            ps.setInt(1, roomId);
            ps.setString(2, login);
            ps.setString(3, (rs.getInt("count_pl") == 0) ? "MOVE" : "WAIT");
            ps.execute();
            ps.close();
            ps = connection.prepareStatement("UPDATE room SET count_pl = (SELECT COUNT(*) FROM game WHERE game.room_id = room.id)");
            ps.execute();
            ps.close();
            System.out.println("FIND ROOM " + roomId);
            ps = connection.prepareStatement("SELECT room.count_pl FROM room WHERE room.id = ?");
            ps.setInt(1, roomId);
            rs = ps.executeQuery();
            if (rs.getInt("count_pl") == 2) {
                ps.close();
                ps = connection.prepareStatement("UPDATE room SET status = 'START' WHERE id = ?");
                ps.setInt(1, roomId);
                ps.execute();
                ps.close();
                System.out.println("START_GAME");
            }
            return roomId;
        } else {
            ps.close();
            ps = connection.prepareStatement("INSERT INTO room (title) VALUES (?)");
            ps.setString(1, "test");
            ps.execute();
            ps.close();
            System.out.println("CREATE NEW ROOM");
            return findRoom(login);
        }
    }

    public static void closeGame(int roomId, String login) {
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("DELETE FROM game WHERE room_id = ? and player_login = ?");
            ps.setInt(1, roomId);
            ps.setString(2, login);
            ps.execute();
            ps.close();
            ps = connection.prepareStatement("UPDATE room SET status = 'WAIT' , count_pl = (SELECT COUNT(*) FROM game WHERE game.room_id = room.id)");
            ps.execute();
            ps = connection.prepareStatement("UPDATE player SET active = 0 WHERE login = ?");
            ps.setString(1, login);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("CLOSE GAME room =" + roomId + " user=" + login);
    }

    public synchronized static boolean statusRoom(int roomId) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement("SELECT room.status, room.count_pl FROM room WHERE room.id = ?");
        ps.setInt(1, roomId);
        ResultSet rs = ps.executeQuery();
        boolean result = rs.getString("status").equals("START");
        ps.close();
        return result;
    }

    public static void setPositions(int roomId, String login, GameObj gameObj) throws SQLException {
        PreparedStatement ps;
        if (gameObj.getType().equals("player")) {
            ps = connection.prepareStatement("UPDATE game_obj SET x = ? , y = ? WHERE room_id = ? and player_login = ? and type = 'player'");
            ps.setInt(1, gameObj.getX());
            ps.setInt(2, gameObj.getY());
            ps.setInt(3, roomId);
            ps.setString(4, login);
            ps.execute();
            ps.close();
        } else {
            ps = connection.prepareStatement("INSERT INTO game_obj(room_id, player_login, type, x, y, x2, y2) VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, roomId);
            ps.setString(2, login);
            ps.setString(3, gameObj.getType());
            ps.setInt(4, gameObj.getX());
            ps.setInt(5, gameObj.getY());
            ps.setInt(6, gameObj.getX2());
            ps.setInt(7, gameObj.getY2());
            ps.execute();
            ps.close();
        }
        ps = connection.prepareStatement("UPDATE game SET status = 'WAIT' WHERE room_id= ? and player_login = ?");
        ps.setInt(1, roomId);
        ps.setString(2, login);
        ps.execute();
        ps = connection.prepareStatement("UPDATE game SET status = 'MOVE' WHERE room_id= ? and player_login != ?");
        ps.setInt(1, roomId);
        ps.setString(2, login);
        ps.execute();
        ps.close();
        System.out.println("UPDATE POSITON");

    }

    public static String getPlayerStatus(int roomId, String login) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement("SELECT * FROM game WHERE room_id = ? and player_login =? ");
        ps.setInt(1, roomId);
        ps.setString(2, login);
        ResultSet rs = ps.executeQuery();
        return rs.getString("status");
    }

    public static List<GameObj> getGameObjList(int roomId) throws SQLException {
        PreparedStatement ps;
        List<GameObj> result = new ArrayList<>();
        ps = connection.prepareStatement("SELECT * FROM game_obj WHERE room_id = ?");
        ps.setInt(1, roomId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            GameObj gameObj = new GameObj();
            gameObj.setLogin(rs.getString("player_login"));
            gameObj.setType(rs.getString("type"));
            gameObj.setX(rs.getInt("x"));
            gameObj.setY(rs.getInt("y"));
            gameObj.setX2(rs.getInt("x2"));
            gameObj.setY2(rs.getInt("y2"));
            result.add(gameObj);
        }
        ps.close();
        System.out.println(result);
        return result;
    }

    public static void clearData() {

    }
}

