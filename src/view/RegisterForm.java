package view;

import controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RegisterForm {

    public RegisterForm(Stage stage) {

        Label titleLbl = new Label("REGISTER");
        titleLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        TextField username = new TextField();
        TextField email = new TextField();
        PasswordField pass = new PasswordField();
        PasswordField confirm = new PasswordField();

        RadioButton male = new RadioButton("Male");
        RadioButton female = new RadioButton("Female");
        ToggleGroup tg = new ToggleGroup();
        male.setToggleGroup(tg);
        female.setToggleGroup(tg);

        CheckBox agree = new CheckBox("I Agree to the terms & services");
        Button register = new Button("Register");
        Label loginLbl = new Label("Already have account? Log in here!");

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setAlignment(Pos.CENTER);

        form.add(new Label("Username"), 0, 0);
        form.add(username, 1, 0);
        form.add(new Label("Email"), 0, 1);
        form.add(email, 1, 1);
        form.add(new Label("Password"), 0, 2);
        form.add(pass, 1, 2);
        form.add(new Label("Confirm"), 0, 3);
        form.add(confirm, 1, 3);
        form.add(male, 1, 4);
        form.add(female, 1, 5);
        form.add(agree, 1, 6);
        form.add(register, 1, 7);
        form.add(loginLbl, 1, 8);

        VBox centerBox = new VBox(20, titleLbl, form);
        centerBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(centerBox);
        root.setPadding(new Insets(20));

        register.setOnAction(e -> {
            String gender = male.isSelected() ? "Male" :
                            female.isSelected() ? "Female" : "";

            AuthController.register(
                stage,
                username.getText(),
                email.getText(),
                pass.getText(),
                confirm.getText(),
                gender,
                agree.isSelected()
            );
        });

        loginLbl.setOnMouseClicked(e -> new LoginForm(stage));

        stage.setScene(new Scene(root, 420, 480));
        stage.setTitle("KyouhoByt - Register");
        stage.show();
    }
}
