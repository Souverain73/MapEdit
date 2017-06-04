package model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import window.ViewContext;

/**
 * Created by Souverain73 on 26.05.2017.
 */
public interface MapObject extends XMLSerializable {
    void draw(GraphicsContext gc, ViewContext vc);
    Point2D getPos();
    void setPos(Point2D pos);
    int getID();
    boolean isPointIn(double x, double y);
}
