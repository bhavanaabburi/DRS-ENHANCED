package com.drs.drs.initial.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }

        if ("admin".equals(username) && "admin123".equals(password)) {
            openDashboard();
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    private void openDashboard() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/Dashboard.fxml")
            );

            Parent dashboardRoot = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();

            Scene dashboardScene = new Scene(dashboardRoot);

            stage.setScene(dashboardScene);
            stage.setTitle("DRS - Dashboard");

        } catch (IOException e) {
            messageLabel.setText("Unable to open dashboard.");
            e.printStackTrace();
        }
    }
}