package view;

import controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginForm {

    public LoginForm(Stage stage) {

        Label titleLbl = new Label("LOGIN");
        titleLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label emailLbl = new Label("Email");
        Label passLbl = new Label("Password");
        Label registerLbl = new Label("Don't have account? Sign up here!");

        TextField emailTf = new TextField();
        PasswordField passPf = new PasswordField();

        Button loginBtn = new Button("Login");

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setAlignment(Pos.CENTER);

        form.add(emailLbl, 0, 0);
        form.add(emailTf, 1, 0);
        form.add(passLbl, 0, 1);
        form.add(passPf, 1, 1);
        form.add(loginBtn, 1, 2);
        form.add(registerLbl, 1, 3);

        VBox centerBox = new VBox(20, titleLbl, form);
        centerBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(centerBox);
        root.setPadding(new Insets(20));

        loginBtn.setOnAction(e -> {
            if (emailTf.getText().isEmpty() || passPf.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Email and Password must be filled!");
                return;
            }
            AuthController.login(stage, emailTf.getText(), passPf.getText());
        });

        registerLbl.setOnMouseClicked(e -> new RegisterForm(stage));

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("KyouhoByt - Login");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }
}
