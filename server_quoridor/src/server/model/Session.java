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
        System.out.println("GO");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            String msg = in.readLine();
            Object result = parser.parse(msg);
            JSONObject object = (JSONObject) result;
            login = (String) object.get("login");
            password = (String) object.get("password");
            if (!DBlayer.authorization(login, password) || login == null) {
                close("ACCESS DENIED");
            }
            roomId = DBlayer.findRoom(login);
            while (true) {
                System.out.println("WAIT MSG >>>");
                msg = in.readLine();
                if (msg == null || msg.equals("end")) {
                    socket.close();
                    break;
                }
                System.out.println(msg);
                result = parser.parse(msg);
                object = (JSONObject) result;
                if (((String) object.get("msg_type")).equals("start")){
                    if (DBlayer.statusRoom(roomId)){
                        out.println("start");
                    } else {
                        out.println("wait");
                    }
                }
                if (DBlayer.statusRoom(roomId)) {
                    switch ((String) object.get("msg_type")) {
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
                            System.out.println("GET STATUS > " + login);
                            JSONObject response = new JSONObject();
                            response.put("status", DBlayer.getPlayerStatus(roomId, login));
                            out.println(response);
                            System.out.println("response " + response);
                            break;
                        case "positions":
                            out.println(GameUtils.getGameObj(DBlayer.getGameObjList(roomId)));
                            break;
                    }
                }
            }
            System.out.println("END");
            DBlayer.closeGame(roomId, login);

        } catch (IOException | ParseException e) {
            close(e.getMessage());
            DBlayer.closeGame(roomId, login);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (SQLException e) {
            close(e.getMessage());
            DBlayer.closeGame(roomId, login);
            LOGGER.log(Level.SEVERE, "Error sql", e);
        }

    }

    private void close(String errorMsg) {
        try {
            PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            out.println(errorMsg);
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
