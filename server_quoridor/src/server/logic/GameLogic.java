package server.logic;

import server.domain.DTO.GameObj;
import server.domain.GameModel;
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

    boolean checkStep(GameObj nextStepObj, GameModel game) throws SQLException, GameException;

    GameModel addPlayer(PlayerModel player, Session session) throws SQLException;

    boolean checkFinish(GameModel game, GameObj gameObj);

    void checkQueue(PlayerModel player) throws GameException;

    RoomModel findRoom();

    RoomModel createRoom() throws SQLException;

    boolean isRun();

    boolean isClose();
}
