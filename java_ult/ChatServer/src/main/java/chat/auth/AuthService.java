package chat.auth;

import java.sql.SQLException;

public interface AuthService {

    void startAuthentication() throws ClassNotFoundException, SQLException;

    void stopAuthentication() throws SQLException;

    String getUsernameByLoginAndPassword(String login, String password) throws SQLException, ClassNotFoundException;
}
