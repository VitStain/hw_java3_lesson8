package client.models;


import client.controllers.ChangePasswordController;
import clientserver.Command;
import clientserver.commands.*;
import client.ChatNetworkClient;
import client.controllers.ChatController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

public class Network {

    private static final String SERVER_ADRESS = "localhost";
    private static final int SERVER_PORT = 8828;
    private final String host;
    private final int port;
    private ObjectOutputStream dataOutputStream;
    private ObjectInputStream dataInputStream;
    private Socket socket;
    private static final Logger logger = Logger.getLogger(Network.class.getName());
    private ChatController chatController;
    private String username;
    public static List<String> userList = new ArrayList<>();



    public Network() throws IOException {
        this(SERVER_ADRESS, SERVER_PORT);
        startLogger();
    }

    private void startLogger() throws IOException {
        logger.setLevel(Level.ALL);
        Handler handler = new FileHandler("C:\\Users\\Виталий\\Desktop\\java_ult\\ChatClient\\src\\main\\resources\\logs\\clientLog.log");
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;

    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new ObjectInputStream(socket.getInputStream());

            return true;

        } catch (IOException e) {
            logger.warning("Соединение не было установлено!");
//            e.printStackTrace();
            return false;
        }

    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
//            e.printStackTrace();
            logger.warning("ошибка закрытия!");
        }
    }

    public void waitMessage(ChatController chatController) {

        Thread thread = new Thread(() -> {
            try {
                while (true) {

                    Command command = readCommand();
                    if (command == null) {
                        logger.severe("Ошибка сервера - неверная команда");
                        ChatNetworkClient.showErrorMessage("Error", "Ошибка сервера", "Получена неверная команда");
                        continue;
                    }

                    switch (command.getType()) {
                        case INFO_MESSAGE: {
                            MessageInfoCommandData data = (MessageInfoCommandData) command.getData();
                            String message = data.getMessage();
                            String sender = data.getSender();
                            String formattedMessage = sender != null ? String.format("%s: %s", sender, message) : message;
                            Platform.runLater(() -> {
                                chatController.appendMessage(formattedMessage);
                            });
                            break;
                        }
                        case ERROR: {
                            ErrorCommandData data = (ErrorCommandData) command.getData();
                            String errorMessage = data.getErrorMessage();
                            Platform.runLater(() -> {
                                ChatNetworkClient.showErrorMessage("Error", "Server error", errorMessage);
                            });
                            break;
                        }
                        case CHANGENAME_OK: {
                            ChangeNameOkCommandData data = (ChangeNameOkCommandData) command.getData();
                            this.username = data.getUsername();

                            break;
                        }
                        case UPDATE_PASSWORD_OK: {
                            PasswordOkCommandData data = (PasswordOkCommandData) command.getData();
                            data.getPassword();
                            Platform.runLater(() -> {

                                ChangePasswordController.okUpdatePassword();
                            });
                            break;
                        }

                        case UPDATE_USERS_LIST: {
                            List<String> data = (List<String>) command.getData();
                            userList.clear();
                            userList.addAll(data);
                            Platform.runLater(chatController::newUserList);
                            break;
                        }
                        case CHANGENAME_ERROR: {
                            ChangeNameErrorCommandData data = (ChangeNameErrorCommandData) command.getData();
                            Platform.runLater(() -> {
                                ChatNetworkClient.showErrorMessage("Ошибка смены имени!", data.getErrorMessage(), "Повторите ввод.");
                                logger.info("Ошибка смены имени!");
                            });
                            break;
                        }
                        case UPDATE_PASSWORD_ERROR: {
                            PasswordErrorCommandData data = (PasswordErrorCommandData) command.getData();
                            Platform.runLater(() -> {
                                ChatNetworkClient.showErrorMessage("Ошибка изменения пароля!", data.getErrorMessage(), "Повторите ввод.");
                                ChangePasswordController.errorUpdatePassword("Ошибка изменения пароля!");
                            });
                            break;
                        }
                        default: {
                            Platform.runLater(() -> {
                                ChatNetworkClient.showErrorMessage("Error", "Unknown command from server!", command.getType().toString());
                                logger.severe("Unknown command from server!");
                            });
                        }
                    }

                }
            } catch (IOException e) {
                logger.info(this.username = getUsername() + " отсоединился");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public String sendAuthCommand(String login, String password) {
        try {
            Command authCommand = Command.authCommand(login, password);
            dataOutputStream.writeObject(authCommand);
//            dataOutputStream.flush();
            Command command = readCommand();
            if (command == null) {
                return "Ошибка чтения команды с сервера";
            }

            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    this.username = data.getUsername();
                    return null;
                }

                case AUTH_ERROR: {
                    AuthErrorCommandData data = (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }
                case ERROR: {
                    ErrorCommandData data = (ErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }

                default:
                    return "Unknown type of command: " + command.getType();

            }
        } catch (IOException e) {
            logger.severe("ошибка sendAuthCommand");
            e.printStackTrace();
            return e.getMessage();
        }
    }


    public String getUsername() {
        return username;
    }


    public void sendMessage(String message) throws IOException {
        sendMessage(Command.publicMessageCommand(username, message));
    }

    public void sendMessage(Command command) throws IOException {
        dataOutputStream.writeObject(command);
//        dataOutputStream.flush();
    }


    public void sendPrivateMessage(String message, String recipient) throws IOException {
        Command command = Command.privateMessageCommand(recipient, message);
        sendMessage(command);
    }

    public Command readCommand() throws IOException {
        try {
            return (Command) dataInputStream.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Получен неизвестный объект";
            System.err.println(errorMessage);
            e.printStackTrace();
            logger.severe(errorMessage);
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }

    public void sendExitMessage() {
        Command command = Command.endCommand();
        try {
            sendMessage(command);
        } catch (IOException e) {
            logger.severe("ошибка sendExitMessage");
            e.printStackTrace();
        }
    }

    public String sendRegCommand(String login, String username, String password) {
        try {
            Command regCommand = Command.regCommand(login, username, password);
            dataOutputStream.writeObject(regCommand);

            Command command = readCommand();
            if (command == null) {
                return "Ошибка чтения команды с сервера";
            }

            switch (command.getType()) {
                case REG_OK: {
                    RegOkCommandData data = (RegOkCommandData) command.getData();
                    ChatNetworkClient.showErrorMessage("Поздравляю!", "Регистрация завершилась!", "Вы успешно зарегистрировались!");
                    return null;
                }
                case REG_ERROR: {
                    RegErrorCommandData data = (RegErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }

                case AUTH_ERROR:
                case ERROR: {
                    AuthErrorCommandData data = (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }

                default:
                    return "Unknown type of command: " + command.getType();

            }
        } catch (IOException e) {
            logger.severe("ошибка sendRegCommand");
            e.printStackTrace();
            return e.getMessage();
        }
    }


    public void sendChangeNameCommand(String lastUsername, String username) {
        try {
            Command changeNameCommand = Command.changeNameCommand(lastUsername, username);
            dataOutputStream.writeObject(changeNameCommand);
//            dataOutputStream.flush();
        } catch (IOException e) {
            logger.severe("ошибка sendChangeNameCommand");
            e.printStackTrace();
        }
    }


    public void sendUpdatePassword(String oldPassword, String password) {
        try {
            Command changePasswordCommand = Command.PasswordCommand(oldPassword, password);
            dataOutputStream.writeObject(changePasswordCommand);
        } catch (IOException e) {
            logger.severe("ошибка sendUpdatePassword");
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObjectOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public ObjectInputStream getDataInputStream() {
        return dataInputStream;
    }

}

