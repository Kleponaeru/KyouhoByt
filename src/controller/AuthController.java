package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import util.DBConnect;
import view.LoginForm;
import view.ShopForm;
import view.ManageMerchForm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthController {

    public static void login(Stage stage, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            alert("Email and Password must be filled");
            return;
        }

        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM msuser WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                alert("Email doesn't exist");
                return;
            }

            if (!rs.getString("password").equals(password)) {
                alert("Wrong password");
                return;
            }

            String role = rs.getString("role").trim().toLowerCase();
            int userId = rs.getInt("UserID"); // Fixed: Use correct column name

            // Show success message
            info("Login success");

            // Navigate based on role
            if (role.equals("admin")) {
                new ManageMerchForm(stage);
            } else {
                new ShopForm(stage, userId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            alert("Error: " + e.getMessage());
        }
    }

    public static void register(Stage stage, String username, String email,
            String pass, String confirm, String gender, boolean agree) {

        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            alert("All fields must be filled");
            return;
        }
        if (username.length() < 5 || username.length() > 25) {
            alert("Username length invalid");
            return;
        }
        if (pass.length() < 8 || pass.length() > 15) {
            alert("Password length invalid");
            return;
        }
        if (!pass.equals(confirm)) {
            alert("Password mismatch");
            return;
        }
        if (!email.endsWith("@gmail.com")) {
            alert("Email must end with @gmail.com");
            return;
        }
        if (gender.isEmpty()) {
            alert("Gender must be chosen");
            return;
        }
        if (!agree) {
            alert("Terms must be accepted");
            return;
        }

        try {
            Connection con = DBConnect.getConnection();

            PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM msuser WHERE email = ?");
            check.setString(1, email);
            if (check.executeQuery().next()) {
                alert("Email already exists");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO msuser VALUES (NULL, ?, ?, ?, ?, 'customer')");
            ps.setString(1, email);
            ps.setString(2, username);
            ps.setString(3, pass);
            ps.setString(4, gender);
            ps.executeUpdate();

            info("Register success");
            new LoginForm(stage);

        } catch (Exception e) {
            e.printStackTrace();
            alert("Registration error: " + e.getMessage());
        }
    }

    private static void alert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    private static void info(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }

}