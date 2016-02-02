package server.model;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import server.domain.PlayerModel;
import server.domain.RoomModel;
import server.exception.GameException;
import server.logic.GameLogic;
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

import static server.model.TypeRequestMsg.valueOf;

/**
 * Created by Valera on 22.01.2016.
 */

public class Session extends Thread {

    private Socket socket;
    private static final Logger LOGGER = Logger.getLogger(Session.class.getName());
    //    TODO Переделать ид на объекты
    private RoomModel room;
    private PlayerModel player;
    private ResponseMsg response;
    private RequestMsg request;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectMapper mapper;
    private boolean authorization = false;
    private GameLogic game;

    public Session(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            this.mapper = new ObjectMapper();
            this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        } catch (IOException e) {
        }
    }

    public static Session createSession(Socket socket) {
        LOGGER.info("New session " + socket);

        return new Session(socket);
    }

    @Override
    public void run() {
        super.run();
        LOGGER.info("Start Session " + this.getName());
        try {
            while (true) {
                request = mapper.readValue(in, RequestMsg.class);
                if (request == null) {
                    response = new ResponseMsg(TypeStatusMsg.ERROR, "invalid msg");
                    out.println(mapper.writeValueAsString(response));
                    LOGGER.info(response.toString());
                    socket.close();
                    break;
                }
                LOGGER.info(request.toString());
                out.println(mapper.writeValueAsString(messageHandling(request)));
                LOGGER.info(response.toString());
            }
            System.out.println("END");
            DBlayer.closeGame(room.getId(), player.getLogin());

        } catch (IOException e) {
            close(e.getMessage());
            DBlayer.closeGame(room.getId(), player.getLogin());
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (RuntimeException e) {
            close(e.getMessage());
            DBlayer.closeGame(room.getId(), player.getLogin());
            LOGGER.log(Level.SEVERE, "Error", e);
        } catch (SQLException e) {
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

    public void authorization() throws IOException {

    }


    public ResponseMsg messageHandling(RequestMsg request) throws IOException, SQLException {

        switch (valueOf(request.getMsgType())) {
            case LOGIN:
                String login = request.getLogin();
                String password = request.getPassword();
                this.player = login != null ? DBlayer.authorization(login, password) : null;
                if (player == null) {
                    response = new ResponseMsg(TypeStatusMsg.ERROR, "ACCESS DENIED");
                    out.println(mapper.writeValueAsString(response));
                    LOGGER.info(response.toString());
                    System.out.println("ACCESS DENIED");
                    throw new RuntimeException("ACCESS DENIED");
                }
                response = new ResponseMsg(TypeStatusMsg.OK);
                authorization = true;
                game.addPlayer(player, this);

//                FIXME remove this
                room = DBlayer.findRoom(player);

                break;
            case START:
                response = new ResponseMsg();
                if (game.startGame()) {
                    response.setStatus(TypeStatusMsg.START);
                } else {
                    response.setStatus(TypeStatusMsg.WAIT);
                }

                break;
            case MOVE:
                game.checkQueue();
                game.checkFinish();
                game.checkStep();


                response = new ResponseMsg();
                System.out.println("SET POSITION > " + player.getLogin());
                try {
                    if (GameUtils.checkStep(request.getGameObjModel(), room.getId(), player.getLogin())) {
                        DBlayer.setPositions(room.getId(), player.getLogin(), request.getGameObjModel());
                        response.setStatus(TypeStatusMsg.OK);
                        break;
                    }
                } catch (GameException e) {
                    response.setStatus(TypeStatusMsg.ERROR);
                    response.setMsg(e.getMessage());
                    break;
                }
            case STATUS:
                response = new ResponseMsg();
                LOGGER.info("GET STATUS > " + player.getLogin());
                if (!DBlayer.statusRoom(room.getId())) {
                    response.setStatus(TypeStatusMsg.CLOSE);
                    break;
                }
                response.setStatus(TypeStatusMsg.valueOf(DBlayer.getPlayerStatus(room.getId(), player.getLogin())));
                out.println(mapper.writeValueAsString(response));
                LOGGER.info(response.toString());
                break;
            case POSITIONS:
                response = new ResponseMsg(TypeStatusMsg.GAME_OBJ);
                response.setGameObjModels(DBlayer.getGameObjList(room.getId()));
                break;
            case FINISH:
                response = new ResponseMsg(TypeStatusMsg.WIN);
                break;
            default:
                LOGGER.info("invalid msg");
        }
        return response;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isAuthorization() {
        return authorization;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public RoomModel getRoom() {
        return room;
    }
}
