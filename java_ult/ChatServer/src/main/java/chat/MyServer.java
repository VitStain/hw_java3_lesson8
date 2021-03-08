package chat;

import chat.auth.AuthService;
import chat.auth.BaseAuthService;

import chat.auth.RegService;
import chat.handler.ClientHandler;
import clientserver.Command;
import org.apache.log4j.PropertyConfigurator;


import java.util.Date;
import java.util.logging.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MyServer {


    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();

    protected final Logger serverLogger = Logger.getLogger("server");
    protected final Logger chatLogger = Logger.getLogger("chat");


    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();

    }

    public void start() throws IOException {
        startServerLogger();
        serverLogger.info("Сервер запущен");
        startChatLogger();
        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            serverLogger.severe("Клиенту не удалось присоединиться к серверу");

        } finally {
            serverSocket.close();
            serverLogger.severe("Сервер закрыт");
        }
    }

    private void startServerLogger() {
        serverLogger.setLevel(Level.ALL);
        try {
            Handler handler = new FileHandler("C:\\Users\\Виталий\\Desktop\\java_ult\\ChatServer\\src\\main\\resources\\serverLogs\\serverLog.log");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            serverLogger.addHandler(handler);
//            PropertyConfigurator.configure("C:\\Users\\Виталий\\Desktop\\java_ult\\ChatServer\\src\\main\\resources\\logs\\log4j.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startChatLogger() {
        chatLogger.setLevel(Level.ALL);
        try {
            Handler handler = new FileHandler("C:\\Users\\Виталий\\Desktop\\java_ult\\ChatServer\\src\\main\\resources\\serverLogs\\chatLog.log");
            //описываем формат логирования переписки для более удобного чтения
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return record.getLevel() + " " + (new Date(record.getMillis())).toString() + " " + record.getMessage() + "\n";
                }
            });
            handler.setLevel(Level.ALL);
            chatLogger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //убираем вывод логов переписки в консоль
        chatLogger.setUseParentHandlers(false);

    }

    private void waitAndProcessNewClientConnection() throws IOException {
        serverLogger.info("Сервер ожидает подключения пользователя к порту " + serverSocket.getLocalPort() + "...");
        Socket clientSocket = serverSocket.accept();
        serverLogger.info("Установлено соединение с клиентом, ожидаем авторизацию");
        processClientConnection(clientSocket);
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isUsernameBusy(String clientUsername) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(clientUsername)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
        chatLogger.info(clientHandler.getUsername() + " подключился к чату");
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        List<String> usernames = null;
        try {
            usernames = getAllUsernames();
        } catch (Exception e) {
            serverLogger.warning("Ошибка передачи списка клиентов");
        }
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
        chatLogger.info(clientHandler.getUsername() + " покинул чат");
    }

    public synchronized void broadcastMessage(ClientHandler sender, Command command) {
        chatLogger.fine("User " + sender + " отправлено сообщение");
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(command);

        }
    }

    public synchronized void sendPrivateMessage(String recipient, Command command) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(command);
                chatLogger.fine("User " + client.getUsername() + " написал приватное сообщение");
                break;
            }
        }
    }

    public List<ClientHandler> getClients() {
        return clients;
    }



}
