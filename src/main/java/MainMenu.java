import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

public class MainMenu extends Application{
    public static void main(String[] args){
        launch(args);
    }
    //Initializes javafx so that TableView can be created statically before launch(args) is called
    public static final JFXPanel fxPanel = new JFXPanel();


    public static TableView table = new TableView();
    private VBox vBox = new VBox();
    private Stage stage = new Stage();
    private BorderPane root = new BorderPane();

    private static double imageHeight = 50;
    private static double imageWidth = 50;
    private final boolean USE_WHITE_ICONS = true;

    @Override
    public void start(Stage stage){
        //sets application title
        stage.setTitle("Stockroom Inventory App");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.setSelectionModel();
        //Inventory
        Button inventory = createButton("View Inventory", Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "Stockroom.png").toString());
        inventory.setMinWidth(125);
        inventory.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                displayInventory();
            }
        });

        Button orders = createButton("Work Orders", Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "workorders.png").toString());
        orders.setMinWidth(125);
        orders.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WorkOrder workOrder = new WorkOrder();
                workOrder.viewGUI(root, stage, table);
            }
        });

        Button purchase = createButton("Purchasing", Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "purchasing.png").toString());
        purchase.setMinWidth(125);
        purchase.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PurchasingUI purchasingUI = new PurchasingUI();
                purchasingUI.viewGUI(root, stage, table);
            }
        });

        Button receiving = createButton("Received Parts", Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "receving.png").toString());
        receiving.setMinWidth(125);
        receiving.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ReceivingGUI Receiving = new ReceivingGUI();
                Receiving.viewGUI(root, stage);
            }

        });

        Button shipping = createButton("Shipping", Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "shipping.png").toString());
        shipping.setMinWidth(125);
        shipping.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayShipped();
            }
        });

        Button overview = createButton("Overview", Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "Customer.png").toString());
        overview.setMinWidth(125);
        overview.setText("Overview");
        overview.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                OverviewUI overView = new OverviewUI();
                overView.viewGUI(root, stage, table);
            }
        });

        Button quit = createButton("Quit", Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "exit.png").toString());
        quit.setMinWidth(125);
        quit.setText("Quit");
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });

        BorderPane borderPane = new BorderPane();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().addAll(inventory, orders, purchase, receiving, shipping, overview, quit);
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);
        vBox.setAlignment(Pos.CENTER);

        showSplash(vBox);

        borderPane.getStylesheets().clear();
        borderPane.getStylesheets().add("main.css");
        root = borderPane;

        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
        this.stage = stage;
    }

    private void showSplash(VBox vBox) {
        String path = Paths.get("Icons", (USE_WHITE_ICONS? "white" : "black"), "stockroom-app.png").toString();
        //ImageView logo = new ImageView(new Image(path));
        //logo.setScaleX(0.5);
        //logo.setScaleY(0.5);
//        vBox.setBackground(new Background(new BackgroundImage(logo, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, new BackgroundPosition(Side.LEFT, 0.5, true, Side.TOP, 0.0, true), null)));
        Text creditsText = new Text("Stefano Mauri");
        creditsText.setVisible(false);
        creditsText.setTextAlignment(TextAlignment.RIGHT);
        creditsText.setId("welcometext");
        TextFlow credits = new TextFlow(creditsText);
        //credits.setId("welcomepane");
        Text versionText = new Text();
        versionText.setTextAlignment(TextAlignment.LEFT);
        versionText.setId("welcometext");
        versionText.setText("Version 1.0\nPublish Date: 11/02/17");
        TextFlow version = new TextFlow(versionText);
        version.setId("welcomepane");
        Text welcomeText = new Text("Beahm Designs Stockroom App");
        welcomeText.setTextAlignment(TextAlignment.LEFT);
        welcomeText.setId("welcometextheader");

        GridPane creditsGrid = new GridPane();
//        creditsGrid.setGridLinesVisible(true);
        GridPane.setHalignment(credits, HPos.RIGHT);
        GridPane.setHalignment(version, HPos.LEFT);
        creditsGrid.setAlignment(Pos.CENTER);
        creditsGrid.add(welcomeText, 1, 0, 1, 1);
        //creditsGrid.add(logo, 1, 1, 1, 1);
        creditsGrid.add(credits, 2, 3, 1, 1);
        creditsGrid.add(version, 0, 3, 1, 1);
        creditsGrid.setVgap(0.0);
        vBox.getChildren().add(creditsGrid);
    }

    public Button createButton(String text, String fileName ){
        Button newButton = new Button();
        newButton.setText(text);
        //Image buttonImage = new Image(getClass().getClassLoader().getResourceAsStream(fileName));
        //ImageView scaledImage = new ImageView(buttonImage);
        //scaledImage.setSmooth(true);
        //Adjusting the image size to fit the button
        //scaledImage.setFitWidth(imageWidth);
        //scaledImage.setFitHeight(imageHeight);
        newButton.setContentDisplay(ContentDisplay.TOP);
        //newButton.setGraphic(scaledImage);

        return newButton;
    }

    public Button createButton(String text, ArrayList<String> path){
        //ArrayList path should have
        String first = path.remove(0);
        String filePath = Paths.get(first, (String[])path.toArray()).toString();
        return createButton(text, filePath);
    }

    public void displayInventory(){
        DBHandler testDB = new DBHandler();
        ResultSet result_part_id = testDB.query("SELECT p.parts_id, p.part_number, p.part_description, p.vendor, s.quantity " +
                "FROM stockroomdb.PARTS AS p JOIN stockroomdb.STOCKROOM AS s ON p.parts_id = s.parts_id;");
        TextField filter = new TextField();

        vBox = displayTable(result_part_id, filter);

        root.setCenter(vBox);
        stage.getScene().setRoot(root);
    }

    public void displayOrderForm(){

    }

    public void displayPurchaseForm(){

    }


    public void displayShipped(){
        Shipping shipping = new Shipping();
        shipping.viewGUI(root, stage, table, this);
    }


    public void displayOverview() {

    }

    public static TableView getTable() {
        return table;
    }

    public void setMiddle(Node newDisplay){
        root.setCenter(newDisplay);
        stage.getScene().setRoot(root);
    }

    public static VBox displayTable(ResultSet queryResult){
        FilteredList<TableData> sortedList = getTableData(queryResult);
        table.setItems(sortedList);

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10,10,0,10));
        vBox.setVgrow(table, Priority.ALWAYS);
        vBox.getChildren().add(table);

        return vBox;
    }

    public static VBox displayTable(ResultSet queryResult, TextField filter){
        FilteredList<TableData> sortedList = getTableData(queryResult);
        filter.textProperty().addListener((observable, oldValue, newValue) ->{
            sortedList.setPredicate(search ->{
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }
                String lowerCaseSearch = newValue.toLowerCase();

                if(search.contains(lowerCaseSearch)){
                    return true;
                }
                return false;
            });
        });
        table.setItems(sortedList);

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10,10,0,10));
        vBox.setVgrow(table, Priority.ALWAYS);
        vBox.getChildren().addAll(filter, table);

        return vBox;
    }

    public static FilteredList<TableData> getTableData(ResultSet queryResult){
        table.getColumns().clear();
        try{
            ResultSetMetaData dbData = queryResult.getMetaData();
            //Sets up the table columns: their names and data types
            queryResult.beforeFirst();
            System.out.println("Setup columns");
            for(int i = 1; i <= dbData.getColumnCount(); i++){
                final int index = i;
                String colName = dbData.getColumnName(i);
                TableColumn column = new TableColumn(colName);
                int type = dbData.getColumnType(i);
                if(type == Types.INTEGER || type == Types.BIGINT || type == Types.DECIMAL){
                    column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableData, Integer>, ObservableValue<Integer>>() {
                        @Override
                        public ObservableValue<Integer> call(TableColumn.CellDataFeatures<TableData, Integer> param) {
                            return param.getValue().getAt(index);
                        }
                    });
                }
                else if(type == Types.VARCHAR ){
                    column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableData, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<TableData, String> param) {
                            return param.getValue().getAt(index);
                        }
                    });
                }
                else if(type == Types.TIMESTAMP){
                    column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableData, Timestamp>, ObservableValue<Timestamp>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<TableData, Timestamp> param) {
                            return param.getValue().getAt(index);
                        }
                    });
                }

                table.getColumns().add(column);
            }
            //Converts the ResultSet into usable data
            ObservableList<TableData> data = FXCollections.observableArrayList();
            queryResult.beforeFirst();
            System.out.println("Populate data");
            while(queryResult.next()){
                //Do a per row data grab and add each value based on the type value
                TableData tableData = new TableData();
                for(int i = 1; i <= dbData.getColumnCount(); i++){
                    int type = dbData.getColumnType(i);
                    if(type == Types.INTEGER || type == Types.BIGINT ){
                        tableData.add(queryResult.getInt(i));
                    }
                    else if(type == Types.VARCHAR){
                        tableData.add(queryResult.getString(i));
                    }
                    else if(type == Types.TIMESTAMP){
                        tableData.add(queryResult.getString(i));
                    }
                    else if(type == Types.DECIMAL){
                        System.out.println("decimal");
                        System.out.println(queryResult.getBigDecimal(i));
                        tableData.add(queryResult.getBigDecimal(i).intValue());
                    }
                    else {
                        System.out.println("does not find type " + type);
                    }
                }
                data.add(tableData);
            }
            //wrap ObservableList data with a filteredlist
            FilteredList<TableData> sortedList = new FilteredList<TableData>(data, p->true);
            return sortedList;
        }
        catch (java.sql.SQLException e){
            System.out.println(e);
        }
        return null;
    }

}

