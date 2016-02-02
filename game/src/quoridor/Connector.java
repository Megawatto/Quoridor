package quoridor;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.parser.ParseException;
import quoridor.model.GameObj;
import quoridor.model.RequestMsg;
import quoridor.model.ResponseMsg;
import quoridor.model.TypeRequestMsg;
import quoridor.model.TypeStatusMsg;

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
    private RequestMsg request;
    private ResponseMsg response;
    private ObjectMapper mapper;

    private final String login;
    private final String password;


    public Connector(String url, String port, final String login, final String password) throws IOException {
        this.conn = new Socket(url, Integer.valueOf(port));
        this.out = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()), true);
        this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        this.mapper = new ObjectMapper();
        this.password = password;
        this.login = login;

        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        conn.setSoTimeout(300000);
    }

    public String getStatus() throws IOException, ParseException {
        request = new RequestMsg(TypeRequestMsg.STATUS);
        sendMsg(request);
        response = mapper.readValue(in, ResponseMsg.class);
        System.out.println(response);
        return response.getStatus();
    }

    public void sendPosition(GameObj gameObj) throws IOException, ParseException {
        request = new RequestMsg(TypeRequestMsg.MOVE);
        request.setGameObj(gameObj);
        sendMsg(request);
        response = mapper.readValue(in, ResponseMsg.class);
        System.out.println(response);
        if (response.getStatus().equals(TypeStatusMsg.ERROR.name())) {
            throw new IOException(response.getMsg());
        }
    }

    public List<GameObj> getGameObj() throws IOException, ParseException {
        request = new RequestMsg(TypeRequestMsg.POSITIONS);
        sendMsg(request);
        response = mapper.readValue(in, ResponseMsg.class);
        System.out.println(response);
        return response.getGameObjs();
    }

    private void sendMsg(RequestMsg requestMsg) throws IOException {
        out.println(mapper.writeValueAsString(requestMsg));
        System.out.println("send >>> " + requestMsg);
    }

    public void login() throws IOException {
        this.request = new RequestMsg();
        request.setMsgType(TypeRequestMsg.LOGIN);
        request.setLogin(this.login);
        request.setPassword(this.password);
        sendMsg(request);
        response = mapper.readValue(in, ResponseMsg.class);
        System.out.println(response);
        if (response.getStatus().equals(TypeStatusMsg.ERROR.name())){
            throw new IOException();
        }
        request.setMsgType(TypeRequestMsg.START);
//        TODO переделать этот бред на асинхронный вызов

        while (true) {
            try {
                sendMsg(request);
                response = mapper.readValue(in, ResponseMsg.class);
                System.out.println(response);
                if (response.getStatus().equals(TypeStatusMsg.START.name())) {
                    break;
                }
                Thread.sleep(3000);
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
