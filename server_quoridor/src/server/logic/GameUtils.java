package server.logic;

import org.codehaus.jackson.map.ObjectMapper;
import server.model.DBlayer;
import server.model.GameObj;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Valera on 26.01.2016.
 */
public class GameUtils {

    public static String getGameObj(List<GameObj> gameObjList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(gameObjList);
    }

    public static boolean checkStep(GameObj nextStepObj, int roomId) throws SQLException {
//        List<GameObj> gameObjList = DBlayer.getGameObjList(roomId);

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
        return true;
    }
}
