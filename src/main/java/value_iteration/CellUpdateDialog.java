package value_iteration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class CellUpdateDialog {

    @FXML
    private AnchorPane cellUpdatePane;

    @FXML
    private TextField newVal;

    @FXML
    private CheckBox wall;
    
    private ValueIterationDialog vi;
    private Cell ce;

    public CellUpdateDialog(Cell ce, ValueIterationDialog vi){
        this.ce = ce;
        this.vi = vi;
    }

    @FXML
    public void initialize() {
        wall.selectedProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o,Object old_Val, 
                     Object new_Val){
                if((boolean)new_Val){
                    newVal.setText("");
                    newVal.setEditable(false);
                }else
                    newVal.setEditable(true);  
            }
          });
    }

    @FXML
    void applyCellUpdate(ActionEvent event) {
        if(wall.isSelected()){
            ce.wall = true;
            ce.setStyle("-fx-background-color : blue; -fx-border-color: black; -fx-color:blue;");
            ce.setTextFill(Color.BLUE);
            ce.setText("0");
            newVal.setEditable(false);
        }else {
            ce.wall = false;
            ce.setStyle("-fx-background-color : white; -fx-border-color: black; -fx-color:black;");
            ce.setTextFill(Color.BLACK);
            ce.setText(newVal.getText());
        }
        vi.initComposer();
        Stage stage = (Stage) (( Node)event.getTarget()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancelCellUpdate(ActionEvent event) {
        Stage stage = (Stage) (( Node)event.getTarget()).getScene().getWindow();
        stage.close();
    }

}
