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
 * Graphic User Interface of Overview
 */
public class OverviewUI {
    DBHandler stockroomDB = new DBHandler();

    public void viewGUI(BorderPane root, Stage stage, TableView table) {

        VBox rVBox = new VBox();

        Button completedOrders = new Button("Orders Ready To Ship");
        completedOrders.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayCompletedOrders(root, stage);
                orderCompleted();
            }
        });
        completedOrders.setPadding(new Insets(10, 10, 10, 10));
        completedOrders.setMinWidth(300);

        Button buildingOrders = new Button("Orders Being Built On The Floor");
        buildingOrders.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayBuildingOrders(root, stage);
                buildingOrders();
            }
        });
        buildingOrders.setPadding(new Insets(10, 10, 10, 10));
        buildingOrders.setMinWidth(300);

        Button missingQuantity = new Button("Missing Parts Holding Up Production");
        missingQuantity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayOutOfStock(root, stage);
                outOfStock();
            }
        });
        missingQuantity.setPadding(new Insets(10, 10, 10, 10));
        missingQuantity.setMinWidth(300);

        Label workOrderTitle = new Label("OVERVIEW");
        workOrderTitle.setScaleX(2);
        workOrderTitle.setScaleY(2);
        workOrderTitle.setAlignment(Pos.CENTER);
        workOrderTitle.setPadding(new Insets(0, 0, 20, 0));
        HBox title = new HBox();
        title.getChildren().add(workOrderTitle);
        title.setMaxWidth(300);
        title.setAlignment(Pos.CENTER);

        rVBox.getChildren().addAll(title, completedOrders, buildingOrders, missingQuantity);
        rVBox.setPadding(new Insets(100));
        rVBox.setAlignment(Pos.CENTER);
        rVBox.setSpacing(10);
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);

    }

    /**
     * display completed orders in Overview
     * @param root the root BorderPane for the program
     * @param stage the active Stage in the program
     */
    public void displayCompletedOrders(BorderPane root, Stage stage) {
        VBox rVBox = new VBox();
        Label woTitle = new Label("ORDERS READY TO SHIP");
        woTitle.setScaleY(2.0);
        woTitle.setScaleX(2.0);
        woTitle.setPadding(new Insets(30));
        rVBox.setAlignment(Pos.CENTER);
        MainMenu mainMenu = new MainMenu();
        ResultSet completedOrders = stockroomDB.query("SELECT wo.order_id, p.product_name, wo.date_completed FROM stockroomdb.WORKORDERS AS wo JOIN stockroomdb.PRODUCTS AS p ON wo.product_id = p.product_id WHERE status = 'COMPLETED';");
        rVBox.getChildren().addAll(woTitle, mainMenu.displayTable(completedOrders));
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);
    }

    /**
     * display building orders in Overview
     * @param root  the root BorderPane for the program
     * @param stage the active Stage in the program
     */
    public void displayBuildingOrders(BorderPane root, Stage stage) {
        VBox rVBox = new VBox();
        Label woTitle = new Label("ORDERS BEING BUILT ON THE FLOOR");
        woTitle.setScaleY(2.0);
        woTitle.setScaleX(2.0);
        woTitle.setPadding(new Insets(30));
        rVBox.setAlignment(Pos.CENTER);
        MainMenu mainMenu = new MainMenu();
        ResultSet buildingOrders = stockroomDB.query("SELECT wo.order_id, p.product_name, wo.date_building FROM stockroomdb.WORKORDERS AS wo JOIN stockroomdb.PRODUCTS AS p ON wo.product_id = p.product_id WHERE status = 'BUILDING';");
        rVBox.getChildren().addAll(woTitle, mainMenu.displayTable(buildingOrders));
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);

    }

    /**
     * display items are missing in the work orders
     * @param root the root BorderPane for the program
     * @param stage the active Stage in the program
     */
    public void displayOutOfStock(BorderPane root, Stage stage) {
        VBox rVBox = new VBox();
        Label woTitle = new Label("MISSING PARTS HOLDING UP PRODUCTION");
        woTitle.setScaleY(2.0);
        woTitle.setScaleX(2.0);
        woTitle.setPadding(new Insets(30));
        rVBox.setAlignment(Pos.CENTER);
        MainMenu mainMenu = new MainMenu();
        ResultSet outOfStock = stockroomDB.query("SELECT oi.order_id, p.parts_id, p.part_description, p.vendor, s.quantity - (oi.amount_needed - oi.amount_filled) FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id JOIN stockroomdb.ORDER_ITEMS AS oi ON s.parts_id = oi.parts_id WHERE s.quantity < (oi.amount_needed - oi.amount_filled);");
        rVBox.getChildren().addAll(woTitle, mainMenu.displayTable(outOfStock));
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);
    }
    public static void orderCompleted() {

        DBHandler testDB = new DBHandler();
        ResultSet table1_order_id = testDB.query("SELECT order_id FROM stockroomdb.WORKORDERS WHERE status = 'COMPLETED';");
        ResultSet table1_product_name = testDB.query("SELECT p.product_name FROM stockroomdb.PRODUCTS AS p JOIN stockroomdb.WORKORDERS AS wo ON p.product_id = wo.product_id WHERE status = 'COMPLETED';");
        ResultSet table1_date_completed = testDB.query("SELECT date_completed FROm stockroomdb.WORKORDERS WHERE status = 'COMPLETED';");
        try {
            table1_order_id.beforeFirst();
            table1_product_name.beforeFirst();
            table1_date_completed.beforeFirst();

            System.out.println("=====================================================================================");
            System.out.printf("||%-10s |%-40s |%-19s||", "Order ID", "              PRODUCT NAME", "       Date Completed       ");
            System.out.println("\n=====================================================================================");
            while (table1_order_id.next() && table1_product_name.next() && table1_date_completed.next()) {

                System.out.printf("|%-11d |%-40s |%20tc|\n", table1_order_id.getInt(1), table1_product_name.getString(1), table1_date_completed.getTimestamp(1));


            }
            System.out.println("=====================================================================================");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Print out the order id, product name, and building date of building orders
     */
    public static void buildingOrders() {

        //ArrayList<Part> buildingOrderList = new ArrayList<Part>();
        DBHandler testDB = new DBHandler();
        // ResultSet update = testDB.query("UPDATE stockroomdb.WORKORDERS SET status = 'COMPLETED', date_completed = NOW() WHERE order_id = 6");
        ResultSet table2_order_id = testDB.query("SELECT order_id FROM stockroomdb.WORKORDERS WHERE status = 'BUILDING';");
        ResultSet table2_product_name = testDB.query("SELECT p.product_name FROM stockroomdb.PRODUCTS AS p JOIN stockroomdb.WORKORDERS AS wo ON p.product_id = wo.product_id WHERE status = 'BUILDING';");
        ResultSet table2_date_building = testDB.query("SELECT date_building FROM stockroomdb.WORKORDERS WHERE status = 'BUILDING';");

        try {
            table2_order_id.beforeFirst();
            table2_product_name.beforeFirst();
            table2_date_building.beforeFirst();

            System.out.println("=====================================================================================");
            System.out.printf("||%-10s |%-40s |%-19s||", "Order ID", "              PRODUCT NAME", "       Date Building       ");
            System.out.println("\n=====================================================================================");

            while (table2_order_id.next() && table2_product_name.next() && table2_date_building.next()) {

                System.out.printf("|%-11d |%-40s |%20tc|\n", table2_order_id.getInt(1), table2_product_name.getString(1), table2_date_building.getTimestamp(1));
            }
            System.out.println("=====================================================================================");

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
