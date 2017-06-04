package model;

import helpers.CoordsHelper;
import helpers.ID;
import helpers.XML;
import javafx.geometry.Point2D;
import org.w3c.dom.Node;

/**
 * Created by Souverain73 on 30.05.2017.
 */
public class Point extends Item {
    public static Point fromXML(Node root){
        Point result = new Point();
        for (Node el : new XML.IterableNodeList(root.getChildNodes())) {
            switch (el.getNodeName().toLowerCase()) {
                case "coordinates" :
                    Point2D coords = CoordsHelper.parse(el.getTextContent());
                    result.x = coords.getX();
                    result.y = -coords.getY();
                    break;
                case "id":
                    result.id = Integer.parseInt(el.getTextContent());
                    ID.use(result.id);
                    break;
            }
        }
        return result;
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Point",
                XML.tag("id", String.valueOf(id)),
                XML.tag("name", name),
                XML.tag("coordinates", XML.coords(x, -y)));
    }
}
