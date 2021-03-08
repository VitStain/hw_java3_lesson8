import chat.MyServer;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class ServerApp {

    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static final int DEFAULT_PORT = 8828;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
//        PropertyConfigurator.configure("C:\\Users\\Виталий\\Desktop\\java_ult\\ChatServer\\src\\main\\resources\\logs\\log4j.properties");
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            new MyServer(port).start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка запуска сервера!", e.getMessage());
            System.exit(1);
        }
    }
}