package model.parameter;

import org.w3c.dom.Node;

/**
 * Created by Maksim on 04.06.2017.
 */
public interface IParam<T> {
    String getName();
    T getValue();
    IParam<T> fromXML(Node node);
    String toXML();
}
