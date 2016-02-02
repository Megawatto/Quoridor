package server.model;

import server.domain.PlayerModel;
import server.domain.RoomModel;

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

    public Game() {
    }


    public void addPlayer(PlayerModel player, Session session) {
        if (session.isAuthorization()) {
            this.sessionMap.put(player, session);
            this.players.add(player);
        }
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

}
