package clientserver.commands;

import java.io.Serializable;

public class RegOkCommandData implements Serializable {

    private final String username;

    public RegOkCommandData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
