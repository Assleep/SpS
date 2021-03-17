import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class GUI extends Application {
    public static Scene primaryScene;
    public static String DIR_TO_SAVE;
    private Properties settings;
    public void launch(){
        Application.launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("/SPS.png"));
        stage.setTitle("SpS");
        MainController mainController = new MainController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
        loader.setController(mainController);
        Parent root = (Parent) loader.load();
        primaryScene = new Scene(root);

        Properties defaultSettings = new Properties();
        defaultSettings.put("download_directory", Paths.get(System.getProperty("user.home")+"\\Downloads\\files_buffer").toString());
        settings = new Properties(defaultSettings);
        DIR_TO_SAVE = settings.getProperty("download_directory");

        File propDir = new File(System.getProperty("user.home"), ".fileworker");
        if(!propDir.exists()) propDir.mkdir();

        File propFile = new File(propDir, "program.properties");
        if(propFile.exists()){
            System.out.println("Getting properties from file");
            try{
                FileInputStream in = new FileInputStream(propFile);
                settings.load(in);
                DIR_TO_SAVE = settings.getProperty("download_directory");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        if(!Files.exists(Paths.get(DIR_TO_SAVE))){
            Files.createDirectory(Paths.get(System.getProperty("user.home")+"\\Downloads\\files_buffer"));
        }
        try{
            FileOutputStream out = new FileOutputStream(propFile);
            settings.store(out, "Program properties");
        }catch(IOException e){
            e.printStackTrace();
        }

        stage.setScene(primaryScene);
        stage.setResizable(false);
        stage.show();
    }
}
