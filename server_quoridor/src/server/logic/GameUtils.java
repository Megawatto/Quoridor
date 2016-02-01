package server.logic;

import server.model.DBlayer;
import server.model.GameObj;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 26.01.2016.
 */
public class GameUtils {

    public static boolean checkStep(GameObj nextStepObj, int roomId, String login) throws SQLException {


        List<GameObj> gameObjList = DBlayer.getGameObjList(roomId);
        GameObj player = DBlayer.getPlayerObj(login, false);
        GameObj opponent = DBlayer.getPlayerObj(login, true);
        List<GameObj> walls = new ArrayList<>();
        for (GameObj gameObj : gameObjList) {
            if (gameObj.getType().equals("wall")) walls.add(gameObj);
        }

        if (nextStepObj.getType().equals("wall")) {
            double result = (int) Math.sqrt(Math.pow(nextStepObj.getX() - nextStepObj.getX2(), 2) + (Math.pow(nextStepObj.getY() - nextStepObj.getY2(), 2)));
            int checkPosX = Math.abs(nextStepObj.getX() - nextStepObj.getX2());
            int checkPosY = Math.abs(nextStepObj.getY() - nextStepObj.getY2());
            return result == 2 && (checkPosX == 0 && checkPosY == 2 || checkPosX == 2 && checkPosY == 0);
        } else {
            if (nextStepObj.equals(opponent)) {
                System.out.println("zaglushka");
            } else {
                int checkPosX = Math.abs(player.getX() - nextStepObj.getX());
                int checkPosY = Math.abs(player.getY() - nextStepObj.getY());
//                TODO косяк с координатами вместо 2х захватывает 3 ячейки
                for (GameObj wall : walls) {
                    int checkWallX = wall.getX() - nextStepObj.getX();
                    int checkWallY = wall.getY() - nextStepObj.getY();
                    System.out.println(">>>>>>>>>>>>>>>" + checkWallX + " " + checkWallY);
                    if (wall.getY() > player.getY() && wall.getY() == wall.getY2()) {
                        if (wall.getX() <= nextStepObj.getX() && wall.getX2() > nextStepObj.getX() && (wall.getY() - nextStepObj.getY()) == 0) {
                            System.out.println("WALL");
                            return false;
                        }
                    }
                    if (wall.getY() <= player.getY() && wall.getY() == wall.getY2()) {
                        if (wall.getX() <= nextStepObj.getX() && wall.getX2() > nextStepObj.getX() && (wall.getY() - nextStepObj.getY()) == 1) {
                            System.out.println("WALL");
                            return false;
                        }
                    }

                    if (wall.getX() <= player.getX() && wall.getX() == wall.getX2()) {
                        if (wall.getY() <= nextStepObj.getY() && wall.getY2() > nextStepObj.getY() && (wall.getX() - nextStepObj.getX()) == 1) {
                            System.out.println("WALL");
                            return false;
                        }
                    }

                    if (wall.getX() > player.getX() && wall.getX() == wall.getX2()) {
                        if (wall.getY() <= nextStepObj.getY() && wall.getY2() > nextStepObj.getY() && (wall.getX() - nextStepObj.getX()) == 0) {
                            System.out.println("WALL");
                            return false;
                        }
                    }


                }
                return checkPosX == 1 && checkPosY == 0 || checkPosX == 0 && checkPosY == 1;
            }

        }
        return false;

//        if (nextStepObj.getType().equals("player")) {
//            for (GameObj oldObj : gameObjList) {
//                if (oldObj.getType().equals("player")) {
//                    if (oldObj.getX() - nextStepObj.getX() == Math.abs(1) || oldObj.getX() - nextStepObj.getX() == 0) {
//                        return true;
//                    }
//                    if (oldObj.getY() - nextStepObj.getY() == Math.abs(1) || oldObj.getY() - nextStepObj.getY() == 0) {
//                        return true;
//                    }
//                }
//            }
//
//        }

//        if (nextStepObj.getType().equals("wall")){
//
//        }
    }
}
