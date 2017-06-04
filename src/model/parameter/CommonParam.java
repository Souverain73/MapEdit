package model.parameter;

import helpers.XML;
import org.w3c.dom.Node;

/**
 * Created by Maksim on 04.06.2017.
 */
public class CommonParam<T> implements IParam<T>{
    protected String name;
    protected T value;

    public CommonParam() {

    }

    public CommonParam(String name, T value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public IParam<T> fromXML(Node node) {
        return null;
    }

    @Override
    public String toXML() {
        return XML.tag(name, value.toString());
    }
}
