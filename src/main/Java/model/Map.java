package model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.ID;
import helpers.XML;
import javafx.scene.canvas.GraphicsContext;
import model.interfaces.MapObject;
import model.interfaces.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import window.ViewContext;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Souverain73 on 25.05.2017.
 */
public class Map implements XMLSerializable {
    double geox = 500, geoy = 500;
    double width, height;

    List<MapObject> objects = new ArrayList<>();

    public Map(File file) throws IOException {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            Node root = doc.getDocumentElement();
            NodeList elements = root.getChildNodes();
            for (Node element : new XML.IterableNodeList(elements)){
                switch (element.getNodeName().toLowerCase()){
                    case "groundoverlay":
                        GroundOverlay go = GroundOverlay.fromXML(element, file);
                        addObject(go);
                        geox = Math.min(go.getWest(), geox);
                        geoy = Math.min(go.getNorth(), geoy);
                        width = go.overlay.getWidth();
                        height = go.overlay.getHeight();
                        break;
                    case "point": addObject(Point.fromXML(element)); break;
                    case "pillar": addObject(Pillar.fromXML(element, this)); break;
                    case "lantern": addObject(Lantern.fromXML(element)); break;
                    case "link": addObject(Link.fromXML(element, this)); break;
                    case "template": addObject(Template.fromXML(element)); break;
                    case "station": addObject(Station.fromXML(element)); break;
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    public void setGeoCoords(double x, double y){
        this.geox = x;
        this.geoy = y;
    }

    public List<Link> getLinksById(int id){
        return objects.stream().filter(obj->obj instanceof Link)
                .map(mapObject -> (Link)mapObject).filter(link -> link.isConnected(id))
                .collect(Collectors.toList());
    }

    public void draw(GraphicsContext context, ViewContext view){
        objects.stream().filter(o->o.getID()>=-1).forEach(o->o.draw(context, view));
    }

    public void addObject(MapObject object){
        objects.add(object);
    }

    public void removeObject(MapObject object) {
        objects.remove(object);
    }

    public double getGeox() {
        return geox;
    }

    public double getGeoy() {
        return geoy;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String SerializeToXML() {
        StringBuilder sb = new StringBuilder();
        objects.forEach(mo -> sb.append(mo.SerializeToXML()));
        return sb.toString();
    }

    public void SerializeToPDF(com.itextpdf.text.Document doc, PdfWriter writer) throws IOException, DocumentException {
        ViewContext vc = new ViewContext();
        for (MapObject mo : objects){
            if (mo instanceof GroundOverlay) {
                GroundOverlay ground = (GroundOverlay) mo;
                com.itextpdf.text.Image groundImage = com.itextpdf.text.Image.getInstance(ground.getImagePath());
                doc.add(groundImage);
                vc.setX(ground.getWest());
                vc.setY(ground.getNorth());
                vc.setScalex(ground.getW()/ground.overlay.getWidth());
                vc.setScaley(ground.getH()/ground.overlay.getHeight());
                vc.setPdfSize(ground.overlay.getWidth(), ground.overlay.getHeight());
            } else{
                mo.SerializeToPDF(writer, vc);
            }
        }
    }

    public MapObject getObjectById(int id){
        return objects.stream().filter(obj->obj.getID() == id).findFirst().orElse(null);
    }

    public MapObject getObjectByPoint(double x, double y, double r, Class<? extends MapObject> c){
        return objects.stream()
                .filter(obj->c.isInstance(obj))
                .filter(obj->obj.isPointIn(x, y, r)).findFirst().orElse(null);
    }

    public MapObject getObjectByPoint(double x, double y, double r){
        return getObjectByPoint(x, y, r, MapObject.class);
    }

    public List<MapObject> getTemplates(){
        return objects.stream().filter(o->o.getID()<-1).collect(Collectors.toList());
    }

    public List<MapObject> getTemplatesByOC(ObjectClass oc){
        return objects.stream().filter(o->o.getID()<-1)
                .filter(o->o.getObjectClass() == oc)
                .collect(Collectors.toList());
    }

    public void addTemplate(MapObject object){
        if (objects.contains(object)) return;
        objects.add(0, new Template(object));
    }

    public void removeAllTemplates(){
        ID.resetTemplate();
        objects.removeAll(getTemplates());
    }

    public void renumber(){
        int i=1;
        for (MapObject p : objects.stream().filter(o-> o instanceof Pillar).collect(Collectors.toList())){
            p.setParam(Pillar.pNumber, String.valueOf(i++));
        }

    }
}
