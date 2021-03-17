import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Double.parseDouble;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

public class Main {
    private static final double VERSION = 3.3;
    private static double newVersion;
    private static Thread update_thread;

    public static void main(String[] args) throws IOException {
        if(!checkUpdates()){
            start();
        } else{
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(250, 140);
                    frame.setLocationRelativeTo(null);

                    frame.getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    GroupLayout layout = new GroupLayout(frame.getContentPane());
                    frame.getContentPane().setLayout(layout);

                    layout.setAutoCreateGaps(true);
                    layout.setAutoCreateContainerGaps(true);

                    JLabel label = new JLabel("Update is available");
                    JButton update = new JButton("Update");
                    JButton update_later = new JButton("Update later");

                    JProgressBar progressBar = new JProgressBar(0, 100);
                    progressBar.setVisible(false);

                    layout.setHorizontalGroup(layout.createParallelGroup()
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(label))
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(update_later)
                                    .addComponent(update))
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(progressBar)));
                    layout.linkSize(SwingConstants.HORIZONTAL, update_later, update);

                    layout.setVerticalGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(LEADING)
                                    .addComponent(label))
                            .addGroup(layout.createParallelGroup(BASELINE)
                                    .addComponent(update_later)
                                    .addComponent(update))
                            .addGroup(layout.createParallelGroup(BASELINE)
                                    .addComponent(progressBar)));
                    update.addActionListener(event -> {
                        Runnable runnable = ()-> {
                            try {
                                update.setVisible(false);
                                progressBar.setVisible(true);
                                progressBar.setIndeterminate(true);
                                URL url = new URL("https://sps2021.000webhostapp.com/versions/SPS.V" + newVersion + ".rar");
                                URLConnection urlconnection = url.openConnection();
                                urlconnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                                urlconnection.connect();

                                InputStream in = urlconnection.getInputStream();
                                FileOutputStream fos = new FileOutputStream(new File(System.getProperty("user.dir") + "\\SPS.V" + newVersion + ".rar"));

                                byte[] buffer = new byte[1024];
                                int length = -1;
                                while ((length = in.read(buffer)) > -1) {
                                    if (update_thread.isInterrupted()) {
                                        in.close();
                                        fos.close();
                                    }
                                    fos.write(buffer, 0, length);
                                }
                                fos.close();
                                in.close();
                                System.exit(0);
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        };
                        update_thread = new Thread(runnable);
                        update_thread.start();
                    });
                    update_later.addActionListener(e -> {
                        if(update_thread != null)update_thread.interrupt();
                        frame.dispose();
                        start();
                    });
                    frame.setVisible(true);
                }
            });
        }

    }
    public static boolean checkUpdates(){
        try {
            Document doc = Jsoup.connect("https://sps2021.000webhostapp.com").get();
            Elements elements = doc.select("a");
            for(Element element : elements){
                if(element.absUrl("href").indexOf("versions") != -1){
                    newVersion = parseDouble(element.absUrl("href").substring(element.absUrl("href").lastIndexOf("V")+1,element.absUrl("href").length()-4));
                    if(VERSION == newVersion) return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    private static void start(){
        GUI gui = new GUI();
        gui.launch();
    }
}