import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Valera on 26.01.2016.
 */
public class Test {

    static final String URL = "localhost";
    static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        JSONObject object = new JSONObject();
        object.put("msg_type", "move");
        object.put("type", "test");
        object.put("x", 10);
        object.put("y", 10);
        object.put("x2", 10);
        object.put("y2", 10);
        System.out.println(object);
        Socket cl1 = new Socket(URL, PORT);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(cl1.getInputStream()));
        Socket cl2 = new Socket(URL, PORT);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(cl2.getInputStream()));


    }
}
