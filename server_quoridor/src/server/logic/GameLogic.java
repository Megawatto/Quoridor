package server.logic;

import server.domain.GameObjModel;
import server.domain.PlayerModel;
import server.domain.RoomModel;
import server.exception.GameException;
import server.model.Session;

import java.sql.SQLException;

/**
 * Created by Valera on 03.02.2016.
 */
public interface GameLogic {

    boolean startGame() throws SQLException;
    void endGame();
    void checkStep();
    boolean checkStep(GameObjModel nextStepObj, int roomId, String login) throws SQLException, GameException;
    void addPlayer(PlayerModel player, Session session) throws SQLException;
    void checkFinish();
    void checkQueue();
    RoomModel findRoom();
    RoomModel createRoom() throws SQLException;
    boolean isRun();
    boolean isClose();
}
