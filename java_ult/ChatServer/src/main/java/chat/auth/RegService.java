package chat.auth;


import java.sql.SQLException;

public interface RegService {

    void startRegistration() throws ClassNotFoundException, SQLException;
    void stopRegistration() throws SQLException;

}
