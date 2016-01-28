package quoridor;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import quoridor.model.GameObj;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by Valera on 22.01.2016.
 */
public class Connector {

    private Socket conn;
    private PrintWriter out;
    private BufferedReader in;
    private JSONObject object;

    private String msg;
    private String login;
    private String password;


    public Connector(String url, String port, String login, String password) throws IOException {
        this.conn = new Socket(url, Integer.valueOf(port));
        this.out = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()), true);
        this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        this.login = login;
        this.password = password;
        login();
    }

    public String getStatus() throws IOException, ParseException {
        object = new JSONObject();
        object.put("msg_type", "status");
        sendMsg(object);
        msg = in.readLine();
        JSONParser parser = new JSONParser();
        System.out.println(msg);
        Object result = parser.parse(msg);
        object = (JSONObject) result;
        System.out.println("response >>> " + (String) object.get("status"));
        return (String) object.get("status");

    }

    public void sendPosition(GameObj gameObj) throws IOException {
        object = new JSONObject();
        object.put("msg_type", "move");
        object.put("type", gameObj.getType());
        object.put("x", gameObj.getX());
        object.put("y", gameObj.getY());
        object.put("x2", gameObj.getX2());
        object.put("y2", gameObj.getY2());
        sendMsg(object);
    }

    public List<GameObj> getGameObj() throws IOException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        object = new JSONObject();
        object.put("msg_type", "positions");
        sendMsg(object);
        msg = in.readLine();
        System.out.println(msg);
        return mapper.readValue(msg, mapper.getTypeFactory().constructCollectionType(List.class, GameObj.class));
    }

    private void sendMsg(JSONObject object) {
        out.println(this.object);
        System.out.println("send >>> " + this.object);
    }

    public void login() throws IOException {
        object = new JSONObject();
        object.put("msg_type","login");
        object.put("login", this.login);
        object.put("password", this.password);
        sendMsg(object);
        object = new JSONObject();
        object.put("msg_type","start");
//        TODO переделать этот бред на асинхронный вызов
        while (true) {
            try {
                sendMsg(object);
                if (in.readLine().equals("start")){
                    break;
                }
                System.out.println("WAIT START");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
