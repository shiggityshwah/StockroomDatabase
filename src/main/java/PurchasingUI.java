import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * purchasing user interface shows the parts are out of stock
 * and parts are in low quantity so that user can handle purchasing
 * of these items
 */
public class PurchasingUI {
    DBHandler stockroomDB = new DBHandler();

    public void viewGUI(BorderPane root, Stage stage, TableView table) {

        VBox rVBox = new VBox();

        Button missingQauntity = new Button("Shortages In Kits");
        missingQauntity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                outOfStock(root, stage);
                outOfStock();
            }
        });
        missingQauntity.setPadding(new Insets(10, 10, 10, 10));
        missingQauntity.setMinWidth(300);

        Button lowQuantity = new Button("Low Quantity In Stockroom");
        lowQuantity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lowQuantity(root, stage);
                lowQuantity();
            }
        });
        lowQuantity.setPadding(new Insets(10, 10, 10, 10));
        lowQuantity.setMinWidth(300);

        Label workOrderTitle = new Label("PURCHASING");
        workOrderTitle.setScaleX(2);
        workOrderTitle.setScaleY(2);
        workOrderTitle.setAlignment(Pos.CENTER);
        workOrderTitle.setPadding(new Insets(0, 0, 20, 0));
        HBox title = new HBox();
        title.getChildren().add(workOrderTitle);
        title.setMaxWidth(300);
        title.setAlignment(Pos.CENTER);

        rVBox.getChildren().addAll(title, missingQauntity, lowQuantity);
        rVBox.setPadding(new Insets(100));
        rVBox.setAlignment(Pos.CENTER);
        rVBox.setSpacing(10);
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);

    }

    /**
     * display parts that are out of stock according to the amount filed and needed
     * @param root the root BorderPane for the program
     * @param stage the active Stage in the program
     */
    public void outOfStock(BorderPane root, Stage stage) {
        VBox rVBox = new VBox();
        Label woTitle = new Label("SHORTAGES IN KITS");
        woTitle.setScaleY(2.0);
        woTitle.setScaleX(2.0);
        woTitle.setPadding(new Insets(30));
        rVBox.setAlignment(Pos.CENTER);
        MainMenu mainMenu = new MainMenu();
        ResultSet missingQuantity = stockroomDB.query("SELECT oi.order_id, p.parts_id, p.part_description, p.vendor, s.quantity - (oi.amount_needed - oi.amount_filled) FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id JOIN stockroomdb.ORDER_ITEMS AS oi ON s.parts_id = oi.parts_id WHERE s.quantity < (oi.amount_needed - oi.amount_filled);");
        rVBox.getChildren().addAll(woTitle, mainMenu.displayTable(missingQuantity));
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);
    }

    /**
     * display parts that are in low quantity according to the low quantity settings
     * @param root the root BorderPane for the program
     * @param stage the active Stage in the program
     */
    public void lowQuantity(BorderPane root, Stage stage) {
        VBox rVBox = new VBox();
        Label woTitle = new Label("LOW QUANTITY IN STOCKROOM");
        woTitle.setScaleY(2.0);
        woTitle.setScaleX(2.0);
        woTitle.setPadding(new Insets(30));
        rVBox.setAlignment(Pos.CENTER);
        MainMenu mainMenu = new MainMenu();
        ResultSet lowQuantiy = stockroomDB.query("SELECT p.parts_id, p.part_description, p.vendor, s.quantity, p.low_quantity FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id WHERE s.quantity < p.low_quantity;");
        rVBox.getChildren().addAll(woTitle, mainMenu.displayTable(lowQuantiy));
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);

    }

    /**
     * Print out the parts are out of stock according to the amount filed and needed
     */
    public static void outOfStock() {

        DBHandler testDB = new DBHandler();

        ResultSet table1_parts_id = testDB.query("SELECT DISTINCT p.parts_id FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id JOIN stockroomdb.ORDER_ITEMS AS oi ON s.parts_id = oi.parts_id WHERE s.quantity < (oi.amount_needed - oi.amount_filled);");
        ResultSet table1_part_description = testDB.query("SELECT DISTINCT p.part_description FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id JOIN stockroomdb.ORDER_ITEMS AS oi ON s.parts_id = oi.parts_id WHERE s.quantity < (oi.amount_needed - oi.amount_filled);");
        ResultSet table1_part_vendor = testDB.query("SELECT DISTINCT p.vendor FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id JOIN stockroomdb.ORDER_ITEMS AS oi ON s.parts_id = oi.parts_id WHERE s.quantity < (oi.amount_needed - oi.amount_filled);");
        ResultSet table1_order_missing_quantity = testDB.query("SELECT s.quantity - SUM(oi.amount_needed - oi.amount_filled) FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id JOIN stockroomdb.ORDER_ITEMS AS oi ON s.parts_id = oi.parts_id WHERE s.quantity < (oi.amount_needed - oi.amount_filled);");

        try {
            table1_parts_id.beforeFirst();
            table1_part_description.beforeFirst();
            table1_part_vendor.beforeFirst();
            table1_order_missing_quantity.beforeFirst();

            System.out.println("=====================================================================================================================================================");
            System.out.printf("||%-10s |%-90s |%-20s |%-15s ||", "Part ID", "           Description", "      Vendor ", "  Missing Quantity");
            System.out.println("\n=====================================================================================================================================================");
            while (table1_parts_id.next() && table1_part_description.next() && table1_part_vendor.next() && table1_order_missing_quantity.next()) {

                System.out.printf("||%-10d |%-90s |%-20s |%-15s    ||\n", table1_parts_id.getInt(1), table1_part_description.getString(1), table1_part_vendor.getString(1), table1_order_missing_quantity.getInt(1));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * print out part are in low quantity according to the low quantity settings
     */
    public static void lowQuantity() {

        DBHandler testDB = new DBHandler();
        ResultSet table2_parts_id = testDB.query("SELECT p.parts_id FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id WHERE s.quantity < p.low_quantity;");
        ResultSet table2_part_description = testDB.query("SELECT p.part_description FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id WHERE s.quantity < p.low_quantity;");
        ResultSet table2_part_vendor = testDB.query("SELECT p.vendor FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id WHERE s.quantity < p.low_quantity;");
        ResultSet table2_stockroom_quantity = testDB.query("SELECT s.quantity FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id WHERE s.quantity < p.low_quantity;");
        ResultSet table2_part_low_quantity = testDB.query("SELECT p.low_quantity FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id WHERE s.quantity < p.low_quantity;");

        try {
            table2_parts_id.beforeFirst();
            table2_part_description.beforeFirst();
            table2_part_vendor.beforeFirst();
            table2_stockroom_quantity.beforeFirst();
            table2_part_low_quantity.beforeFirst();

            System.out.println("================================================================================================================================================================");
            System.out.printf("||%-10s |%-90s |%-20s |%-15s |%-15s ||", "Part ID", "           Description", "   Vendor    ", "  Quantity", "Low Quantity");
            System.out.println("\n================================================================================================================================================================");

            while (table2_parts_id.next() && table2_part_description.next() && table2_part_vendor.next() && table2_stockroom_quantity.next() && table2_part_low_quantity.next()) {
                System.out.printf("||%-10d |%-90s |%-20s |%-15s |%-15s ||\n", table2_parts_id.getInt(1), table2_part_description.getString(1), table2_part_vendor.getString(1), table2_stockroom_quantity.getInt(1), table2_part_low_quantity.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}