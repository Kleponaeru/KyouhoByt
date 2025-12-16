package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Merch;
import util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ShopForm {
    private int userId;
    private String username;
    private TableView<Merch> table;
    private Label nameLabel, totalPriceLabel;
    private Spinner<Integer> amountSpinner;
    private Merch selectedMerch;

    public ShopForm(Stage stage, int userId) {
        this.userId = userId;
        loadUsername();

        BorderPane root = new BorderPane();
        root.setTop(createMenuBar(stage));
        root.setCenter(createContent());
        
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("KyouhoByt - Shop");
        stage.setFullScreen(true);
        stage.show();
    }

    private void loadUsername() {
        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT Username FROM msuser WHERE UserID = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                username = rs.getString("Username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        // Menu Menu
        Menu menuMenu = new Menu("Menu");
        MenuItem merchListItem = new MenuItem("Merch List");
        MenuItem cartItem = new MenuItem("Cart");

        merchListItem.setOnAction(e -> new ShopForm(stage, userId));
        cartItem.setOnAction(e -> new CartForm(stage, userId));

        menuMenu.getItems().addAll(merchListItem, cartItem);

        // Account Menu
        Menu accountMenu = new Menu("Account");
        MenuItem logoutItem = new MenuItem("Logout");

        logoutItem.setOnAction(e -> {
            new LoginForm(stage);
        });

        accountMenu.getItems().add(logoutItem);

        menuBar.getMenus().addAll(menuMenu, accountMenu);
        return menuBar;
    }

    private VBox createContent() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        // Welcome label
        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Details section
        GridPane detailsPane = new GridPane();
        detailsPane.setHgap(10);
        detailsPane.setVgap(10);
        detailsPane.setPadding(new Insets(10));

        Label nameTitle = new Label("Name:");
        nameLabel = new Label("-");
        Label totalTitle = new Label("Total Price:");
        totalPriceLabel = new Label("0");
        Label amountTitle = new Label("Amount:");

        amountSpinner = new Spinner<>(1, 100, 1);
        amountSpinner.setEditable(true);
        amountSpinner.setPrefWidth(100);
        amountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());

        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setOnAction(e -> addToCart());

        detailsPane.add(nameTitle, 0, 0);
        detailsPane.add(nameLabel, 1, 0);
        detailsPane.add(totalTitle, 0, 1);
        detailsPane.add(totalPriceLabel, 1, 1);
        detailsPane.add(amountTitle, 0, 2);
        detailsPane.add(amountSpinner, 1, 2);
        detailsPane.add(addToCartBtn, 1, 3);

        // Table
        table = new TableView<>();

        TableColumn<Merch, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("merchID"));

        TableColumn<Merch, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("merchName"));

        TableColumn<Merch, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("merchPrice"));

        TableColumn<Merch, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("merchStock"));

        TableColumn<Merch, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("merchType"));

        table.getColumns().addAll(idCol, nameCol, priceCol, stockCol, typeCol);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedMerch = newVal;
                nameLabel.setText(newVal.getMerchName());
                updateTotalPrice();
            }
        });

        loadMerchData();

        vbox.getChildren().addAll(welcomeLabel, detailsPane, table);
        return vbox;
    }

    private void loadMerchData() {
        table.getItems().clear();
        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM msmerch WHERE MerchStock > 0");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                table.getItems().add(new Merch(
                        rs.getInt("MerchID"),
                        rs.getString("MerchName"),
                        rs.getInt("MerchPrice"),
                        rs.getInt("MerchStock"),
                        rs.getString("MerchType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotalPrice() {
        if (selectedMerch != null) {
            int total = selectedMerch.getMerchPrice() * amountSpinner.getValue();
            totalPriceLabel.setText(String.valueOf(total));
        }
    }

    private void addToCart() {
        if (selectedMerch == null) {
            showAlert("Error", "Please select a merch!");
            return;
        }

        int amount = amountSpinner.getValue();

        if (amount > selectedMerch.getMerchStock()) {
            showAlert("Error", "Not enough stock!");
            return;
        }

        try {
            Connection con = DBConnect.getConnection();

            // Check if merch already in cart
            PreparedStatement checkPs = con.prepareStatement(
                    "SELECT * FROM cart WHERE UserID = ? AND MerchID = ?");
            checkPs.setInt(1, userId);
            checkPs.setInt(2, selectedMerch.getMerchID());
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // Update existing cart item
                int currentQty = rs.getInt("Quantity");
                PreparedStatement updatePs = con.prepareStatement(
                        "UPDATE cart SET Quantity = ? WHERE UserID = ? AND MerchID = ?");
                updatePs.setInt(1, currentQty + amount);
                updatePs.setInt(2, userId);
                updatePs.setInt(3, selectedMerch.getMerchID());
                updatePs.executeUpdate();
            } else {
                // Insert new cart item
                PreparedStatement insertPs = con.prepareStatement(
                        "INSERT INTO cart (UserID, MerchID, Quantity) VALUES (?, ?, ?)");
                insertPs.setInt(1, userId);
                insertPs.setInt(2, selectedMerch.getMerchID());
                insertPs.setInt(3, amount);
                insertPs.executeUpdate();
            }

            showInfo("Success", "Added to cart!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add to cart!");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setTitle(title);
        alert.showAndWait();
    }
}