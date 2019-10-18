package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * @author Chaykin Ivan
 * @version 17.10.2019
 */
public class Controller {

    public TextField regLogin;
    public TextField regPass;
    public TextField regNick;

    // chatPanel
    @FXML
    Pane chatPanel;

    @FXML
    TextField msgField;

    @FXML
    TextArea chatArea;

    @FXML
    ListView<String> clientsList;

    // LoginPanel
    @FXML
    Pane loginPanel;

    @FXML
    TextField loginField;

    @FXML
    TextField passwordField;

    // RegPanel
    @FXML
    Pane regPanel;

    @FXML
    Label authErrorLabel;

    @FXML
    Label regErrorLabel;


    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8877;
    private boolean isAuthorized;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
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
                        if (str.startsWith("/authOk")) {
                            setAuthorized(true);
                            switchView("chatPanel");
                            break;
                        } else if (str.equals("/clearRegFields")) {
                            clearRegField();
                        } else if (str.startsWith("/errorAuth")){
                            String[] errors = str.split(" = ");
                            setError("auth", errors[1]);
                        } else if (str.startsWith("/errorReg")){
                            String[] errors = str.split(" = ");
                            setError("reg", errors[1]);
                        } else {
                            chatArea.appendText(str + "\n");
                        }
                    }
                    while (true) {
                        String str = in.readUTF();

                        if (str.equals("/serverClosed")) {
                            switchView("loginPanel");
                            break;
                        }
                        if (str.startsWith("/clientList")) {
                            String[] tokens = str.split(" ");
                            Platform.runLater(() -> {
                                clientsList.getItems().clear();
                                for (int i = 1; i < tokens.length; i++) {
                                    clientsList.getItems().add(tokens[i]);
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
                }
                finally {
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

    private void setError(String type, String error) {
        Platform.runLater(() -> {
            switch (type) {
                case "auth":
                    authErrorLabel.setVisible(true);
                    authErrorLabel.setText(error);
                    break;
                case "reg":
                    regErrorLabel.setVisible(true);
                    regErrorLabel.setText(error);
                    break;
                case "clear":
                    authErrorLabel.setVisible(false);
                    authErrorLabel.setText("");
                    regErrorLabel.setVisible(false);
                    regErrorLabel.setText("");
                    break;
            }
        });
    }

    private void switchView(String view) {
        switch (view) {
            case "chatPanel":
                Platform.runLater(() -> {
                    chatPanel.setVisible(true);
                    loginPanel.setVisible(false);
                    regPanel.setVisible(false);
                });
                break;
            case "regPanel":
                Platform.runLater(() -> {
                    chatPanel.setVisible(false);
                    loginPanel.setVisible(false);
                    regPanel.setVisible(true);
                });
                break;
            default:
                Platform.runLater(() -> {
                    chatPanel.setVisible(false);
                    loginPanel.setVisible(true);
                    regPanel.setVisible(false);
                    setError("clear", "");
                });
                break;
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

    public void switchToSignUp() {
        switchView("regPanel");
    }

    public void switchToSignIn() {
        switchView("loginPanel");
    }

    private void clearRegField() {
        regNick.clear();
        regPass.clear();
        regLogin.clear();
    }

    public void doLogin() {
        if(socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText().trim()
                    + " " + passwordField.getText().trim());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doRegister() {
        if(socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF("/addUser " + regLogin.getText().trim()
                    + " " + regNick.getText().trim()
                    + " " + regPass.getText().trim());
            clearRegField();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
