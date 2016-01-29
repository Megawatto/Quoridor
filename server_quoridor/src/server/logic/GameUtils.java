package server.logic;

import org.codehaus.jackson.map.ObjectMapper;
import server.model.DBlayer;
import server.model.GameObj;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 26.01.2016.
 */
public class GameUtils {

    public static String getGameObj(List<GameObj> gameObjList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(gameObjList);
    }

    public static boolean checkStep(GameObj nextStepObj, int roomId, String login) throws SQLException {


        List<GameObj> gameObjList = DBlayer.getGameObjList(roomId);
        GameObj player = null;
        GameObj opponent = null;
        List<GameObj> walls = new ArrayList<>();
        for (GameObj gameObj : gameObjList) {
            if (gameObj.getLogin().equals(login) && gameObj.getType().equals("player")) {
                player = gameObj;
            }
//            opponent = !gameObj.getLogin().equals(login) ? gameObj : null;
            if (gameObj.getType().equals("wall")) walls.add(gameObj);
        }

        if (!nextStepObj.equals(player)) {
            double result = (int) Math.sqrt(Math.pow(nextStepObj.getX() - nextStepObj.getX2(), 2) + (Math.pow(nextStepObj.getY() - nextStepObj.getY2(), 2)));
            int checkPosX = Math.abs(nextStepObj.getX() - nextStepObj.getX2());
            int checkPosY = Math.abs(nextStepObj.getY() - nextStepObj.getY2());
            return result == 2 && (checkPosX == 0 && checkPosY == 2 || checkPosX == 2 && checkPosY == 0);
        } else {
            int checkPosX = Math.abs(player.getX() - nextStepObj.getX());
            int checkPosY = Math.abs(player.getY() - nextStepObj.getY());
            return checkPosX == 1 && checkPosY == 0 || checkPosX == 0 && checkPosY == 1;
        }

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
