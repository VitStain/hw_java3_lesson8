package chat.auth;


import chat.handler.ClientHandler;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseAuthService implements AuthService {

    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;
    private static final Logger logger = Logger.getLogger(BaseAuthService.class.getName());


    private static void connectAuthentication() throws ClassNotFoundException, SQLException {
        logger.setLevel(Level.ALL);
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Виталий\\Desktop\\java_ult\\ChatServer\\src\\main\\resources\\db\\main.db");
        logger.log(Level.INFO, "Подключен к базе данных");
        stmt = connection.createStatement();
    }

    private static void closeAuthentication() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            logger.log(Level.SEVERE, throwables.getMessage(), throwables);
        }
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) throws SQLException, ClassNotFoundException {
        connectAuthentication();
        rs = stmt.executeQuery(String.format("SELECT password, username FROM users WHERE login = '%s'", login));
        String username = rs.getString("username");
        System.out.println(rs.getString("username"));

        if (rs.getString("password").equals(password)) {
            closeAuthentication();
            return username;
        }
        closeAuthentication();
        return null;
    }

    @Override
    public void startAuthentication() {
        logger.log(Level.INFO, "Сервис аутентификации запущен");
    }

    @Override
    public void stopAuthentication() {
        logger.log(Level.INFO, "Сервис аутентификации завершен");
    }
}
