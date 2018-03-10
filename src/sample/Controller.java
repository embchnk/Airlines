package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.swing.*;

public class Controller {
    public TextField loginField;
    public TextField passField;
    public Button skipButton;
    public Button loginButton;
    public Button registerButton;
    public Button goToLoginButton;
    public Button startButton;

    public void handleSkipButtonClicked(ActionEvent e) {
        System.out.println("Skip!");
    }

    public void handleLoginButtonClicked() {
        try {
            System.out.println("Login!");
            System.out.println(loginField.getText());
            System.out.println(passField.getText());
            // admin menu:
            // Main.getInstance().changeScene("start.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRegisterButtonClicked() {
        System.out.println("Register!");
    }

    public void handleGoToLoginButtonClicked() {
        try {
            Main.getInstance().changeScene("login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleStartButtonClicked() {
        System.out.println("Start without logging in");
    }
}
