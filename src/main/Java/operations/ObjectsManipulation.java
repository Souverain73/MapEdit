package operations;

import model.Map;
import model.interfaces.MapObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Souverain73 on 21.06.2017.
 */
public class ObjectsManipulation implements Operation {
    public enum Manipulation{
        CREATE,
        REMOVE
    }

    protected Manipulation type;
    protected List<MapObject> objects;
    protected Map map;

    public ObjectsManipulation(Manipulation type, Map map, MapObject ... objects){
        this.type = type;
        this.map = map;
        this.objects = Arrays.asList(objects);
    }

    public ObjectsManipulation(Manipulation type, Map map, List<MapObject> objects){
        this.type = type;
        this.map = map;
        this.objects = objects;
    }

    @Override
    public void apply() {
        switch (type){
            case CREATE: map.addAll(objects); break;
            case REMOVE: map.removeAll(objects); break;
        }
    }

    @Override
    public void undo() {
        switch (type){
            case REMOVE: map.addAll(objects); break;
            case CREATE: map.removeAll(objects); break;
        }
    }
}
