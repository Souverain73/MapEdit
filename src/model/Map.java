package model;

import helpers.XML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
public class Map implements XMLSerializable{
    double geox = 500, geoy = 500;

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
                        break;
                    case "point": addObject(Point.fromXML(element)); break;
                    case "pillar": addObject(Pillar.fromXML(element)); break;
                    case "lantern": addObject(Lantern.fromXML(element)); break;
                    case "link": addObject(Link.fromXML(element, this)); break;
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

    @Override
    public String SerializeToXML() {
        StringBuilder sb = new StringBuilder();
        objects.forEach(mo -> sb.append(mo.SerializeToXML()));
        return sb.toString();
    }

    public MapObject getObjectById(int id){
        return objects.stream().filter(obj->obj.getID() == id).findFirst().orElse(null);
    }

    public MapObject getObjectByPoint(double x, double y){
        return objects.stream().filter(obj->obj.isPointIn(x, y)).findFirst().orElse(null);
    }
}
