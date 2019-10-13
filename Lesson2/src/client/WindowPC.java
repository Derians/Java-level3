package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Chaykin Ivan
 * @version 13.10.2019
 */

public class WindowPC extends Stage {

    private String toNick;
    private DataOutputStream out;

    public WindowPC (String toNick, DataOutputStream out) {
        this.toNick = toNick;
        this.out = out;

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("personalChat.fxml"));
            setTitle("Личный чат с " + toNick);
            setScene(new Scene(root, 300, 300));
            show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getToNick() {
        return toNick;
    }

    public DataOutputStream getOut() {
        return out;
    }
}
