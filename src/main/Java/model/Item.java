package model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.ID;
import helpers.Params;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import model.interfaces.IPlaceable;
import model.interfaces.MapObject;
import org.w3c.dom.Node;
import window.ViewContext;

import java.io.IOException;
import java.util.*;
import java.util.Map;

/**
 * Created by Souverain73 on 25.05.2017.
 */
public abstract class Item implements MapObject, IPlaceable {
    int id;
    String name;
    double x, y;
    Map<String, String> params = new HashMap<String, String>();
    List<MapObject> parents = new ArrayList<>();

    public Item() {
        this.id = ID.getNext();
    }


    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        double r = 12;
        if (vc.isFixedSize()){
            r = r/vc.getFixedScale() * vc.getScale();
        }
        gc.fillOval(vc.absX(x) - r/2, vc.absY(y) - r/2, r, r);
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
    public boolean isPointIn(double x, double y, double r) {
        return Math.sqrt((x-this.x)*(x-this.x) + (y-this.y)*(y-this.y)) < 6 * r;
    }

    @Override
    public void place(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean hasParam(String name){
        if (params.containsKey(name)) return true;

        if (parents.size() != 0) {
            for (MapObject p : parents) {
                if (p.hasParam(name))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getParam(String name){
        if (params.containsKey(name)) return params.get(name);

        if (parents.size() != 0) {
            for (MapObject p : parents) {
                if (p.hasParam(name)) {
                    return p.getParam(name);
                }
            }
        }

        return null;
    }

    @Override
    public void addTemplate(MapObject template) {
        parents.add(template);
    }

    @Override
    public List<MapObject> getTemplates() {
        return parents;
    }

    @Override
    public void setParam(String name, String value){
        params.put(name, value);
    }

    protected void addParamFromXML(Node node){
        setParam(node.getNodeName(), node.getTextContent());
    }

    protected String parentsToXMLString(){
        StringBuilder sb = new StringBuilder();
        for (MapObject p : parents){
            sb.append(XML.tag("parent", String.valueOf(p.getID())));
        }
        return sb.toString();
    }

    protected String paramsToXMLString(){
        StringBuilder sb = new StringBuilder();
        params.keySet().forEach(p->{
            sb.append(XML.tag(p, params.get(p)));
        });
        return sb.toString();
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Item",
                XML.tag("id", String.valueOf(id)),
                XML.tag("name", name),
                XML.tag("coordinates", XML.coords(x, -y)),
                parentsToXMLString(),
                Params.paramsToXmlString(params));
    }

    @Override
    public Map<String, String> getRawParams() {
        return params;
    }

    @Override
    public void SerializeToPDF(PdfWriter writer, ViewContext vc) {
        PdfContentByte content = writer.getDirectContent();
        content.saveState();
        content.circle(vc.vtix(x), vc.vtiy(y), 10);
        content.closePathStroke();
        content.restoreState();
    }

    @Override
    public ObjectClass getObjectClass() {
        return ObjectClass.PILLAR;
    }
}
