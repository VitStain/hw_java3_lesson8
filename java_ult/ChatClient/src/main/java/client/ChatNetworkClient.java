package client;


import client.controllers.AuthController;
import client.controllers.ChangePasswordController;
import client.controllers.ChatController;
import client.controllers.RegController;
import client.models.Network;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

public class ChatNetworkClient extends Application {
    private static Network network;
    private List<String> stringList;
    private Stage primaryStage;
    private Stage authStage;
    public Stage regStage;

    private static final Logger logger = Logger.getLogger(ChatNetworkClient.class.getName());
        private ChatController chatController;


    @Override
    public void start(Stage primaryStage) throws Exception {
        startLogger();
        this.primaryStage = primaryStage;

        network = new Network();
        if (!network.connect()) {
            logger.warning("Проблемы с соединением. Ошибка подключения к серверу");
            showErrorMessage("Проблемы с соединением", "", "Ошибка подключения к серверу");
            return;
        }

        openAuthWindow();
        createMainChatWindow();
    }

    private void startLogger() throws IOException {
        logger.setLevel(Level.ALL);
        Handler handler = new FileHandler("C:\\Users\\Виталий\\Desktop\\java_ult\\ChatClient\\src\\main\\resources\\logs\\clientLog.log");
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
    }


    public void openAuthWindow() throws IOException {
        authStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatNetworkClient.class.getResource("auth-view.fxml"));
        Parent root = loader.load();

        authStage.setTitle("Авторизация");

        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        authStage.setScene(scene);
        authStage.show();

        TimerTask timeout = new TimerTask() {
            @Override
            public void run() {
                if (!primaryStage.isShowing()) {
                    network.close();
                    System.exit(2);
                }
            }
        };
        new Timer().schedule(timeout, AuthController.deadlineReg * 1000);

        AuthController authController = loader.getController();
        authController.setNetwork(network);
        authController.setNetworkClient(this);

    }

    public void createMainChatWindow() throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatNetworkClient.class.getResource("chat-view.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root/*, 600, 400*/));

        chatController = loader.getController();
        chatController.setNetwork(network);

        primaryStage.setOnCloseRequest(windowEvent -> {
            network.sendExitMessage();
            network.close();
            System.exit(2);
        });
    }


    public static void showErrorMessage(String title, String message, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void openMainChatWindow() {
        authStage.close();
        primaryStage.show();

        primaryStage.setTitle("ЧАТ!!!");
        primaryStage.setAlwaysOnTop(false);
        chatController.setLabel(network.getUsername());
        network.waitMessage(chatController);

        Platform.runLater(() -> {
            stringList = new HistoryMessages().getResentMessage(network.getUsername());
            try {
                if (stringList.size() < 100) {
                    stringList.forEach(msg -> chatController.chatHistory.appendText(msg + "\n"));
                } else {
                    for (int i = stringList.size() - 100; i < stringList.size(); i++) {
                        chatController.chatHistory.appendText(stringList.get(i) + "\n");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                logger.info(e.toString());
            }
        });
    }

    public void openRegWindow() throws IOException {
        authStage.close();
        regStage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatNetworkClient.class.getResource("reg-view.fxml"));
        Parent root = loader.load();

        regStage.setTitle("Регистрация");
        regStage.initModality(Modality.WINDOW_MODAL);
        regStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        regStage.setScene(scene);
        regStage.show();
        regStage.showingProperty();

        RegController regController = loader.getController();
        regController.setNetwork(network);
        regController.setNetworkClient(this);

        regStage.setOnCloseRequest(windowEvent -> {
            regStage.close();
            try {
                openAuthWindow();
            } catch (IOException e) {
                e.printStackTrace();
                logger.warning("ошибка открытия окна авторизации");
            }


        });
    }

    public static void createChangePasswordWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatNetworkClient.class.getResource("changePassword-view.fxml"));
//        AnchorPane dialog = loader.load();
        Parent root = loader.load();
        Stage changePasswordStage = new Stage();
        changePasswordStage.setTitle("Изменение пароля");

        changePasswordStage.setScene(new Scene(root));
        ChangePasswordController changePasswordController = loader.getController();
        changePasswordController.changePasswordStage = changePasswordStage;
        changePasswordController.setNetwork(network);
        changePasswordStage.show();
    }



}