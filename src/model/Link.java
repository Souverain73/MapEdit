package model;

import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.w3c.dom.Node;
import window.ViewContext;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class Link implements MapObject {
    public enum LineType{
        AIR("Воздушный кабель", Color.RED, 5.0),
        GROUND("Подземный кабель", Color.RED, 0.0),
        ISOLATED("Изолированный и СИП", Color.GREEN, 5.0),
        AIR_NOT_ISOLATED("Воздушный (голый)", Color.CYAN, 0.0),
        STRETCH("Растяжка", Color.BLACK, 0.0);

        private String name;
        private Color color;
        private Double dashes;

        LineType(String name, Color color, Double dashes) {
            this.name = name;
            this.color = color;
            this.dashes = dashes;
        }

        @Override
        public String toString() {
            return name;
        }

        public Color getColor() {
            return color;
        }

        public Double getDashes() {
            return dashes;
        }
    }

    private final MapObject from;
    private final MapObject to;
    private final LineType type;

    public Link(MapObject from, MapObject to, LineType type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public static Link fromXML(Node root, Map map){
        MapObject from = null;
        MapObject to = null;
        LineType lt = LineType.AIR;
        for (Node el : new XML.IterableNodeList(root.getChildNodes())) {
            switch (el.getNodeName().toLowerCase()) {
                case "from":
                    from = map.getObjectById(Integer.parseInt(el.getTextContent()));
                    break;
                case "to":
                    to = map.getObjectById(Integer.parseInt(el.getTextContent()));
                    break;
                case "type":
                    lt = LineType.valueOf(el.getTextContent());
                    break;
            }
        }
        return new Link(from, to, lt);
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Link",
                XML.tag("from", String.valueOf(from.getID())),
                XML.tag("to", String.valueOf(to.getID())),
                XML.tag("type", type.name()));
    }

    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        Point2D f = from.getPos();
        Point2D t = to.getPos();
        gc.setStroke(type.getColor());
        gc.setLineDashes(type.getDashes());
        gc.strokeLine(vc.absX(f.getX()),
                vc.absY(f.getY()),
                vc.absX(t.getX()),
                vc.absY(t.getY()));
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(0);
    }

    @Override
    public Point2D getPos() {
        return new Point2D (from.getPos().getX(), from.getPos().getY());
    }

    @Override
    public void setPos(Point2D pos) {

    }

    @Override
    public int getID() {
        return -1;
    }

    @Override
    public boolean isPointIn(double x, double y) {
        return false;
    }

    public boolean isConnected(int id){
        return (from.getID() == id || to.getID() == id);
    }
}
