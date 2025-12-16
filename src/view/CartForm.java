package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CartItem;
import util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CartForm {
    private int userId;
    private String username;
    private TableView<CartItem> table;
    private Label totalLabel;

    public CartForm(Stage stage, int userId) {
        this.userId = userId;
        loadUsername();

        BorderPane root = new BorderPane();
        root.setTop(createMenuBar(stage));
        root.setCenter(createContent());
        
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("KyouhoByt - Cart");
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

        Menu menuMenu = new Menu("Menu");
        MenuItem merchListItem = new MenuItem("Merch List");
        MenuItem cartItem = new MenuItem("Cart");

        merchListItem.setOnAction(e -> new ShopForm(stage, userId));
        cartItem.setOnAction(e -> new CartForm(stage, userId));

        menuMenu.getItems().addAll(merchListItem, cartItem);

        Menu accountMenu = new Menu("Account");
        MenuItem logoutItem = new MenuItem("Logout");

        logoutItem.setOnAction(e -> new LoginForm(stage));

        accountMenu.getItems().add(logoutItem);

        menuBar.getMenus().addAll(menuMenu, accountMenu);
        return menuBar;
    }

    private VBox createContent() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ===== INPUT FORM =====
        Label merchLabel = new Label("-");
        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");

        Button updateQtyBtn = new Button("Update Quantity");

        updateQtyBtn.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Select an item first!");
                return;
            }

            int newQty;
            try {
                newQty = Integer.parseInt(qtyField.getText());
            } catch (Exception ex) {
                showAlert("Error", "Invalid quantity!");
                return;
            }

            try {
                Connection con = DBConnect.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE cart SET Quantity = ? WHERE UserID = ? AND MerchID = ?");
                ps.setInt(1, newQty);
                ps.setInt(2, userId);
                ps.setInt(3, selected.getMerchID());
                ps.executeUpdate();

                loadCartData();
                qtyField.clear();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox inputBox = new VBox(5,
                new Label("Selected Merch:"), merchLabel,
                new Label("New Quantity:"), qtyField,
                updateQtyBtn);

        // ===== TABLE =====
        table = new TableView<>();

        TableColumn<CartItem, Integer> idCol = new TableColumn<>("Merch ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("merchID"));
        idCol.setPrefWidth(100);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Merch Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("merchName"));
        nameCol.setPrefWidth(220);

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(120);

        TableColumn<CartItem, Integer> totalCol = new TableColumn<>("Total Price");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, nameCol, qtyCol, totalCol);

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                merchLabel.setText(n.getMerchName());
                qtyField.setText(String.valueOf(n.getQuantity()));
            }
        });

        // ===== BOTTOM =====
        totalLabel = new Label("Grand Total: 0");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button removeBtn = new Button("Remove Item");
        Button checkoutBtn = new Button("Checkout");

        removeBtn.setOnAction(e -> removeItem());
        checkoutBtn.setOnAction(e -> checkout());

        loadCartData();

        vbox.getChildren().addAll(
                welcomeLabel,
                inputBox,
                table,
                totalLabel,
                removeBtn,
                checkoutBtn);

        return vbox;
    }

    private void loadCartData() {
        table.getItems().clear();
        int grandTotal = 0;

        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT m.MerchName, m.MerchPrice, c.Quantity, m.MerchID " +
                            "FROM cart c JOIN msmerch m ON c.MerchID = m.MerchID " +
                            "WHERE c.UserID = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem(
                        rs.getString("MerchName"),
                        rs.getInt("MerchPrice"),
                        rs.getInt("Quantity"),
                        rs.getInt("MerchID"));

                table.getItems().add(item);
                grandTotal += item.getTotal();
            }

            totalLabel.setText("Grand Total: " + grandTotal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeItem() {
        CartItem selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an item to remove!");
            return;
        }

        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM cart WHERE UserID = ? AND MerchID = ?");
            ps.setInt(1, userId);
            ps.setInt(2, selected.getMerchID());
            ps.executeUpdate();

            showInfo("Success", "Item removed from cart!");
            loadCartData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to remove item!");
        }
    }

    private void checkout() {
        if (table.getItems().isEmpty()) {
            showAlert("Error", "Cart is empty!");
            return;
        }

        try {
            Connection con = DBConnect.getConnection();

            // Create transaction
            PreparedStatement transPs = con.prepareStatement(
                    "INSERT INTO transactionheader (UserID, TransactionDate) VALUES (?, CURDATE())",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            transPs.setInt(1, userId);
            transPs.executeUpdate();

            ResultSet keys = transPs.getGeneratedKeys();
            keys.next();
            int transactionId = keys.getInt(1);

            // Move cart items to transaction details
            for (CartItem item : table.getItems()) {
                PreparedStatement detailPs = con.prepareStatement(
                        "INSERT INTO transactiondetail (TransactionID, MerchID, Quantity) VALUES (?, ?, ?)");
                detailPs.setInt(1, transactionId);
                detailPs.setInt(2, item.getMerchID());
                detailPs.setInt(3, item.getQuantity());
                detailPs.executeUpdate();

                PreparedStatement stockPs = con.prepareStatement(
                        "UPDATE msmerch SET MerchStock = MerchStock - ? WHERE MerchID = ?");
                stockPs.setInt(1, item.getQuantity());
                stockPs.setInt(2, item.getMerchID());
                stockPs.executeUpdate();
            }

            // Clear cart
            PreparedStatement clearPs = con.prepareStatement(
                    "DELETE FROM cart WHERE UserID = ?");
            clearPs.setInt(1, userId);
            clearPs.executeUpdate();

            showInfo("Success", "Checkout complete!");
            loadCartData();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Checkout failed!");
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