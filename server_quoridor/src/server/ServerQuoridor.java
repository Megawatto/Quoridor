package server;

import server.model.DBlayer;
import server.model.Session;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 22.01.2016.
 */

public class ServerQuoridor {
    public static void main(String[] args) {
        int port = 8080;

        try {
            switch (args.length) {
                case 1:
                    port = Integer.parseInt(args[0]);
                    System.out.println("set port = " + port);
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }

            System.out.println("Start Server");
            List<Session> sessions = new ArrayList<Session>();
            ServerSocket serverSocket = new ServerSocket(port);
            DBlayer.createConnectFromDB();
            DBlayer.clearData();

            while (true) {
                System.out.println("Wait");
                Socket socket = serverSocket.accept();
                if (sessions.size() > 4) {
                    break;
                } else {
                    System.out.println("Connect " + socket);
                    sessions.add(Session.createSession(socket));
                    sessions.get((sessions.size() - 1)).start();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
