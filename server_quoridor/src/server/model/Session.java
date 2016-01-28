package server.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
    private JSONParser parser;
    private static final Logger LOGGER = Logger.getLogger(Session.class.getName());
    private int roomId;
    private String login;
    private String password;
    private String msg;

    public Session(Socket socket) {
        this.socket = socket;
    }

    public static Session createSession(Socket socket) {
        System.out.println("New session");
        return new Session(socket);
    }

    @Override
    public void run() {
        super.run();
        parser = new JSONParser();
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
                Object result = parser.parse(msg);
                JSONObject object = (JSONObject) result;

                switch ((String) object.get("msg_type")) {
                    case "login":
                        login = (String) object.get("login");
                        password = (String) object.get("password");
                        if (!DBlayer.authorization(login, password) || login == null) {
                            System.out.println("ACCESS DENIED");
                            throw new RuntimeException("ACCESS DENIED");
                        }
                        roomId = DBlayer.findRoom(login);
                        break;
                    case "start":
                        if (DBlayer.statusRoom(roomId)) {
                            out.println("start");
                        } else {
                            out.println("wait");
                        }
                        break;
                    case "move":
                        System.out.println("SET POSITION > " + login);
                        if (GameUtils.checkStep(new GameObj(object), roomId)) {
                            DBlayer.setPositions(roomId, login, new GameObj(object));
//                                out.println();
                            break;
                        }
                        out.println("error");
                        break;
                    case "status":
                        LOGGER.info("GET STATUS > " + login);
                        JSONObject response = new JSONObject();
                        if (!DBlayer.statusRoom(roomId)) {
                            response.put("status", "CLOSE");
                            out.println(response);
                            throw new RuntimeException("Close party");
                        }
                        response.put("status", DBlayer.getPlayerStatus(roomId, login));
                        out.println(response);
                        System.out.println("response " + response);
                        break;
                    case "positions":
                        out.println(GameUtils.getGameObj(DBlayer.getGameObjList(roomId)));
                        break;
                    default:
                        System.out.println("invalid msg");
                }
            }
            System.out.println("END");
            DBlayer.closeGame(roomId, login);

        } catch (IOException | ParseException | SQLException e) {
            close(e.getMessage());
            DBlayer.closeGame(roomId, login);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (RuntimeException e) {
            close(e.getMessage());
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
