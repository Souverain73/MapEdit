package model.parameter;

import org.w3c.dom.Node;

/**
 * Created by Maksim on 04.06.2017.
 */
public class DoubleParam extends CommonParam<Double> {
    @Override
    public IParam<Double> fromXML(Node node) {
        name = node.getNodeName();
        value = Double.parseDouble(node.getTextContent());
        return this;
    }
}
