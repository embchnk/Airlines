package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Dictionary;
import java.util.Hashtable;

public class Main extends Application {
    private Stage stage;
    private static Main instance;

    public Main() {
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        this.changeScene("start.fxml");

        Hashtable<String, String> dict = new Hashtable<>();
        Model[] admins;
        dict.put("login", "admin");
        dict.put("password", "admin");
        admins = (new Admin()).select(dict);
        for (Model m : admins) {
            System.out.println(((Admin)m).login);
        }
        dict.clear();
        dict.put("model", "Boeing 272");
        admins = (new Plane()).select(dict);
        for (Model m : admins) {
            System.out.println(m);
        }
    }

    public void changeScene(String fxml) throws Exception {
        Parent page = FXMLLoader.load(getClass().getResource(fxml));
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(page);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(page);
        }
        stage.sizeToScene();
        stage.show();
    }

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
