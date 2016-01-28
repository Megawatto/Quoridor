package server.logic;

import com.j256.ormlite.dao.Dao;
import org.codehaus.jackson.map.ObjectMapper;
import server.domain.GameObjModel;
import server.domain.PlayerModel;
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
            if (gameObj.getLogin().equals(login)&& gameObj.getType().equals("player") ) {
                player = gameObj;
            }
//            opponent = !gameObj.getLogin().equals(login) ? gameObj : null;
            if (gameObj.getType().equals("wall")) walls.add(gameObj);
        }

        assert player != null;
        return (Math.abs(player.getX() - nextStepObj.getX()) == 1 && Math.abs(player.getY() - nextStepObj.getY()) == 0)
                || (Math.abs(player.getX() - nextStepObj.getX()) == 0 && Math.abs(player.getY() - nextStepObj.getY()) == 1);

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
