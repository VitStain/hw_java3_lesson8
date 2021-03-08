package clientserver.commands;

import java.io.Serializable;

public class PasswordOkCommandData implements Serializable {

    private final String password;

    public PasswordOkCommandData(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
