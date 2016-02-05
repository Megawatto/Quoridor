package server.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;
import server.domain.DTO.GameObj;

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
    private GameObj gameObj;

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

    public GameObj getGameObj() {
        return gameObj;
    }

    public void setGameObj(GameObj gameObj) {
        this.gameObj = gameObj;
    }

    @Override
    public String toString() {
        return "RequestMsg{" +
                "msgType='" + msgType + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", gameObj=" + gameObj +
                '}';
    }
}
