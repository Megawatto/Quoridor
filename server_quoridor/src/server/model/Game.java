package server.model;

import server.domain.DTO.GameObj;
import server.domain.GameModel;
import server.domain.GameObjModel;
import server.domain.PlayerModel;
import server.domain.RoomModel;
import server.exception.GameException;
import server.logic.GameLogic;
import server.utils.GameObjUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Created by Valera on 02.02.2016.
 */
public class Game implements GameLogic {

    private RoomModel room;
    private Queue<PlayerModel> queue;
    private Map<PlayerModel, Session> sessionMap;
    private boolean run = false;
    private boolean closeGame = false;
    private int limitPlayer = 2;

    private static final Logger log = Logger.getLogger(Game.class.getName());

    public Game() throws SQLException {
        this.sessionMap = new HashMap<>();
        this.queue = new LinkedList<>();
        this.room = createRoom();
    }


    @Override
    public synchronized boolean startGame() throws SQLException {
        if (room.getStatus().equals("START")) {
            return true;
        }
        if (room.getCountPlayer() == limitPlayer) {
            room.setStatus("START");
            DBlayer.initGameObj(room);
            DBlayer.updateStatusRoom(room);
            this.run = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void endGame() throws SQLException {
        this.queue.clear();
        this.sessionMap.clear();
        this.room.setCountPlayer(0);
        DBlayer.updateStatusRoom(room);
        this.run = false;
        log.info("END GAME room = " + room);
    }

    @Override
    public boolean checkStep(GameObj nextStepObj, GameModel game) throws SQLException, GameException {

        List<GameObjModel> gameObjModelList = DBlayer.getGameObjList(game.getRoom().getId());
        GameObj player = new GameObj(DBlayer.getPlayerObj(game.getPlayer().getLogin(), false));
        GameObj opponent = new GameObj(DBlayer.getPlayerObj(game.getPlayer().getLogin(), true));
        List<GameObjModel> walls = new ArrayList<>();
        for (GameObjModel gameObjModel : gameObjModelList) {
            if (gameObjModel.getType().equals(GameObjUtils.TYPE_OBJ_WALL)) walls.add(gameObjModel);
        }

        if (nextStepObj.getType().equals(GameObjUtils.TYPE_OBJ_WALL)) {
            double result = (int) Math.sqrt(Math.pow(nextStepObj.getX() - nextStepObj.getX2(), 2) + (Math.pow(nextStepObj.getY() - nextStepObj.getY2(), 2)));
            int checkPosX = Math.abs(nextStepObj.getX() - nextStepObj.getX2());
            int checkPosY = Math.abs(nextStepObj.getY() - nextStepObj.getY2());
            if (!(result == 2 && (checkPosX == 0 && checkPosY == 2 || checkPosX == 2 && checkPosY == 0))) {
                throw new GameException("invalid state wall");
            }
            return true;
        } else {
            int checkPosX = Math.abs(player.getX() - nextStepObj.getX());
            int checkPosY = Math.abs(player.getY() - nextStepObj.getY());
//                TODO косяк с координатами вместо 2х захватывает 3 ячейки
            for (GameObjModel wall : walls) {
                if (wall.getY() > player.getY() && wall.getY() == wall.getY2()) {
                    if (wall.getX() <= nextStepObj.getX() && wall.getX2() > nextStepObj.getX() && (wall.getY() - nextStepObj.getY()) == 0) {
                        throw new GameException("WALL");
                    }
                }
                if (wall.getY() <= player.getY() && wall.getY() == wall.getY2()) {
                    if (wall.getX() <= nextStepObj.getX() && wall.getX2() > nextStepObj.getX() && (wall.getY() - nextStepObj.getY()) == 1) {
                        throw new GameException("WALL");
                    }
                }

                if (wall.getX() <= player.getX() && wall.getX() == wall.getX2()) {
                    if (wall.getY() <= nextStepObj.getY() && wall.getY2() > nextStepObj.getY() && (wall.getX() - nextStepObj.getX()) == 1) {
                        throw new GameException("WALL");
                    }
                }

                if (wall.getX() > player.getX() && wall.getX() == wall.getX2()) {
                    if (wall.getY() <= nextStepObj.getY() && wall.getY2() > nextStepObj.getY() && (wall.getX() - nextStepObj.getX()) == 0) {
                        throw new GameException("WALL");
                    }
                }
            }
            if (opponent.equals(nextStepObj)) {
                throw new GameException("OPPONENT");
            } else {
                int checkPosXopp = Math.abs(opponent.getX() - player.getX());
                int checkPosYopp = Math.abs(opponent.getY() - player.getY());
                if (checkPosXopp == 1 && checkPosYopp == 0 || checkPosXopp == 0 && checkPosYopp == 1) {
                    checkPosXopp = Math.abs(opponent.getX() - nextStepObj.getX());
                    checkPosYopp = Math.abs(opponent.getY() - nextStepObj.getY());
                    if (checkPosXopp == 1 && checkPosYopp == 0 || checkPosXopp == 0 && checkPosYopp == 1) {
                        return true;
                    }
                }
            }
            if (!(checkPosX == 1 && checkPosY == 0 || checkPosX == 0 && checkPosY == 1)) {
                throw new GameException("invalid step x=" + nextStepObj.getX() + " y= " + nextStepObj.getY());
            }
            return true;
        }
    }


    @Override
    public GameModel addPlayer(PlayerModel player, Session session) throws SQLException {
        this.sessionMap.put(player, session);
        this.queue.add(player);
        GameModel game = DBlayer.addPlayerFromGame(room, player, GameObjUtils.getStatus(TypeStatusMsg.WAIT), queue.size());
        room.setCountPlayer(this.queue.size());
        DBlayer.updateStatusRoom(room);
        return game;
    }

    @Override
    public boolean checkFinish(GameModel game, GameObj gameObj) throws SQLException {
        switch (game.getQueue()) {
            case 1:
                if (gameObj.getY() == 9) {
                    DBlayer.endGame(game);
                    return true;
                }
                break;
            case 2:
                if (gameObj.getY() == 1) {
                    DBlayer.endGame(game);
                    return true;
                }
                break;
            case 3:
                if (gameObj.getX() == 9) {
                    DBlayer.endGame(game);
                    return true;
                }
                break;
            case 4:
                if (gameObj.getX() == 1) {
                    DBlayer.endGame(game);
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void checkQueue(PlayerModel player) throws GameException {
        PlayerModel next = queue.peek();
        if (!next.getLogin().equals(player.getLogin())) {
            throw new GameException("someone else's turn");
        } else {
            queue.poll();
            queue.add(player);
        }
    }

    @Override
    public RoomModel createRoom() throws SQLException {
        return DBlayer.createRoom();
    }

    @Override
    public boolean isRun() {
        return run;
    }

    @Override
    public boolean isClose() {
        return closeGame;
    }

    public void setLimitPlayer(int limitPlayer) {
        this.limitPlayer = limitPlayer;
    }
}
