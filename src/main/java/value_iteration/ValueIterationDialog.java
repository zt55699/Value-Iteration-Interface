package value_iteration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class ValueIterationDialog {

    @FXML
    private TextField n_rows;

    @FXML
    private TextField n_colums;

    @FXML
    private AnchorPane mapPane;

    @FXML
    private TextField pRight;

    @FXML
    private TextField pLeft;

    @FXML
    private TextField pUp;

    @FXML
    private TextField num_iteration;

    @FXML
    private TextField init_policy;

    @FXML
    private TextField rewards;

    @FXML
    private TextField discountRate;

    protected GridPane map;

    protected Composer composer;

    protected ValueIterationDialog vi;

    protected int rows, colums;

    public ValueIterationDialog() {
        map = new GridPane();
        this.vi = this;
    }

    @FXML
    public void initialize() {
        map.setMaxSize(mapPane.getMaxWidth(), mapPane.getMaxHeight());
        mapPane.getChildren().addAll(map);
    }

    @FXML
    void applyNum(ActionEvent event) {
        initMap();
        initComposer();
    }

    public void initMap() {
        rows = (int) Integer.valueOf(n_rows.getText());
        colums = (int) Integer.valueOf(n_colums.getText());

        map.getChildren().clear();
        for (int i = 0; i < rows + 1; i++) {
            for (int j = 0; j < colums + 1; j++) {
                Cell cell = new Cell("");
                cell.setMinWidth(map.getMaxWidth() / (colums + 1));
                cell.setMinHeight(map.getMaxHeight() / (rows + 1));

                if (i == rows && j == 0) {
                    cell.setText(" ");
                } else if (i == rows) {
                    cell.setAlignment(Pos.TOP_CENTER);
                    cell.setText("\n" + Integer.toString(j));
                } else if (j == 0) {
                    cell.setAlignment(Pos.CENTER_RIGHT);
                    cell.setText(Integer.toString(rows - i) + "          ");
                } else {
                    cell.setAlignment(Pos.CENTER);
                    if(i == 0 && j == colums){
                        cell.setText("Click to set + val");
                        cell.setStyle("-fx-background-color : lightgreen; -fx-border-color: black;");
                    }else if(i == 1 && j == colums){
                        cell.setText("Click to set - val");
                        cell.setStyle("-fx-background-color : lightpink; -fx-border-color: black;");
                    }else{
                        cell.setText(rewards.getText());
                        cell.setStyle("-fx-background-color : white; -fx-border-color: black;");
                    }
                    cell.x = j;
                    cell.y = rows - i;
                    cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent me) {
                            //System.out.printf("cell(%d,%d) clicked\n", ((Cell)me.getSource()).x, ((Cell)me.getSource()).y);
                            try{
                                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("cell_update.fxml"));
                                CellUpdateDialog cellController = new CellUpdateDialog((Cell)me.getSource(), vi);
                                loader.setController(cellController);
                                //controller.setLocation(composer.plotData.getHSeqLength() / 2, composer.plotData.getVSeqLength() / 2);
                                //controller.init();
                                Parent node = loader.load();
                    
                                //SubPane.getChildren().setAll(node);
                                Stage popup = new Stage();
                                popup.initStyle(StageStyle.UTILITY);
                                popup.setTitle("Update Cell Value");
                                popup.setScene(new Scene(node));
                                popup.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                map.add(cell, j, i);
            }
        }
    }

    public void initComposer(){
        this.composer = new Composer(rows, colums, (Double) Double.valueOf(rewards.getText()), 
        (Double) Double.valueOf(discountRate.getText()),(Double) Double.valueOf(pUp.getText()), 
        (Double) Double.valueOf(pLeft.getText()), (Double) Double.valueOf(pRight.getText()));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colums + 1; j++) {
                Cell ce = (Cell)map.getChildren().get(i*(colums+1)+j);
                if((j!=0) && !ce.wall){
                    String allDigits = "-?[0-9]+";
                    if(ce.getText().matches(allDigits))
                        composer.utilities[ce.y][ce.x] = (Double) Double.valueOf(ce.getText());
                    else
                        composer.utilities[ce.y][ce.x] = 0.0;
                }
            }
        }
    }

    @FXML
    void runIteration(ActionEvent event) {
        composer.discountRate = (Double) Double.valueOf(discountRate.getText());
        composer.pGood =  (Double) Double.valueOf(pUp.getText());
        Double[][] newUtil = composer.runIteration((int) Integer.valueOf(num_iteration.getText()));
        updateMap(newUtil);
    }

    @FXML
    void runPolicyIteration(ActionEvent event) {
        composer.discountRate = (Double) Double.valueOf(discountRate.getText());
        composer.pGood =  (Double) Double.valueOf(pUp.getText());
        Double[][] newUtil = composer.policyIteration((int) Integer.valueOf(num_iteration.getText()), init_policy.getText().charAt(0));
        updateMap(newUtil);
    }

    public void updateMap(Double[][] utilities) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colums + 1; j++) {
                Cell ce = (Cell)map.getChildren().get(i*(colums+1)+j);
                if((j!=0) && !ce.wall){             
                    ce.setText(new DecimalFormat("#0.00#").format(utilities[ce.y][ce.x]) +"\n" + composer.optPolicy[ce.y][ce.x]);
                }
            }
        }
    }

    
        
}
