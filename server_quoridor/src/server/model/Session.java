package server.model;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import server.domain.GameModel;
import server.domain.PlayerModel;
import server.domain.RoomModel;
import server.exception.GameException;
import server.logic.GameLogic;
import server.utils.GameObjUtils;
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
    private PlayerModel player;
    private ResponseMsg response;
    private RequestMsg request;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectMapper mapper;
    private boolean authorization = false;
    private GameLogic gameLogic;
    private GameModel game;

    public Session(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            this.mapper = new ObjectMapper();
            this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
            this.mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
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
            DBlayer.closeGame(game.getRoom().getId(), player.getLogin());

        } catch (IOException e) {
            close(e.getMessage());
            DBlayer.closeGame(game.getRoom().getId(), player.getLogin());
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (RuntimeException e) {
            close(e.getMessage());
            DBlayer.closeGame(game.getRoom().getId(), player.getLogin());
            LOGGER.log(Level.SEVERE, "Error", e);
        } catch (SQLException e) {
            close(e.getMessage());
            DBlayer.closeGame(game.getRoom().getId(), player.getLogin());
            LOGGER.log(Level.SEVERE, "Error", e);
        } catch (GameException e) {
            e.printStackTrace();
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

    public ResponseMsg messageHandling(RequestMsg request) throws IOException, SQLException, GameException {

        switch (valueOf(request.getMsgType())) {
            case LOGIN:
                player = request.getLogin() != null ? DBlayer.authorization(request.getLogin(), request.getPassword()) : null;
                if (player == null) {
                    response = new ResponseMsg(TypeStatusMsg.ERROR, "ACCESS DENIED");
                    out.println(mapper.writeValueAsString(response));
                    LOGGER.info(response.toString());
                    System.out.println("ACCESS DENIED");
                    throw new RuntimeException("ACCESS DENIED");
                }
                response = new ResponseMsg(TypeStatusMsg.OK);
                authorization = true;
                this.game = gameLogic.addPlayer(player, this);
                break;
            case START:
                response = new ResponseMsg();
                if (gameLogic.startGame()) {
                    response.setStatus(TypeStatusMsg.START);
                } else {
                    response.setStatus(TypeStatusMsg.WAIT);
                }
                break;
            case MOVE:
                response = new ResponseMsg();
                gameLogic.checkQueue(this.player);
                System.out.println("SET POSITION > " + player.getLogin());
                try {
                    if (gameLogic.checkStep(request.getGameObjModel(), game)) {
                        DBlayer.setPositions(game.getRoom().getId(), player.getLogin(), request.getGameObjModel());
                        if (request.getGameObjModel().getType().equals(GameObjUtils.TYPE_OBJ_PLAYER)) {
                            if (gameLogic.checkFinish(game, request.getGameObjModel())) {
                                response.setStatus(TypeStatusMsg.WIN);
                                break;
                            }
                        }
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
                if (gameLogic.isClose()) {
                    response.setStatus(TypeStatusMsg.CLOSE);
                    break;
                }
                response.setStatus(TypeStatusMsg.valueOf(DBlayer.getPlayerStatus(game.getRoom().getId(), player.getLogin())));
                break;
            case POSITIONS:
                response = new ResponseMsg(TypeStatusMsg.GAME_OBJ);
                response.setGameObjModels(DBlayer.getGameObjList(game.getRoom().getId()));
                break;
            case FINISH:
                response = new ResponseMsg(TypeStatusMsg.WIN);
                break;
            default:
                LOGGER.info("invalid msg");
        }
        return response;
    }

    public void setGameLogic(Game gameLogic) {
        this.gameLogic = gameLogic;
    }

    public boolean isAuthorization() {
        return authorization;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public RoomModel getRoom() {
        return game.getRoom();
    }
}
