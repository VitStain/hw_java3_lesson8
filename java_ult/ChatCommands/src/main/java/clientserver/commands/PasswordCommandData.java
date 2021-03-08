package clientserver.commands;

import java.io.Serializable;

public class PasswordCommandData implements Serializable {

    private final String oldPassword;
    private final String password;

    public PasswordCommandData(String oldPassword, String password) {
        this.oldPassword = oldPassword;
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getPassword() {
        return password;
    }
}
