package model;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.interfaces.MapObject;
import org.w3c.dom.Node;
import window.ViewContext;

import java.util.List;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class Link implements MapObject {
    public enum LineType{
        ISOLATED("Изолированный и СИП", Color.GREEN, 5.0),
        AIR("Воздушный кабель", Color.RED, 5.0),
        GROUND("Подземный кабель", Color.RED, 0.0),
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
    private LineType type;

    public Link(MapObject from, MapObject to, LineType type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public void setType(LineType type) {
        this.type = type;
    }

    public LineType getType() {
        return type;
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

    public void SerializeToPDF(PdfWriter writer, ViewContext vc) {
        PdfContentByte content = writer.getDirectContent();
        Point2D f = from.getPos();
        Point2D t = to.getPos();
        content.saveState();

        if (type.getDashes() > 0)
            content.setLineDash(type.getDashes(), 0);
        else
            content.setLineDash(1,0,0);

        content.setColorStroke(ptc(type.getColor()));
        content.moveTo(vc.pdfx(f.getX()), vc.pdfy(f.getY()));
        content.lineTo(vc.pdfx(t.getX()), vc.pdfy(t.getY()));
        content.stroke();
        content.newPath();

        content.restoreState();
    }

    private BaseColor ptc(Color c){
        return new BaseColor((int) (c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
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
    public boolean isPointIn(double x, double y, double r) {
        Point2D from = this.from.getPos();
        Point2D to = this.to.getPos();
        double tx = Math.max(from.getX(), to.getX());
        double ty = Math.max(from.getY(), to.getY());
        double mx = Math.min(from.getX(), to.getX());
        double my = Math.min(from.getY(), to.getY());
        if (x<mx || y < my || x > tx || y > ty)
            return false;

        double A = from.getY() - to.getY();
        double B = to.getX() - from.getX();

        double l = Math.sqrt(A*A + B*B);

        double C = -A*from.getX() -B*from.getY();

        double dist = Math.abs(A*x + B*y + C)/l;

        return dist < 2*r;
    }

    @Override
    public String getParam(String name) {
        return null;
    }

    @Override
    public void setParam(String name, String value) {
    }

    @Override
    public java.util.Map<String, String> getRawParams() {
        return null;
    }

    public boolean isConnected(int id){
        return (from.getID() == id || to.getID() == id);
    }

    @Override
    public ObjectClass getObjectClass() {
        return ObjectClass.LINK;
    }

    @Override
    public void addTemplate(MapObject template) {

    }

    @Override
    public List<MapObject> getTemplates() {
        return null;
    }

    @Override
    public boolean hasParam(String name) {
        return false;
    }

    public MapObject getAnother(MapObject one){
        if (one == from) return  to;
        if (one == to) return from;
        return null;
    }

    public MapObject getFrom() {
        return from;
    }

    public MapObject getTo() {
        return to;
    }
}
