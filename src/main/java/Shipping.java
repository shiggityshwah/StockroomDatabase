import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Handles shipping of completed workorders.
 */
public class Shipping {
    public static void main(String[] args) {
        displayShipping();
    }

    /**
     * Prints a command-line menu that allows the user to view and ship completed work orders.
     */
    public static void displayShipping() {
        DBHandler stockroomdb = new DBHandler();

        ArrayList<String> conditions = new ArrayList<>();
        conditions.add("status = 'COMPLETED'");
        ResultSet id_and_quantity = stockroomdb.select("stockroomdb.WORKORDERS", "order_id, quantity", conditions);
        // get product names from PRODUCTS table
        conditions = new ArrayList<>();
        ResultSet product_name = stockroomdb.query("SELECT p.product_name FROM PRODUCTS AS p JOIN WORKORDERS AS oi ON p.product_id = oi.product_id WHERE status = 'COMPLETED'");

        System.out.println("Completed work orders:");

        System.out.println("=============================================================================");
        System.out.printf("||%-10s |%-40s |%19s||", "Order ID", "PRODUCT NAME", "Quantity  ");
        System.out.println("\n=============================================================================");

        try {
            id_and_quantity.beforeFirst();
            product_name.beforeFirst();
            while (id_and_quantity.next()) {
                product_name.next();
                System.out.printf("||%-10d |%-40s |%19d||\n", id_and_quantity.getInt(1), product_name.getString(1), id_and_quantity.getInt(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scanner user_input = new Scanner(System.in);
        System.out.println("Please enter your Order ID to be shipped: ");
        int orderId = user_input.nextInt();

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("status", "SHIPPED");
        updates.put("date_shipped", "NOW()");
        ArrayList<Object[]> searchConditions = new ArrayList<>();
        Object[] cond1 = {"order_id", "=", orderId};
        searchConditions.add(cond1);
        stockroomdb.update("stockroomdb.WORKORDERS", updates, searchConditions);
    }

    /**
     * Gets the number of completed and shipped workorders.
     * @return a ResultSet with workorders that are marked COMPLETED or SHIPPED
     */
    public static ResultSet getCompletedWorkOrders() {
        DBHandler stockroomdb = new DBHandler();

        ArrayList<String> conditions = new ArrayList<>();
        conditions.add("status = 'COMPLETED' OR status = 'SHIPPED'");
        ResultSet id_and_quantity = stockroomdb.select("stockroomdb.WORKORDERS", "*", conditions);
        return id_and_quantity;
    }

    /**
     * Ships a workorder
     * @param orderId the orderId of the workorder to ship
     */
    public void shipOrder(int orderId) {
        DBHandler stockroomdb = new DBHandler();
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("status", "SHIPPED");
        updates.put("date_shipped", "NOW()");
        ArrayList<Object[]> searchConditions = new ArrayList<>();
        Object[] cond1 = {"order_id", "=", orderId};
        searchConditions.add(cond1);
        stockroomdb.update("stockroomdb.WORKORDERS", updates, searchConditions);
    }

    /**
     * viewGUI: display a GUI for the Shipping menu.
     * @param root the root BorderPane for the program
     * @param stage the active Stage in the program
     * @param table the active TableView in the program
     * @param mainMenu the running MainMenu
     */
    public void viewGUI(BorderPane root, Stage stage, TableView table, MainMenu mainMenu) {
        {
            ResultSet rs = getCompletedWorkOrders();
            VBox shipVBox = mainMenu.displayTable(rs);

            TableColumn shipButtons = new TableColumn("Ship");
//            shipButtons.setSortable(false);
            shipButtons.setCellFactory(new Callback<TableColumn<Object, Boolean>, TableCell<Object, Boolean>>() {
                @Override
                public TableCell<Object, Boolean> call(TableColumn<Object, Boolean> param) {
                    return new AddShipCell(mainMenu, table);
                }
            });

            table.getColumns().add(shipButtons);
            table.setMaxWidth(Double.MAX_VALUE);

            root.setCenter(shipVBox);
            stage.getScene().setRoot(root);
        }
    }


    private class AddShipCell extends TableCell<Object, Boolean> {
        final Button shipButton = new Button();
        final StackPane paddedButton = new StackPane();
        final DoubleProperty buttonY = new SimpleDoubleProperty();

        /**
         * Constructor for ShipCell
         * @param mainMenu: the active MainMenu
         * @param tableView: the active TableVie
         */
        AddShipCell(final MainMenu mainMenu, final TableView tableView) {
            paddedButton.setPadding(new Insets(0))  ;
            paddedButton.getChildren().add(shipButton);
            shipButton.setText("Ship");
            shipButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    buttonY.set(event.getScreenY());
                }
            });
            shipButton.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 * Handles the ShipButton action
                 * @param event: the ActionEvent (a click)
                 */
                @Override
                public void handle(ActionEvent event) {
                    TableData data = (TableData) tableView.getItems().get(getTableRow().getIndex());
                    SimpleIntegerProperty orderID = (SimpleIntegerProperty) data.getAt(1);
                    Shipping shipping = new Shipping();
                    shipping.shipOrder(orderID.intValue());
                    mainMenu.displayShipped();
                }
            });
        }

        /**
         * Determines if the row should have a shipButton by checking whether the row has an entry in date_shipped.
         * @param item not used
         * @param empty Whether the cell contains an item.
         */
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                if (getTableRow() != null) {
                    TableData data = (TableData) getTableView().getItems().get(getTableRow().getIndex());
                    SimpleStringProperty shipDate = (SimpleStringProperty) data.getAt(9);
                    if (shipDate.getValue() == null) {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        setGraphic(paddedButton);
                    } else {
                        setGraphic(null);
                    }
                } else {
                    setGraphic(null);
                }
            } else {
                setGraphic(null);
            }
        }

    }
}