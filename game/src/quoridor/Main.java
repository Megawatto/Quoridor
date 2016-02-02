package quoridor;

import org.json.simple.parser.ParseException;
import quoridor.graphic.MainFrame;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Valera on 21.01.2016.
 */

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException, ParseException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MainFrame("Quoridor game");
                } catch (InterruptedException | IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
