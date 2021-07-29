import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.saiushev.sps.controller.MainController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ApplicationRunner extends Application {
    public static Scene primaryScene;
    public static String DIR_TO_SAVE;
    public void launch(){
        Application.launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("/resources/SPS.png"));
        stage.setTitle("SpS");
        MainController mainController = new MainController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/saiushev/sps/view/gui.fxml"));
        loader.setController(mainController);
        Parent root = (Parent) loader.load();
        primaryScene = new Scene(root);

        Properties defaultSettings = new Properties();
        defaultSettings.put("download_directory", Paths.get(System.getProperty("user.home")+"\\Downloads\\Sps").toString());
        Properties settings = new Properties(defaultSettings);
        DIR_TO_SAVE = settings.getProperty("download_directory");

        File propDir = new File(System.getProperty("user.home"), ".sps");
        if(!propDir.exists()) propDir.mkdir();

        File propFile = new File(propDir, "program.properties");
        if(propFile.exists()){
            try{
                FileInputStream in = new FileInputStream(propFile);
                settings.load(in);
                DIR_TO_SAVE = settings.getProperty("download_directory");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        if(!Files.exists(Paths.get(DIR_TO_SAVE))){
            Files.createDirectory(Paths.get(System.getProperty("user.home")+"\\Downloads\\Sps"));
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
