package model.parameter;

import helpers.CoordsHelper;
import javafx.geometry.Point2D;
import org.w3c.dom.Node;

/**
 * Created by Maksim on 04.06.2017.
 */
public class PointParam extends CommonParam<Point2D>{
    @Override
    public IParam<Point2D> fromXML(Node node) {
        name = node.getNodeName();
        value = CoordsHelper.parse(node.getTextContent());
        return this;
    }
}
