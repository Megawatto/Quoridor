package server.model;

import server.domain.PlayerModel;
import server.domain.RoomModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Valera on 02.02.2016.
 */
public class Game {

    private RoomModel room;
    private List<PlayerModel> players;
    private Queue queue;
    private Map<PlayerModel, Session> sessionMap;
    private boolean isGame = false;
    private boolean run;

    public Game() {
    }


    public void startGame() throws SQLException {
        DBlayer.initGameObj(room);
        for (PlayerModel player : sessionMap.keySet()) {
            sessionMap.get(player).start();
        }
    }

    public void endGame(){}

    public void closeGame(){}

    public void addPlayer(Session session) {
        if (session.isAuthorization()) {
            this.sessionMap.put(session.getPlayer(), session);
            this.players.add(session.getPlayer());
        }
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public boolean isRun() {
        return run;
    }
}
