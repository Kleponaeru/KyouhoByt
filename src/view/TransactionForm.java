package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TransactionFull;
import util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransactionForm {
    private TableView<TransactionFull> transactionTable;

    public TransactionForm(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar(stage));
        root.setCenter(createContent());
        
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("KyouhoByt - Transactions");
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

        Label transLabel = new Label("Transactions:");
        transLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Single Transaction Table
        transactionTable = new TableView<>();

        TableColumn<TransactionFull, Integer> idCol = new TableColumn<>("Transaction ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("transactionID"));

        TableColumn<TransactionFull, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<TransactionFull, String> merchCol = new TableColumn<>("Merch Name");
        merchCol.setCellValueFactory(new PropertyValueFactory<>("merchName"));

        TableColumn<TransactionFull, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("merchPrice"));

        TableColumn<TransactionFull, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<TransactionFull, String> dateCol = new TableColumn<>("Transaction Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        transactionTable.getColumns().addAll(idCol, userCol, merchCol, priceCol, qtyCol, dateCol);

        loadTransactions();

        vbox.getChildren().addAll(welcomeLabel, transLabel, transactionTable);
        return vbox;
    }

    private void loadTransactions() {
        transactionTable.getItems().clear();
        try {
            Connection con = DBConnect.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT t.TransactionID, u.Username, m.MerchName, m.MerchPrice, td.Quantity, t.TransactionDate " +
                            "FROM transactionheader t " +
                            "JOIN msuser u ON t.UserID = u.UserID " +
                            "JOIN transactiondetail td ON t.TransactionID = td.TransactionID " +
                            "JOIN msmerch m ON td.MerchID = m.MerchID " +
                            "ORDER BY t.TransactionDate DESC");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactionTable.getItems().add(new TransactionFull(
                        rs.getInt("TransactionID"),
                        rs.getString("Username"),
                        rs.getString("MerchName"),
                        rs.getInt("MerchPrice"),
                        rs.getInt("Quantity"),
                        rs.getString("TransactionDate")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
