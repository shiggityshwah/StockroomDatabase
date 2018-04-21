import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Pair;

import java.sql.Types;
import java.util.ArrayList;

public class TableData {

    private static final int INT_VAL = Types.INTEGER;
    private static final int STRING_VAL = Types.VARCHAR;

    private ArrayList<ObservableValue> properties;

    public TableData(){
        properties = new ArrayList<>();
    }


    public TableData(Pair<Integer, Object>... colNames){
        properties = new ArrayList<>();
        for(int i=0; i< colNames.length; i++){
            Pair<Integer, Object> column = colNames[i];
            int type = column.getKey();
            if(type == INT_VAL){
                properties.add(new SimpleIntegerProperty((Integer) column.getValue()));
            }
            else if (type == STRING_VAL){
                properties.add(new SimpleStringProperty((String) column.getValue()));
            }
            else{
                System.out.println("Unsupported property type");
            }
        }
    }

    public void add(Integer value){
        properties.add(new SimpleIntegerProperty(value));
    }

    public void add(String value){
        properties.add(new SimpleStringProperty(value));
    }

    public ObservableValue getAt(int i){
        return properties.get(i - 1);
    }

    public boolean contains(String matchString){
        for(int i = 0; i < properties.size(); i++){
            ObservableValue property = properties.get(i);
            String propString = property.toString();
            if(propString.toLowerCase().contains(matchString)){
                return true;
            }
        }
        return false;
    }
}
