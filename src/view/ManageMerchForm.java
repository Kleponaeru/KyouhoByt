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

public class ManageMerchForm {
    private TextField nameField, priceField, stockField;
    private ComboBox<String> typeCombo;
    private TableView<Merch> table;
    private Merch selectedMerch;

    public ManageMerchForm(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar(stage));
        root.setCenter(createContent());
        
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("KyouhoByt - Manage Merch");
        stage.setFullScreen(true);
        stage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu menuMenu = new Menu("Menu");
        MenuItem manageMerchItem = new MenuItem("Manage Merch");
        MenuItem viewTransItem = new MenuItem("View Transaction");

        manageMerchItem.setOnAction(e -> new ManageMerchForm(stage));
        viewTransItem.setOnAction(e -> new TransactionForm(stage));

        menuMenu.getItems().addAll(manageMerchItem, viewTransItem);

        Menu accountMenu = new Menu("Account");
        MenuItem logoutItem = new MenuItem("Logout");

        logoutItem.setOnAction(e -> new LoginForm(stage));

        accountMenu.getItems().add(logoutItem);

        menuBar.getMenus().addAll(menuMenu, accountMenu);
        return menuBar;
    }

    private VBox createContent() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome, Admin");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Input form
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(10));

        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        nameField.setPrefWidth(200);

        Label priceLabel = new Label("Price:");
        priceField = new TextField();
        priceField.setPrefWidth(200);

        Label stockLabel = new Label("Stock:");
        stockField = new TextField();
        stockField.setPrefWidth(200);

        Label typeLabel = new Label("Type:");
        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Clothing", "Accessories", "Collectibles");
        typeCombo.setPrefWidth(200);

        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");

        addBtn.setOnAction(e -> addMerch());
        updateBtn.setOnAction(e -> updateMerch());
        deleteBtn.setOnAction(e -> deleteMerch());
        clearBtn.setOnAction(e -> clearForm());

        formPane.add(nameLabel, 0, 0);
        formPane.add(nameField, 1, 0);
        formPane.add(priceLabel, 0, 1);
        formPane.add(priceField, 1, 1);
        formPane.add(stockLabel, 0, 2);
        formPane.add(stockField, 1, 2);
        formPane.add(typeLabel, 0, 3);
        formPane.add(typeCombo, 1, 3);
        formPane.add(addBtn, 0, 4);
        formPane.add(updateBtn, 1, 4);
        formPane.add(deleteBtn, 2, 4);
        formPane.add(clearBtn, 3, 4);

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
                nameField.setText(newVal.getMerchName());
                priceField.setText(String.valueOf(newVal.getMerchPrice()));
                stockField.setText(String.valueOf(newVal.getMerchStock()));
                typeCombo.setValue(newVal.getMerchType());
            }
        });

        loadMerchData();

        vbox.getChildren().addAll(welcomeLabel, formPane, table);
        return vbox;
    }

    private void loadMerchData() {
        table.getItems().clear();
        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM msmerch");
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

    private void addMerch() {
        if (!validateInput())
            return;

        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO msmerch (MerchName, MerchPrice, MerchStock, MerchType) VALUES (?, ?, ?, ?)");
            ps.setString(1, nameField.getText());
            ps.setInt(2, Integer.parseInt(priceField.getText()));
            ps.setInt(3, Integer.parseInt(stockField.getText()));
            ps.setString(4, typeCombo.getValue());
            ps.executeUpdate();

            showInfo("Success", "Merch added successfully!");
            clearForm();
            loadMerchData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add merch!");
        }
    }

    private void updateMerch() {
        if (selectedMerch == null) {
            showAlert("Error", "Please select a merch to update!");
            return;
        }
        if (!validateInput())
            return;

        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE msmerch SET MerchName = ?, MerchPrice = ?, MerchStock = ?, MerchType = ? WHERE MerchID = ?");
            ps.setString(1, nameField.getText());
            ps.setInt(2, Integer.parseInt(priceField.getText()));
            ps.setInt(3, Integer.parseInt(stockField.getText()));
            ps.setString(4, typeCombo.getValue());
            ps.setInt(5, selectedMerch.getMerchID());
            ps.executeUpdate();

            showInfo("Success", "Merch updated successfully!");
            clearForm();
            loadMerchData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update merch!");
        }
    }

    private void deleteMerch() {
        if (selectedMerch == null) {
            showAlert("Error", "Please select a merch to delete!");
            return;
        }

        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM msmerch WHERE MerchID = ?");
            ps.setInt(1, selectedMerch.getMerchID());
            ps.executeUpdate();

            showInfo("Success", "Merch deleted successfully!");
            clearForm();
            loadMerchData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete merch!");
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || priceField.getText().isEmpty() ||
                stockField.getText().isEmpty() || typeCombo.getValue() == null) {
            showAlert("Error", "All fields must be filled!");
            return false;
        }

        try {
            int price = Integer.parseInt(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (price <= 0 || stock < 0) {
                showAlert("Error", "Price must be positive and stock must be non-negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Price and Stock must be valid numbers!");
            return false;
        }

        return true;
    }

    private void clearForm() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
        typeCombo.setValue(null);
        selectedMerch = null;
        table.getSelectionModel().clearSelection();
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