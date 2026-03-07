package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.CaptchaUtil;
import util.DataManager;
import util.ValidationUtil;
import app.Main;


public class SignupController {

    @FXML
    private TextField nameField;

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

    //start captcha when on signup screen
    @FXML
    public void initialize() {
        generateCaptcha();
    }

    //new captcha
    private void generateCaptcha() {
        currentCaptchaQuestion = CaptchaUtil.generateCaptchaQuestion();
        captchaLabel.setText("CAPTCHA: " + currentCaptchaQuestion);
        captchaField.clear();
    }

    //create account button click
    @FXML
    private void handleCreateAccount() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String captchaInput = captchaField.getText().trim();

        // check for empty fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        // Validate name
        if (!ValidationUtil.isNotEmpty(name)) {
            errorLabel.setText("Please enter your name.");
            return;
        }

        // Validate email format
        if (!ValidationUtil.isValidEmail(email)) {
            errorLabel.setText("Invalid email format. Please enter a valid email address.");
            generateCaptcha();
            return;
        }

        // Validate password strength
        if (!ValidationUtil.isStrongPassword(password)) {
            errorLabel.setText("Password must be at least 8 characters, include an uppercase letter and a number.");
            generateCaptcha();
            return;
        }

        // Validate CAPTCHA
        if (!CaptchaUtil.validateCaptcha(captchaInput)) {
            errorLabel.setText("Incorrect CAPTCHA. Please try again.");
            generateCaptcha();
            return;
        }

        // make sure email isn't already used
        if (dataManager.emailExists(email)) {
            errorLabel.setText("An account with this email already exists.");
            generateCaptcha();
            return;
        }

        // Create new user account
        if (!dataManager.createUser(name, email, password)) {
            errorLabel.setText("Failed to create account. Please try again.");
            generateCaptcha();
            return;
        }

        // Account created successfully, so return to login screen
        errorLabel.setText("Account created! Redirecting to login...");
        Main.loadScene("view/login.fxml", 400, 380);
    }

    //back to login screen
    @FXML
    private void handleBackToLogin() {
        Main.loadScene("view/login.fxml", 400, 380);
    }
}
