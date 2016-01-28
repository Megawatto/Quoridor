package quoridor.graphic;

import quoridor.Connector;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Valera on 22.01.2016.
 */
public class GameMenu extends JPanel {

    private final String LOGIN;
    private Connector connector;

    public GameMenu(String LOGIN) {
        this.LOGIN = LOGIN;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.ORANGE));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font("Verdana",Font.PLAIN, 14));
        g.drawString("<<< " + LOGIN + " >>>",5,15);
        g.drawString("STEP",5,30);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 275);
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }
}
