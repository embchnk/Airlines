package app.admins;

import app.Main;
import app.db.Admin;
import app.db.Model;
import app.db.RegistrationCode;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Hashtable;

public class Controller {
    // login
    public TextField loginField;
    public TextField passField;
    public Button skipButton;
    public Button loginButton;
    public Button registerButton;
    // registration
    public Button submitButton;
    public Button cancelButton;
    public TextField repeatPassField;
    public TextField registrationCodeField;

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
        try {
            Main.getInstance().changeScene("admins/register.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * Method used to register new admin. We need to pass correct registrationCode, same passwords
     * and unique login
     */
    public void handleSubmitButtonClicked() {
        Model[] modelsArray;
        Admin newAdmin = new Admin();
        RegistrationCode regCode = new RegistrationCode();
        Hashtable<String, String> dict = new Hashtable<>();
        String pass1 = passField.getText();
        String pass2 = repeatPassField.getText();
        String sRegCode = registrationCodeField.getText();
        String login = loginField.getText();

        /*
         * if passed passwords are same
         */
        if (pass1.equals(pass2)) {
            try {
                // check if passed registrationCode exists in database
                dict.put("code", sRegCode);
                modelsArray = regCode.select(dict);
                if (modelsArray.length != 0) {
                    // add new admin
                    dict.clear();
                    dict.put("login", login);
                    dict.put("password", (new Base64()).encodeAsString(pass1.getBytes()));
                    newAdmin.insert(dict);
                    // delete used code from database
                    dict.clear();
                    dict.put("code", sRegCode);
                    regCode.delete(dict);
                    // return to login screen
                    Main.getInstance().changeScene("admins/login.fxml");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Passwords are not the same!");
        }
    }

    public void handleCancelButtonClicked() {
        try {
            Main.getInstance().changeScene("admins/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
