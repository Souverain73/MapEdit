package model;

import helpers.ID;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Node;
import window.ViewContext;

/**
 * Created by Souverain73 on 25.05.2017.
 */
public abstract class Item implements MapObject, IPlaceable{
    int id;
    String name;
    double x, y;
    Item parent;

    public Item() {
        this.id = ID.getNext();
        this.name = "SampleItem";
    }


    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        gc.fillOval(vc.absX(x) - 6, vc.absY(y) - 6, 12, 12);
    }

    @Override
    public Point2D getPos() {
        return new Point2D(x, y);
    }

    @Override
    public void setPos(Point2D pos) {
        x = pos.getX();
        y = pos.getY();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public boolean isPointIn(double x, double y) {
        return Math.sqrt((x-this.x)*(x-this.x) + (y-this.y)*(y-this.y)) < 0.0001;
    }

    @Override
    public void place(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Item",
                XML.tag("id", String.valueOf(id)),
                XML.tag("name", name),
                XML.tag("coordinates", XML.coords(x, -y)));
    }
}
