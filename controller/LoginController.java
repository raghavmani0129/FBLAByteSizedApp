package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.CaptchaUtil;
import util.DataManager;
import util.ValidationUtil;
import app.Main;


public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField captchaField;

    @FXML
    private Label captchaLabel;

    @FXML
    private Label errorLabel;

    private String currentCaptchaQuestion;
    private final DataManager dataManager = DataManager.getInstance();

    //begin when you're on the login screen
    @FXML
    public void initialize() {
        generateCaptcha();
    }

    //create new captcha question
    private void generateCaptcha() {
        currentCaptchaQuestion = CaptchaUtil.generateCaptchaQuestion();
        captchaLabel.setText("CAPTCHA: " + currentCaptchaQuestion);
        captchaField.clear();
    }

    //login button
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String captchaInput = captchaField.getText().trim();

        //empty fields
        if (email.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        // email format validation
        if (!ValidationUtil.isValidEmail(email)) {
            errorLabel.setText("Invalid email format.");
            generateCaptcha();
            return;
        }

        // check answer
        if (!CaptchaUtil.validateCaptcha(captchaInput)) {
            errorLabel.setText("Incorrect CAPTCHA. Please try again.");
            generateCaptcha();
            return;
        }

        // check credentials
        var user = dataManager.login(email, password);
        if (user == null) {
            errorLabel.setText("Invalid email or password.");
            generateCaptcha();
            return;
        }

        // send user to main app screen
        dataManager.setCurrentUser(user);
        Main.loadScene("view/main.fxml", 1100, 700);
    }

    //signup button
    //move to signup screen instead
    @FXML
    private void handleSignup() {
        Main.loadScene("view/signup.fxml", 420, 500);
    }
}
