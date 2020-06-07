
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.Optional;


public class LoginController {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    VBox globParent;


    public int id;
    public int id2;
    public int id3;

    public Controller backController;
    private final String PASS = "/Empty";

    public void auth(ActionEvent actionEvent) {
        System.out.println(login.getText() + " " + password.getText());
        System.out.println("authId = " + id);
        globParent.getScene().getWindow().hide();
        getDialog ("/auth ");
    }

    public void sign(ActionEvent actionEvent) {
        System.out.println(login.getText() + " " + password.getText());
        System.out.println("signId = " + id2);
        globParent.getScene().getWindow().hide();
        getDialog ("/sign ");
    }

    public void delUser(ActionEvent actionEvent) {
        System.out.println(login.getText() + " " + password.getText());
        System.out.println("removeId = " + id2);
        globParent.getScene().getWindow().hide();
        getDialog ("/remove ");
    }

    public void disconnect(ActionEvent actionEvent) {
        if (!NetWork.isConnected()) {
            return;
        }
        backController.setClearListCloud();
        System.out.println("disconId = " + id3);
        globParent.getScene().getWindow().hide();
        if (backController.isAuthorized) {
            NetWork.sendMsg(new FileCommand("/end_login", ""));
        }
        login.clear();
        password.clear();
        System.out.println("all_clear");
    }

    public void getDialog (String cmd) {
        backController.setClearListCloud();
        backController.connect();
        if  (!login.getText().trim().isEmpty()) {
            while (true) {
                if (password.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Password is empty! Use empty password.", ButtonType.OK, ButtonType.CANCEL);
                    Optional<ButtonType> result = alert.showAndWait();
                    System.out.println("ButCan= " + result.get().getText());
                    if (result.get().getText().equals("OK")) {
                        System.out.println("You clicked OK");
                        NetWork.sendMsg(new FileCommand(cmd + login.getText() + " " + PASS, ""));
                        break;
                    }
                    if (result.get().getText().equals("Cancel")) {
                        System.out.println("You clicked CANCEL");
                        backController.getStageAuth();
                        break;
                    }
                } else {
                    NetWork.sendMsg(new FileCommand(cmd + login.getText() + " " + password.getText(), ""));
                    break;
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Login is empty! Enter login.", ButtonType.OK);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get().getText().equals("OK")) {
                System.out.println("You clicked OK");
            }
            backController.getStageAuth();
        }
        login.clear();
        password.clear();
    }

}