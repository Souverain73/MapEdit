package model;

import com.itextpdf.text.pdf.PdfWriter;
import helpers.ID;
import helpers.Params;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import model.interfaces.MapObject;
import org.w3c.dom.Node;
import window.ViewContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Souverain73 on 09.06.2017.
 */
public class Template implements MapObject {
    private MapObject base;
    private int id;
    private ObjectClass oc;

    public Template(MapObject mo){
        base = mo;
        id = ID.getNextTemplate();
        oc = mo.getObjectClass();
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Template",
                XML.tag("ID", String.valueOf(id)),
                XML.tag("BaseClass", oc.toString()),
                Params.paramsToXmlString(base.getRawParams()));
    }



    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        return;
    }

    @Override
    public Point2D getPos() {
        return new Point2D(0,0);
    }

    @Override
    public void setPos(Point2D pos) {}

    @Override
    public int getID() {
        return id;
    }

    @Override
    public boolean isPointIn(double x, double y, double r) {
        return false;
    }

    @Override
    public String getParam(String name) {
        return base.getParam(name);
    }

    @Override
    public void setParam(String name, String value) {
        base.setParam(name, value);
    }

    @Override
    public Map<String, String> getRawParams() {
        return base.getRawParams();
    }

    @Override
    public String toString() {
        return base.getRawParams().values().stream().map(o->(String)o).reduce((o, o2) -> o = o + o2 + ":").orElse("");
    }

    protected void addParamFromXML(Node node){
        setParam(node.getNodeName(), node.getTextContent());
    }

    public static Template fromXML(Node root){
        Template result = new Template(new Pillar());
        XML.iterateChilds(root, (name, el) -> {
            switch (name) {
                case "id":
                    result.id = Integer.parseInt(el.getTextContent());
                    ID.useTemplate(result.id);
                    break;
                case "baseclass":
                    result.oc = ObjectClass.valueOf(el.getTextContent());
                    break;
                default:
                    result.addParamFromXML(el);
            }
        });
        return result;
    }

    @Override
    public ObjectClass getObjectClass() {
        return oc;
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
        return base.hasParam(name);
    }

    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof Template)) return false;
        Template other = (Template) obj;

        if (this.oc != other.oc) return false;

        if (!this.getRawParams().equals(other.getRawParams())) return false;

        return true;
    }

    @Override
    public void SerializeToPDF(PdfWriter writer, ViewContext vc) {}
}
