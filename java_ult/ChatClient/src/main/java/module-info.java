module ChatClient {
    requires javafx.controls;
    requires javafx.fxml;
    requires ChatCommands;
    requires java.logging;

    opens client.controllers to javafx.fxml;
    exports client;
}