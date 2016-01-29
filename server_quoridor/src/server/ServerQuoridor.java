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
        int pool = 10;
        try {
            switch (args.length) {
                case 1:
                    port = Integer.parseInt(args[0]);
                    break;
                case 2:
                    port = Integer.parseInt(args[0]);
                    if (Integer.parseInt(args[1]) % 2 == 0) {
                        pool = Integer.parseInt(args[1]);
                    }
                    break;
                case 3:
                    break;
            }
            System.out.printf("Start Server witch port=%d pool =%d\n",port,pool);
            List<Session> sessions = new ArrayList<Session>();
            ServerSocket serverSocket = new ServerSocket(port);
            DBlayer.createConnectFromDB();
            DBlayer.clearData();

            while (true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(30000);
                if (sessions.size() > pool) {
                    break;
                } else {
                    System.out.println("Connect #" + sessions.size()+ " >>> " + socket);
                    sessions.add(Session.createSession(socket));
                    sessions.get((sessions.size() - 1)).start();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
