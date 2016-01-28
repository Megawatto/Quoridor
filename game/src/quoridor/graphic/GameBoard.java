package quoridor.graphic;

import org.json.simple.parser.ParseException;
import quoridor.Connector;
import quoridor.model.GameObj;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

    private int startWallX;
    private int startWallY;
    private int endWallX;
    private int endWallY;

    public GameBoard(final String LOGIN, final Connector connector) throws IOException, ParseException {
        this.gameObjs = connector.getGameObj();
        this.LOGIN = LOGIN;
        this.connector = connector;

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
                if (Math.abs(startWallX - e.getX()) <= 25 && Math.abs(startWallY - e.getY()) <= 25) {
                    GameObj nextStep = new GameObj();
                    nextStep.setType(TYPE_PLAYER);
                    nextStep.setX(calcPositionX(e.getX()));
                    nextStep.setY(calcPositionY(e.getY()));
                    try {
                        connector.sendPosition(nextStep);
                        gameObjs = connector.getGameObj();
                    } catch (IOException | ParseException e1) {
                        e1.printStackTrace();
                    }
                    repaint();
                    checkStatus();
                } else {
                    endWallX = 0;
                    endWallY = 0;
                    GameObj nextStep = new GameObj();
                    nextStep.setType(TYPE_WALL);
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
                    checkStatus();
                }
            }
        });
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
        for (GameObj gameObj : gameObjs) {
            if (gameObj.getType().equals(TYPE_PLAYER)) {
                if (gameObj.getLogin().equals(LOGIN)) {
                    g.setColor(Color.orange);
                    g.fillOval(gameObj.getX() * SIZE_CELL, gameObj.getY() * SIZE_CELL, SIZE_CELL, SIZE_CELL);
                } else {
                    g.setColor(Color.blue);
                    g.fillOval(gameObj.getX() * SIZE_CELL, gameObj.getY() * SIZE_CELL, SIZE_CELL, SIZE_CELL);
                }
            }
            if (gameObj.getType().equals(TYPE_WALL)) {
                g.setColor(Color.red);
                g.drawLine(gameObj.getX() * SIZE_CELL, gameObj.getY() * SIZE_CELL, gameObj.getX2() * SIZE_CELL, gameObj.getY2() * SIZE_CELL);
            }
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

    private void checkStatus()  {
        try {
            while (true) {
                if (connector.getStatus().equals("MOVE")) {
                    break;
                }
                System.out.println("WAIT STEP");
                Thread.sleep(1000);
            }
        } catch (InterruptedException | ParseException | IOException e) {
            e.printStackTrace();
        }
    }

}

