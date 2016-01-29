package server.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.domain.PlayerModel;
import server.domain.RoomModel;
import server.logic.GameUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Valera on 22.01.2016.
 */
public class Session extends Thread {

    private Socket socket;
    private static final Logger LOGGER = Logger.getLogger(Session.class.getName());
//    TODO Переделать ид на объекты
    private RoomModel room;
    private String login;
    private String msg;
    private PlayerModel player;

    public Session(Socket socket) {
        this.socket = socket;
    }

    public static Session createSession(Socket socket) {
        LOGGER.info("New session " + socket);
        return new Session(socket);
    }

    @Override
    public void run() {
        super.run();
        JSONParser parser = new JSONParser();
        LOGGER.info("Start Session " + this.getName());
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);

            while (true) {
                System.out.println("WAIT MSG >>>");
                msg = in.readLine();
                if (msg == null || msg.equals("end")) {
                    socket.close();
                    break;
                }
                LOGGER.info(msg);
                JSONObject object = (JSONObject) parser.parse(msg);

                switch ((String) object.get("msg_type")) {
                    case "login":
                        login = (String) object.get("login");
                        String password = (String) object.get("password");
                        this.player = login != null ? DBlayer.authorization(login, password) : null;
                        if (player == null) {
                            System.out.println("ACCESS DENIED");
                            throw new RuntimeException("ACCESS DENIED");
                        }
                        room = DBlayer.findRoom(player);
                        break;
                    case "start":
                        if (DBlayer.statusRoom(room.getId())) {
                            DBlayer.initGameObj(player, room);
                            out.println("start");
                        } else {
                            out.println("wait");
                        }
                        break;
                    case "move":
                        System.out.println("SET POSITION > " + login);
                        if (GameUtils.checkStep(new GameObj(object), room.getId(),login)) {
                            DBlayer.setPositions(room.getId(), login, new GameObj(object));
                            out.println("{\"status\":\"OK\"}");
                            break;
                        }
                        out.println("{\"status\":\"ERROR\"}");
                        break;
                    case "status":
                        LOGGER.info("GET STATUS > " + login);
                        JSONObject response = new JSONObject();
                        if (!DBlayer.statusRoom(room.getId())) {
                            response.put("status", "CLOSE");
                            out.println(response);
                            throw new RuntimeException("Close party");
                        }
                        response.put("status", DBlayer.getPlayerStatus(room.getId(), login));
                        out.println(response);
                        System.out.println("response " + response);
                        break;
                    case "positions":
                        out.println(GameUtils.getGameObj(DBlayer.getGameObjList(room.getId())));
                        break;
                    default:
                        System.out.println("invalid msg");
                }
            }
            System.out.println("END");
            DBlayer.closeGame(room.getId(), login);

        } catch (IOException | ParseException | SQLException e) {
            close(e.getMessage());
            DBlayer.closeGame(room.getId(), login);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (RuntimeException e) {
            close(e.getMessage());
            DBlayer.closeGame(room.getId(), login);
            LOGGER.log(Level.SEVERE, "Error", e);
        }

    }

    private void close(String errorMsg) {
        try {
            PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            out.println(errorMsg);
            out.close();
            socket.close();
            System.out.println("Close session");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
