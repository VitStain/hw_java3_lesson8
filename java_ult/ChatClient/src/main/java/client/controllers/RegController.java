package client.controllers;

import client.ChatNetworkClient;
import client.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;



public class RegController {


    @FXML
    public TextField loginFieldReg;
    @FXML
    public PasswordField passwordFieldReg;
    @FXML
    public TextField usernameFieldReg;

    private Network network;
    private ChatNetworkClient chatNetworkClient;

    @FXML
    public void doReg() throws IOException {
        String loginReg = loginFieldReg.getText();
        String usernameReg = usernameFieldReg.getText();
        String passwordReg = passwordFieldReg.getText();

        if (loginReg.isBlank() || passwordReg.isBlank() || usernameReg.isBlank()) {
            ChatNetworkClient.showErrorMessage("Ошибка регистрации", "Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }

        String regErrorMessage = network.sendRegCommand(loginReg, usernameReg, passwordReg);
        if (regErrorMessage != null) {
            chatNetworkClient.showErrorMessage("Ошибка регистрации", "regErrorMessage", regErrorMessage);
        } else {
            chatNetworkClient.regStage.close();
            chatNetworkClient.openAuthWindow();

        }

    }

    @FXML
    void goBack() throws IOException {
        chatNetworkClient.regStage.close();
        chatNetworkClient.openAuthWindow();
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setNetworkClient(ChatNetworkClient chatNetworkClient) {
        this.chatNetworkClient = chatNetworkClient;
    }
}