package model.parameter;

import org.w3c.dom.Node;

/**
 * Created by Maksim on 04.06.2017.
 */
public class IntParam extends CommonParam<Integer> {
    @Override
    public IParam<Integer> fromXML(Node node) {
        name = node.getNodeName();
        value = Integer.parseInt(node.getTextContent());
        return this;
    }
}
