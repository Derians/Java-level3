package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Chaykin Ivan
 * @version 13.10.2019
 */

public class Controller {
    @FXML
    TextField msgField;

    @FXML
    TextArea chatArea;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox loginPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> clientsList;

    @FXML
    HBox registerPanel;

    @FXML
    TextField regLog;

    @FXML
    TextField regNick;

    @FXML
    PasswordField regPass;


    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;
    boolean isAuthorized;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientsList.setVisible(false);
            clientsList.setManaged(false);
            registerPanel.setVisible(true);
            registerPanel.setManaged(true);
        } else {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientsList.setVisible(true);
            clientsList.setManaged(true);
            registerPanel.setVisible(false);
            registerPanel.setManaged(false);
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread thread = new Thread(() -> {
                try {

                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok")) {
                            setAuthorized(true);
                            break;
                        } else if (str.equals("/clearRegFields")) {
                            clearRegField();
                        } else {
                            chatArea.appendText(str + "\n");
                        }
                    }
                    while (true) {
                        String str = in.readUTF();

                        if (str.equals("/serverclosed")) break;
                        if (str.startsWith("/clientlist")) {
                            String[] tokens = str.split(" ");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    clientsList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientsList.getItems().add(tokens[i]);
                                    }
                                }
                            });
                        } else if (str.equals("/timeout")) {
                            out.writeUTF("/end");
                        }  else {
                            chatArea.appendText(str + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthorized(false);
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth() {
        if(socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addNewUser(ActionEvent actionEvent) {
        if(socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF("/addUser " + regLog.getText() + " " + regPass.getText() + " " + regNick.getText());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearRegField() {
        regNick.clear();
        regPass.clear();
        regLog.clear();
    }

    public void selectClient(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2) {
            WindowPC pc = new WindowPC(clientsList.getSelectionModel().getSelectedItem(), out);
            pc.show();
        }
    }


}
