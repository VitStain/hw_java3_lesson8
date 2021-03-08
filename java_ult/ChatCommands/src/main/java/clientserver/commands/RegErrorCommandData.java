package clientserver.commands;

import java.io.Serializable;

public class RegErrorCommandData implements Serializable {

    private final String errorMessage;

    public RegErrorCommandData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
