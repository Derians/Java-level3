package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Chaykin Ivan
 * @version 13.10.2019
 */

public class ControllerPC {

    @FXML
    TextField msgFieldPC;

    @FXML
    TextArea chatAreaPC;

    public void sendPersMsg () {
        String toNick =  ((WindowPC)msgFieldPC.getScene().getWindow()).getToNick();
        DataOutputStream out = ((WindowPC)msgFieldPC.getScene().getWindow()).getOut();

        try {
            chatAreaPC.appendText(msgFieldPC.getText() + "\n");
            out.writeUTF("/w " + toNick + " " + msgFieldPC.getText());
            msgFieldPC.clear();
            msgFieldPC.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
