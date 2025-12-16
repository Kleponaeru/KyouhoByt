package main;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginForm;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        new LoginForm(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
