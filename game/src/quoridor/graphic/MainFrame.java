package quoridor.graphic;

import org.json.simple.parser.ParseException;
import quoridor.Connector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by Valera on 21.01.2016.
 */
public class MainFrame extends JFrame {

    private GameBoard gameBoard;
    private GameMenu gameMenu;
    private JTextField url = new JTextField("localhost", 10);
    private JTextField port = new JTextField("8080", 5);
    private JTextField login = new JTextField("test", 5);
    private JTextField password = new JTextField("test", 5);
    private JButton button = new JButton("CONNECT!");
    private Connector connector;

    public MainFrame(String header) throws InterruptedException, IOException, ParseException {
        super(header);

        setLayout(new FlowLayout());
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println(">>>");
                if (connector != null){
                    connector.close();
                }
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connector = new Connector(url.getText(), port.getText(), login.getText(), password.getText());
                    initGameScreen();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(new Frame(),ex,"ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setSize(600, 400);
        add(url);
        add(port);
        add(login);
        add(password);
        add(button);
    }

    void initGameScreen() throws IOException, ParseException {
        this.remove(this.button);
        this.remove(this.url);
        this.remove(this.port);
        this.remove(this.login);
        this.remove(this.password);
        this.gameBoard = new GameBoard(this.login.getText(), connector);
        this.gameMenu = new GameMenu(this.login.getText());
        gameMenu.setConnector(this.connector);
        this.add(gameBoard);
        this.add(gameMenu);
        revalidate();
        repaint();
    }
}
