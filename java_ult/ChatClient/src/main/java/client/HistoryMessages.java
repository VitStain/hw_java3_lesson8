
package client;



import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

import java.util.stream.Collectors;

public class HistoryMessages {
    private final String userDirectory = "C:\\Users\\Виталий\\Desktop\\java_ult\\ChatClient\\src\\main\\resources";
    private String username;
    private String pathToLogFile;
    private List<String> messages;

    private void init(String username) {
        this.username = username;
        this.pathToLogFile = "C:\\Users\\Виталий\\Desktop\\java_ult\\ChatClient\\src\\main\\resources\\text\\history.txt";

    }
    private void createUserFiles(String username) {
        init(username);
        try {
            try {
                try {
                    Files.createFile(Paths.get(pathToLogFile));
                } catch (NoSuchFileException e) {
                    Files.createDirectory(Paths.get(userDirectory));
                    Files.createFile(Paths.get(pathToLogFile));
                }
            } catch (FileAlreadyExistsException e) {
                System.out.println(e.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    public synchronized List<String> getResentMessage(String username) {
        init(username);
        try {
            try {
                messages = Files.lines(Paths.get(pathToLogFile), StandardCharsets.UTF_8).collect(Collectors.toList());
            } catch (NoSuchFileException e) {
                createUserFiles(username);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return messages;
    }
    public void saveChatStory(String username, String messageIn) {
        init(username);
        try {
            try {
                BufferedWriter br = Files.newBufferedWriter(Paths.get(pathToLogFile), StandardCharsets.UTF_8,
                        StandardOpenOption.APPEND);
                br.write(messageIn);
                br.newLine();
                br.flush();
                br.close();
            } catch (NoSuchFileException e) {
                createUserFiles(username);
                saveChatStory(username, messageIn);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}


