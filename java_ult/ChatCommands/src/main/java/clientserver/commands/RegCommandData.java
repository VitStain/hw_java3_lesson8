package clientserver.commands;

import java.io.Serializable;

public class RegCommandData implements Serializable{

    private final String login;
    private final String username;
    private final String password;

    public RegCommandData(String login, String username, String password) {
        this.login = login;
        this.username = username;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
