package server;

import server.model.DBlayer;
import server.model.Game;
import server.model.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valera on 22.01.2016.
 */

public class ServerQuoridor {
    public static void main(String[] args) {
        int port = 8080;
        int pool = 10;
        String f = null;
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
                    port = Integer.parseInt(args[0]);
                    if (Integer.parseInt(args[1]) % 2 == 0) {
                        pool = Integer.parseInt(args[1]);
                    }
                    f = args[2];
                    break;
            }
            System.out.printf("Start Server witch port=%d pool =%d\n", port, pool);
//            FIXME Убрать коллекцию сессий
            List<Session> sessions = new ArrayList<Session>();
            List<Game> games = new ArrayList<>();
            ServerSocket serverSocket = new ServerSocket(port);
            DBlayer.createConnectFromDB(f);
            DBlayer.clearData();

            while (true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(300000);
                if (sessions.size() > pool) {
                    socket.close();
                    break;
                } else {
                    System.out.println("Connect #" + sessions.size() + " >>> " + socket);
                    Session session = Session.createSession(socket);
                    while (!session.isAlive()) {
                        if (games.size() == 0) {
                            games.add(new Game());
                        }

                        for (Game game : games) {
                            if (!game.isRun()) {
                                session.setGameLogic(game);
                                session.start();
                            }
                        }

                        if (!session.isAlive()){
                            games.add(new Game());
                        }
                    }
                }
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
