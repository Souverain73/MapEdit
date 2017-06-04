package model.parameter;

import org.w3c.dom.Node;

/**
 * Created by Maksim on 04.06.2017.
 */
public class StringParam extends CommonParam<String>{
    @Override
    public IParam<String> fromXML(Node node) {
        name = node.getNodeName();
        value = node.getTextContent();
        return this;
    }
}
