package helpers;

import javafx.geometry.Point2D;

/**
 * Created by Souverain73 on 30.05.2017.
 */
public class CoordsHelper {
    public static Point2D parse(String str){
        String[] parts = str.split(",");
        return  new Point2D((Double.parseDouble(parts[0])),
                Double.parseDouble(parts[1]));
    }
}
