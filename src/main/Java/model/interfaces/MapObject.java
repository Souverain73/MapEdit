package model.interfaces;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import model.ObjectClass;
import window.ViewContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Souverain73 on 26.05.2017.
 */
public interface MapObject extends XMLSerializable, PDFSerializable {
    void draw(GraphicsContext gc, ViewContext vc);
    Point2D getPos();
    void setPos(Point2D pos);
    int getID();
    boolean isPointIn(double x, double y, double r);
    void addTemplate(MapObject template);
    List<MapObject> getTemplates();
    boolean hasParam(String name);
    String getParam(String name);
    void setParam(String name, String value);
    Map<String, String> getRawParams();
    ObjectClass getObjectClass();
}
