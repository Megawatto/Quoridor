import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Valera on 28.01.2016.
 */
public class TestAsyn {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SwingWorker<String, String> swingWorker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                return "gogogo";
            }

            @Override
            protected void done() {
                System.out.println("DONE");
            }
        };

        swingWorker.execute();
        System.out.println(swingWorker.get());

//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    try {
//                        Thread.sleep(1000);
//                        System.out.println("A work some " + i);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        Thread.sleep(5000);
        System.out.println("i ready to go");
    }
}
