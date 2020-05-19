import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginController {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    VBox globParent;

    @FXML
    ListView<String> clientList;



    public int id;

    private boolean isAuthorized;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;

    public Controller backController;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if(!isAuthorized) {
            clientList.setVisible(false);
            clientList.setManaged(false);
        } else {
            clientList.setVisible(true);
            clientList.setManaged(true);
        }
    }

    public void auth(ActionEvent actionEvent) {
        System.out.println(login.getText() + " " + password.getText());
        System.out.println("id = " + id);
        globParent.getScene().getWindow().hide();

        if(socket == null || socket.isClosed()) {
            connect();
        }

        if  (login.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {
            NetWork.sendMsg(new FileCommand("/auth null null", ""));
        } else {
            NetWork.sendMsg(new FileCommand("/auth ",  login.getText() + password.getText()));
        }
        login.clear();
        password.clear();
    }

    public void sign(ActionEvent actionEvent) {
        System.out.println(login.getText() + " " + password.getText());
        System.out.println("id = " + id);
        globParent.getScene().getWindow().hide();
    }

    public void connect() {

        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authok")) {
                                setAuthorized(true);
                                break;
                            } else if (str.equals("/closeAuth")) {
                                out.writeUTF("/end2");
                                break;
                            } else {
                                // textArea.appendText(str + "\n");
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            if(str.startsWith("/")) {

                                if (str.equals("/serverclosed"))  {
                                    break;
                                }
                                if (str.startsWith("/clientlist ")) {
                                    String[] tokens = str.split(" ");
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            clientList.getItems().clear();
                                            for (int i = 1; i < tokens.length; i++) {
                                                clientList.getItems().add(tokens[i]);
                                            }
                                        }
                                    });
                                }
                            } else {
                                // textArea.appendText(str + "\n");
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                            System.out.println("Сокет закрыт" + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF("send MSG");
            System.out.println("send MSG");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}