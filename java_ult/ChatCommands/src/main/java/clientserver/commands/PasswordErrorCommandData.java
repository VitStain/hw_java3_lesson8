package clientserver.commands;

import java.io.Serializable;

public class PasswordErrorCommandData implements Serializable {

    private final String errorMessage;

    public PasswordErrorCommandData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
