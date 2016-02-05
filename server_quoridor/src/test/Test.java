import org.json.simple.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Valera on 26.01.2016.
 */
public class Test {

    static final String URL = "localhost";
    static final int PORT = 8080;

    public static void main(String[] args) throws Throwable {
        String s1 = "{\"msg_type\":\"LOGIN\",\"login\":\"test\",\"password\":\"test\"}\n";
        String s2 = "{\"msg_type\":\"LOGIN\",\"login\":\"test2\",\"password\":\"test\"}\n";
        String s3 = "{\"msg_type\":\"START\"}\n";
        String s4;

        Socket cl1 = new Socket(URL, PORT);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(cl1.getInputStream()));
        PrintWriter out1 = new PrintWriter(new BufferedOutputStream(cl1.getOutputStream()), true);
        Socket cl2 = new Socket(URL, PORT);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(cl2.getInputStream()));
        PrintWriter out2 = new PrintWriter(new BufferedOutputStream(cl2.getOutputStream()), true);

        out1.println(s1);
        System.out.println(in1.readLine());
        out2.println(s2);
        System.out.println(in2.readLine());
        out1.println(s3);
        System.out.println(in1);
        Thread.sleep(15000);

    }
}
