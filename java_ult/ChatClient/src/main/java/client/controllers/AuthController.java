package client.controllers;

import client.ChatNetworkClient;
import client.models.Network;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;


public class AuthController {

    @FXML
    public Label authLabel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    private Network network;
    private ChatNetworkClient chatNetworkClient;
    public static int deadlineReg = 120;

    @FXML
    public void openReg() throws IOException {
        chatNetworkClient.openRegWindow();
    }

    @FXML
    void initialize() {
        Thread thread = new Thread(() -> {
            long startTimeAuth = System.currentTimeMillis();
            try {
                int seconds = 0;
                while (seconds < deadlineReg) {
                    Thread.sleep(1000);
                    seconds = (int) ((System.currentTimeMillis() - startTimeAuth) / 1000);
                    String msg = "Авторизируйтесь, осталось: " + (deadlineReg - seconds) + " секунд";
                    Platform.runLater(() -> authLabel.setText(msg));
                }

            } catch (InterruptedException e) {
                System.out.println("Пользователь не успел авторизироваться.");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void checkAuth() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isBlank() || password.isBlank()) {
            ChatNetworkClient.showErrorMessage("Ошибка авторизации", "Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }

        String authErrorMessage = network.sendAuthCommand(login, password);
        if (authErrorMessage != null) {
            ChatNetworkClient.showErrorMessage("Ошибка авторизации", "Ошибка authErrorMessage", authErrorMessage);
        } else {
            chatNetworkClient.openMainChatWindow();
        }

    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setNetworkClient(ChatNetworkClient chatNetworkClient) {
        this.chatNetworkClient = chatNetworkClient;
    }
}

