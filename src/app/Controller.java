package app;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {
    public Button goToLoginButton;
    public Button startButton;

    public void handleGoToLoginButtonClicked() {
        try {
            Main.getInstance().changeScene("admins/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleStartButtonClicked() {
        System.out.println("Start without logging in");
    }
}
