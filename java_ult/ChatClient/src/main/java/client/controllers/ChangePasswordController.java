package client.controllers;

import client.ChatNetworkClient;
import client.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;


public class ChangePasswordController {

    public static Stage changePasswordStage;
    public Network network;
    private ChatNetworkClient chatNetworkClient;

    @FXML
    private PasswordField pastPasswordField;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    void goBack() {
        changePasswordStage.close();

    }

    @FXML
    void updatePassword() {
        String oldPassword = pastPasswordField.getText().trim();
        String password = passwordField1.getText().trim();
        if (password.equals(passwordField2.getText().trim())) {
            network.sendUpdatePassword(oldPassword, password);
        } else {
            showNotEquallyPasswords();
        }

    }

    private void showNotEquallyPasswords() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка изменения пароля");
        alert.setContentText("Пароли не совпадают");
        alert.show();
    }

    public static void okUpdatePassword() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Успех");
        alert.setHeaderText("Здравствуйте");
        alert.setContentText("Ваш пароль успешно заменен");
        alert.show();
        changePasswordStage.close();
    }

    public static void errorUpdatePassword(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setContentText(message);
        alert.show();

    }

    public void setNetwork(Network network) {
        this.network = network;
    }
    public void setNetworkClient (ChatNetworkClient chatNetworkClient) {
        this.chatNetworkClient = chatNetworkClient;
    }
}

