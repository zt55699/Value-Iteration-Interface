package value_iteration;

import javafx.scene.control.Label;

public class Cell extends Label{
    public int x, y;
    boolean wall = false;
    //public String val;
    
    public Cell(String s){
        super(s);
        x = -1; 
        y = -1;
    }

    public Cell(String s, int x, int y){
        super(s);
        this.x = x; 
        this.y = y;
    }
}
