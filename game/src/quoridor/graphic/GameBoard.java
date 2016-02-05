package quoridor.graphic;

import org.json.simple.parser.ParseException;
import quoridor.Connector;
import quoridor.model.GameObj;
import quoridor.model.TypeStatusMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

/**
 * Created by Valera on 21.01.2016.
 */
public final class GameBoard extends JPanel {

    private final Connector connector;
    private java.util.List<GameObj> gameObjs;

    private final int SIZE_CELL = 25;
    private final int SHIFT = 25;
    private final int MAX_LINE = 10;
    private final String LOGIN;
    private final String TYPE_PLAYER = "player";
    private final String TYPE_WALL = "wall";
    private final GameMenu gameMenu;

    private int startWallX;
    private int startWallY;
    private int endWallX;
    private int endWallY;
    private boolean active;

    public GameBoard(final String LOGIN, final Connector connector, final GameMenu gameMenu) throws IOException, ParseException {
        this.gameMenu = gameMenu;
        this.gameObjs = connector.getGameObj();
        this.LOGIN = LOGIN;
        this.connector = connector;
        this.active = connector.getStatus().equals("MOVE");


        final Runnable getAsynStatus = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String msg = connector.getStatus();
                        gameMenu.updateStatus(msg);
                        if (msg.equals("MOVE")) {
                            break;
                        }
                        if (msg.equals("CLOSE")) {
                            closeParty();
                        }

                        if (msg.equals(TypeStatusMsg.WIN.name())) {
                            endGame(msg);
                        }

                        if (msg.equals(TypeStatusMsg.LOSE.name())) {
                            endGame(msg);
                        }
                        Thread.sleep(1000);
                    }
                    active = true;
                    gameObjs = connector.getGameObj();
                    repaint();
                } catch (InterruptedException | ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        if (!active) {
            new Thread(getAsynStatus).start();
        }

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setSize(300, 200);
        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                endWallX = e.getX();
                endWallY = e.getY();
                repaint();
            }
        });
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                startWallX = e.getX();
                startWallY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (active) {
                    if (Math.abs(startWallX - e.getX()) <= SIZE_CELL && Math.abs(startWallY - e.getY()) <= SIZE_CELL) {
                        GameObj nextStep = new GameObj();
                        nextStep.setLogin(LOGIN);
                        nextStep.setType(TYPE_PLAYER);
                        nextStep.setX(calcPositionX(e.getX()));
                        nextStep.setY(calcPositionY(e.getY()));
                        try {
                            connector.sendPosition(nextStep);
                            gameObjs = connector.getGameObj();
                        } catch (IOException | ParseException e1) {
                            gameMenu.updateStatus(e1.getMessage());
                            e1.printStackTrace();
                            return;
                        }
                        repaint();
                        active = false;
                        new Thread(getAsynStatus).start();
                    } else {
                        endWallX = 0;
                        endWallY = 0;
                        GameObj nextStep = new GameObj();
                        nextStep.setLogin(LOGIN);
                        nextStep.setType(TYPE_WALL);
                        convertCoords(startWallX, startWallY, endWallX, endWallY);
                        nextStep.setX(calcPositionX(startWallX));
                        nextStep.setY(calcPositionY(startWallY));
                        nextStep.setX2(calcPositionX(e.getX()));
                        nextStep.setY2(calcPositionX(e.getY()));
                        try {
                            connector.sendPosition(nextStep);
                            gameObjs = connector.getGameObj();
                        } catch (IOException | ParseException e1) {
                            e1.printStackTrace();
                        }
                        repaint();
                        active = false;
                        new Thread(getAsynStatus).start();
                    }
                }
                System.out.println("LOCK");
            }
        });
    }

    private void convertCoords(int startWallX, int startWallY, int endWallX, int endWallY) {
        if (startWallX < endWallX) {
            this.startWallX = endWallX;
            this.endWallX = startWallX;
        }
        if (startWallY < endWallY) {
            this.startWallY = endWallY;
            this.endWallY = startWallY;
        }
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i < MAX_LINE; i++) {
            g.setColor(Color.black);
            g.drawLine(SHIFT + SIZE_CELL * i, SHIFT, SHIFT + SIZE_CELL * i, SHIFT + SIZE_CELL * (MAX_LINE - 1)); // y
            g.drawLine(SHIFT, SHIFT + SIZE_CELL * i, SHIFT + SIZE_CELL * (MAX_LINE - 1), SHIFT + SIZE_CELL * i); // x
            if (endWallX > 0 && endWallY > 0) {
                g.setColor(Color.red);
                g.drawLine(startWallX, startWallY, endWallX, endWallY);
            }
        }
        try {
            for (GameObj gameObj : gameObjs) {
                if (gameObj.getType().equals(TYPE_PLAYER)) {
                    if (gameObj.getLogin().equals(LOGIN)) {
                        g.setColor(Color.blue);
                        g.fillOval(gameObj.getX() * SIZE_CELL, gameObj.getY() * SIZE_CELL, SIZE_CELL, SIZE_CELL);
                    } else {
                        g.setColor(Color.red);
                        g.fillOval(gameObj.getX() * SIZE_CELL, gameObj.getY() * SIZE_CELL, SIZE_CELL, SIZE_CELL);
                    }
                }
                if (gameObj.getType().equals(TYPE_WALL)) {
                    g.setColor(Color.green);
                    g.drawLine(gameObj.getX() * SIZE_CELL, gameObj.getY() * SIZE_CELL, gameObj.getX2() * SIZE_CELL, gameObj.getY2() * SIZE_CELL);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(275, 275);
    }

    private int calcPositionX(int x) {
        x = (int) (Math.floor(x) / SIZE_CELL);
        return x;
    }

    private int calcPositionY(int y) {
        y = (int) (Math.floor(y) / SIZE_CELL);
        return y;
    }

    private void closeParty() {
        JOptionPane.showMessageDialog(new Frame(), "Close Party", "END GAME", JOptionPane.INFORMATION_MESSAGE);
        connector.close();
        System.exit(0);
    }

    private void endGame(String status) {
        JOptionPane.showMessageDialog(new Frame(), status, "END GAME", JOptionPane.INFORMATION_MESSAGE);
        connector.endGame();
        connector.close();
        System.exit(0);
    }

}

