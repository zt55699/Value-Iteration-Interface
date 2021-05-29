package value_iteration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.*;

import java.util.*;

public class App extends Application  {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("value_iteration.fxml"));
        loader.setController(new ValueIterationDialog());
        Parent root = loader.load();

        primaryStage.setTitle("Value Iteration");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    
}
