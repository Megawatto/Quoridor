package server.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;
import server.domain.DTO.GameObj;

import java.util.List;

/**
 * Created by Valera on 01.02.2016.
 */
public class ResponseMsg {

    @JsonProperty
    private String status;

    @JsonProperty
    private String msg;

    @JsonProperty
    private List<GameObj> gameObjs;

    public ResponseMsg() {
    }

    public ResponseMsg(TypeStatusMsg status) {
        this.status = status.name();
    }

    public ResponseMsg(TypeStatusMsg statusMsg, String msg) {
        this.status = statusMsg.name();
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    @JsonSetter
    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(TypeStatusMsg status) {
        this.status = status.name();
    }

    public List<GameObj> getGameObjs() {
        return gameObjs;
    }

    public void setGameObjs(List<GameObj> gameObjs) {
        this.gameObjs = gameObjs;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResponseMsg{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", gameObjs=" + gameObjs +
                '}';
    }
}
