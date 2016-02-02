package server.utils;

import server.domain.GameObjModel;
import server.domain.PlayerModel;
import server.domain.RoomModel;

/**
 * Created by Valera on 02.02.2016.
 */
public class StartGameObjUtils {
    public static final Point PLAYER_ONE_POSITION = new Point(5, 1, 1);
    public static final Point PLAYER_TWO_POSITION = new Point(5, 9, 2);
    public static final Point PLAYER_THREE_POSITION = new Point(1, 5, 3);
    public static final Point PLAYER_FOUR_POSITION = new Point(9, 5, 4);

    public static final String TYPE_OBJ_PLAYER = "player";
    public static final String TYPE_OBJ_WALL = "wall";

    public static GameObjModel createStartPlayerObj(RoomModel room, PlayerModel player, int numberPlayer) {
        GameObjModel result;
        switch (numberPlayer) {
            case 1:
                result = new GameObjModel(room, player, TYPE_OBJ_PLAYER, PLAYER_ONE_POSITION);
                break;
            case 2:
                result = new GameObjModel(room, player, TYPE_OBJ_PLAYER, PLAYER_TWO_POSITION);
                break;
            case 3:
                result = new GameObjModel(room, player, TYPE_OBJ_PLAYER, PLAYER_THREE_POSITION);
                break;
            case 4:
                result = new GameObjModel(room, player, TYPE_OBJ_PLAYER, PLAYER_FOUR_POSITION);
                break;
            default:
                throw new NullPointerException("NO QUEUE NUMBER");
        }
        return result;
    }
}
