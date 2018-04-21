import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

/**
 * Configure Receiving Class from command line to GUI.
 */
public class ReceivingGUI {
    static DBHandler stockroomDB = new DBHandler();
    Scanner reader = new Scanner(System.in);
    private int partQuantityReceived;
    private BorderPane boardRoot;
    private Stage boardStage;

    /**
     * Create the title of recording receiving function with the start button
     * @param root the root of the scene
     * @param stage the active Stage
     */
    public void viewGUI(BorderPane root, Stage stage) {
        boardRoot = root;
        boardStage = stage;
        VBox rVBox = new VBox();

        System.out.println("title");
        Button start = new Button("Start");
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                getReceivingAmount(root, stage);
                //displayReceivingGUI();
            }
        });
        start.setPadding(new Insets(10, 10, 10, 10));
        start.setMinWidth(300);

        Label receivingTitle = new Label("RECORD A PART DELIVERY");
        MainMenu mainMenu = new MainMenu();
        receivingTitle.setScaleX(2);
        receivingTitle.setScaleY(2);
        receivingTitle.setAlignment(Pos.CENTER);
        receivingTitle.setPadding(new Insets(0, 0, 20, 0));
        HBox title = new HBox();
        title.getChildren().add(receivingTitle);
        title.setMaxWidth(300);
        title.setAlignment(Pos.CENTER);

        rVBox.getChildren().addAll(title, start);//add all the new things in here
        rVBox.setPadding(new Insets(100));
        rVBox.setAlignment(Pos.CENTER);
        rVBox.setSpacing(10);
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);
    }

    /**
     * Ask user if they want to record the receiving with the yes and no button
     *
     * @param root
     * @param stage
     */
    private void displayReceivingGUI(BorderPane root, Stage stage) {
        System.out.println("record a receiving.");
        VBox rVBox = new VBox();

        Label t = new Label();
        t.setText("Have you received a part? ");
        t.setScaleX(2);
        t.setScaleY(2);
        t.setPadding(new Insets(0, 0, 0, 0));
        t.setMinWidth(300);
        t.setAlignment(Pos.CENTER);
        HBox title = new HBox();
        title.getChildren().add(t);
        title.setMaxWidth(300);
        title.setAlignment(Pos.CENTER);


        Button yes = new Button("YES");
        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getReceivingAmount(root, stage);
            }
        });
        yes.setPadding(new Insets(10, 10, 10, 10));
        yes.setMinWidth(300);


        Button no = new Button("NO");
        no.setPadding(new Insets(10, 10, 10, 10));
        no.setMinWidth(300);

        HBox buttons = new HBox();
        rVBox.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(yes, no);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        rVBox.getChildren().addAll(title, buttons);
        rVBox.setSpacing(20);
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);
    }

    /**
     * Ask user to enter parts id and the quantity they received into the text field created in GUI with submit button
     * @param root the root of the scene
     * @param stage the active Stage
     */
    public void getReceivingAmount(BorderPane root, Stage stage) {
        System.out.println("Entering received parts");
        VBox rVBox = new VBox();

        Label label1 = new Label("Enter Part ID: ");
        TextField pid = new TextField();
        HBox hb1 = new HBox();
        hb1.getChildren().addAll(label1, pid);
        hb1.setSpacing(10);
        hb1.setAlignment(Pos.CENTER);

        Label label2 = new Label("Enter Quantity: ");
        TextField qtt = new TextField();
        HBox hb2 = new HBox();
        hb2.getChildren().addAll(label2, qtt);
        hb2.setSpacing(10);
        hb2.setAlignment(Pos.CENTER);
        partQuantityReceived = 0;
        Button submit = new Button("SUBMIT");
        submit.setAlignment(Pos.CENTER);
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String partIDNumber = pid.getText();
                partQuantityReceived = Integer.parseInt(qtt.getText());
                System.out.println("part id from user: " + partIDNumber);
                System.out.println("quantity from user: " + partQuantityReceived);

                ResultSet orderID = getOrders(partIDNumber);
                ResultSet productName = stockroomDB.query("SELECT p.product_name FROM PRODUCTS AS p JOIN ORDER_ITEMS as oi ON p.product_id = oi.product_id WHERE " + partIDNumber + " = parts_id AND amount_needed > amount_filled;");
                ResultSet quantityNeeded = stockroomDB.query("SELECT (amount_needed - amount_filled) AS amount FROM ORDER_ITEMS WHERE " + partIDNumber + " = parts_id AND amount_needed > amount_filled;");
                VBox all = new VBox();
                all.setPadding(new Insets(10, 50, 50, 150));
                Label qttReceived = new Label(getFillPageHeader(partQuantityReceived, partIDNumber));
                qttReceived.setAlignment(Pos.CENTER);
                qttReceived.setScaleX(2);
                qttReceived.setScaleY(2);
                HBox labelRow = new HBox();
                labelRow.getChildren().add(qttReceived);
                labelRow.setAlignment(Pos.CENTER);
                labelRow.setPadding(new Insets(30, 30, 60, 30));
                all.getChildren().add(labelRow);
                try {
                    orderID.beforeFirst();
                    productName.beforeFirst();
                    quantityNeeded.beforeFirst();
                    Label orderIdLabel = new Label("Order ID");
                    orderIdLabel.setMinWidth(200);
                    Label productNameLabel = new Label("PRODUCT NAME");
                    productNameLabel.setMinWidth(200);
                    Label amountLabel = new Label("Amount Needed");
                    amountLabel.setMinWidth(200);
                    HBox header = new HBox();
                    header.getChildren().addAll(orderIdLabel, productNameLabel, amountLabel);

                    all.getChildren().add(header);
                    while (orderID.next()) {
                        productName.next();
                        quantityNeeded.next();
                        System.out.println("=============================================================================");
                        System.out.printf("||%-10s |%-40s |%19s||", "Order ID", "              PRODUCT NAME", "Amount Needed ");
                        System.out.println("\n=============================================================================");
                        int uid = orderID.getInt(1);
                        int orderId = orderID.getInt(2);
                        int quantityNeededInt = quantityNeeded.getInt(1);

                        System.out.printf("|%11d |%-40s |%20d|\n", orderId, productName.getString(1), quantityNeededInt);
                        Button fill = new Button("Fill");
                        fill.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try {
                                    int partLeft = fillKit(partQuantityReceived, quantityNeededInt, uid);
                                    qttReceived.setText(getFillPageHeader(partLeft, partIDNumber));
                                    partQuantityReceived = partLeft;
                                    fill.setDisable(true);
                                    if (partQuantityReceived <= 0) {
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        DialogPane dialogPane = alert.getDialogPane();
                                        dialogPane.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
                                        dialogPane.getStyleClass().add("myDialog");

                                        alert.setContentText("The number of parts left is 0.");
                                        ButtonType receiveMore = new ButtonType("Receiving another part");
                                        alert.getButtonTypes().setAll(receiveMore);
                                        Optional<ButtonType> result = alert.showAndWait();
                                        viewGUI(boardRoot, boardStage);
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        all.getChildren().add(generateVBox(Integer.toString(orderId), productName.getString(1), Integer.toString(quantityNeededInt), fill));
                        System.out.println("-----------------------------------------------------------------------------");
                    }
                    // add leftovers to STOCKROOM
                    if (partQuantityReceived > 0) {
                        stockroomDB.adjustPartQuantity(Integer.parseInt(partIDNumber), partQuantityReceived);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Button addRemaining = new Button("Finish and add remaining parts to stockroom");
                addRemaining.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        addLeftOverToStockroom(Integer.parseInt(partIDNumber), partQuantityReceived);
                        partQuantityReceived = 0;
                        viewGUI(boardRoot, boardStage);
                    }
                });
                all.getChildren().add(addRemaining);
                root.setCenter(all);
                stage.getScene().setRoot(root);
            }
        });
        rVBox.setAlignment(Pos.CENTER);
        rVBox.setPadding(new Insets(100));
        rVBox.setSpacing(20);
        rVBox.getChildren().addAll(hb1, hb2, submit);
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);
    }


    /**
     * Display the table to show how many parts needed to fill into the kit after submitted the data that user entered.
     * The table will include the order ID, product name the quantity needed to fill.
     * @param root the root of the scene
     * @param stage the active Stage
     */
    private static void submitGUI(BorderPane root, Stage stage) {
        System.out.println("In the submitGui");
        VBox rVBox = new VBox();
        rVBox.setAlignment(Pos.CENTER);
        Label antTitle = new Label("VIEW AMOUNT NEEDED");
        MainMenu mainMenu = new MainMenu();
        ResultSet amountNeeded = stockroomDB.query("SELECT ant.order_id, p.product_name, ant.quantity, ant.status FROM stockroomdb.ORDER_ITEMS AS ant JOIN stockroomdb.PRODUCTS AS p ON ant.product_id = p.product_id;");
        rVBox.getChildren().addAll(antTitle, mainMenu.displayTable(amountNeeded));
        root.setCenter(rVBox);
        stage.getScene().setRoot(root);
    }

    /**
     * Get the order and unique based on partId from database
     * @param partId the partId being filled
     * @return the ResultSet from mysql
     */
    private static ResultSet getOrders(String partId) {
        ArrayList<String> conditions = new ArrayList<>();
        conditions.add(Integer.parseInt(partId) + " = " + "parts_id");
        conditions.add("amount_needed > amount_filled");
        return stockroomDB.select("stockroomdb.ORDER_ITEMS", "id, order_id", conditions);
    }

    /**
     * Set up the table title to string
     * @param partQuantityReceived the quantity of parts being received
     * @param partIDNumber the partId in the database
     * @return Generate a formatted String
     */
    private String getFillPageHeader(int partQuantityReceived, String partIDNumber) {
        return partQuantityReceived + " part " + partIDNumber + " to be filled";
    }

    /**
     * Fill the items into kits whenever items have enough or not
     * @param partQuantityReceived the quantity that is being received
     * @param quantityNeededInt the quantity needed
     * @param uid the kit to fill
     * @return the number of parts left
     * @throws SQLException
     */
    private int fillKit(int partQuantityReceived, int quantityNeededInt, int uid) throws SQLException {
        int amountLeftover = partQuantityReceived - quantityNeededInt;
        int quantifyLeft;
        if (amountLeftover <= 0) {
            // receiving less than or equal amount of parts needed in current kit
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("amount_filled", quantityNeededInt + partQuantityReceived);
            ArrayList<Object[]> conds = new ArrayList<>();
            Object[] cond = {"id", "=", uid};
            conds.add(cond);
            stockroomDB.update("stockroomdb.ORDER_ITEMS", updates, conds);
            quantifyLeft = 0;
        } else {
            // receiving more parts than needed in current kit, get total quantity needed and set to quantity filled
            ResultSet quantityNeededTotal = stockroomDB.query("SELECT amount_needed AS amount FROM ORDER_ITEMS WHERE id = " + uid);
            quantityNeededTotal.next();
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("amount_filled", quantityNeededTotal.getInt(1));
            ArrayList<Object[]> conds = new ArrayList<>();
            Object[] cond = {"id", "=", uid};
            conds.add(cond);
            stockroomDB.update("stockroomdb.ORDER_ITEMS", updates, conds);
            // remove quantity stored in kit from partQuantityReceived
            quantifyLeft = partQuantityReceived - quantityNeededInt;
        }
        return quantifyLeft;
    }

    /**
     * Create the fill button to allow user to fill the parts to the kit.
     * @param orderId the Id of the kit
     * @param productName the name of the product
     * @param quantityNeeded the quantity of the part needed in the order
     * @param fill the button to fill the order
     * @return an HBox displaying the kit's information and a button to fill the kit
     */
    private HBox generateVBox(String orderId, String productName, String quantityNeeded, Button fill) {
        Label orderIdLabel = new Label(orderId);
        orderIdLabel.setMinWidth(200);
        Label productNameLabel = new Label(productName);
        productNameLabel.setMinWidth(200);
        Label amountLabel = new Label(quantityNeeded);
        amountLabel.setMinWidth(200);
        HBox ret = new HBox();
        ret.getChildren().addAll(orderIdLabel, productNameLabel, amountLabel, fill);
        return ret;
    }

    /**
     * Add leftover received items to the stockroom data base.
     * @param partId the part being filled
     * @param left the amount leftover after filling kits
     */
    private void addLeftOverToStockroom(int partId, int left) {
        if (partQuantityReceived > 0) {
            stockroomDB.adjustPartQuantity(partId, left);
        }
    }
}