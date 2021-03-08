package chat.auth;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseRegService implements RegService {

    private static Connection connection;
    private static Statement stmt;
    private static final Logger logger = Logger.getLogger(BaseRegService.class.getName());

    private static void connectRegistration() throws ClassNotFoundException, SQLException {
        logger.setLevel(Level.ALL);
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Виталий\\Desktop\\java_ult\\ChatServer\\src\\main\\resources\\db\\main.db");
        logger.log(Level.INFO, "Подключен к базе данных");
        stmt = connection.createStatement();
    }

    private static void closeRegistration() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            logger.log(Level.SEVERE, throwables.getMessage(), throwables);
        }
    }

    public static String regInDatabase(String login, String username, String password) throws SQLException, ClassNotFoundException {
        connectRegistration();
        int result = stmt.executeUpdate(String.format("INSERT INTO users (login, username, password)  VALUES ('%s', '%s', '%s')", login, username, password));
        closeRegistration();
        if (result == 1) {
            return username;
        } else {
            return null;
        }
    }

    @Override
    public void startRegistration() {
        logger.log(Level.INFO, "Сервис регистрации запущен");
    }

    @Override
    public void stopRegistration() {
        logger.log(Level.INFO, "Сервис регистрации завершен");
    }


}