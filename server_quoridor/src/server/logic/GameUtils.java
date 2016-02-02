package server.logic;

import server.model.DBlayer;
import server.exception.GameException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 26.01.2016.
 */
public class GameUtils {

    public static boolean checkStep(GameObjModel nextStepObj, int roomId, String login) throws SQLException, GameException {

        List<GameObjModel> gameObjModelList = DBlayer.getGameObjList(roomId);
        GameObjModel player = DBlayer.getPlayerObj(login, false);
        GameObjModel opponent = DBlayer.getPlayerObj(login, true);
        List<GameObjModel> walls = new ArrayList<>();
        for (GameObjModel gameObjModel : gameObjModelList) {
            if (gameObjModel.getType().equals("wall")) walls.add(gameObjModel);
        }

        if (nextStepObj.getType().equals("wall")) {
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
}
