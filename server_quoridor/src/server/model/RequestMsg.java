package server.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;
import server.domain.GameObjModel;

/**
 * Created by Valera on 29.01.2016.
 */
public class RequestMsg {

    @JsonProperty(value = "msg_type")
    private String msgType;

    @JsonProperty
    private String login;

    @JsonProperty
    private String password;

    @JsonProperty
    private GameObjModel gameObjModel;

    public RequestMsg() {
    }

    public RequestMsg(TypeRequestMsg msgType) {
        this.msgType = msgType.name();
    }

    public String getMsgType() {
        return msgType;
    }

    @JsonSetter
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public void setMsgType(TypeRequestMsg msgType) {
        this.msgType = msgType.name();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GameObjModel getGameObjModel() {
        return gameObjModel;
    }

    public void setGameObjModel(GameObjModel gameObjModel) {
        this.gameObjModel = gameObjModel;
    }

    @Override
    public String toString() {
        return "RequestMsg{" +
                "msgType='" + msgType + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", gameObj=" + gameObjModel +
                '}';
    }
}
