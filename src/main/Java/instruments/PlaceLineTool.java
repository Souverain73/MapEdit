package instruments;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import model.Link;
import model.MapObject;
import model.Pillar;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class PlaceLineTool extends ToolAdapter{
    private MapObject selected;
    private Pane optionsPane;
    private Link.LineType type = Link.LineType.AIR;

    @Override
    protected void click(double x, double y, MouseButton button) {
        if (button == MouseButton.PRIMARY) {

            MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius(), Pillar.class);

            if (target == null) return;

            if (selected == null) {
                selected = target;
                return;
            }

            Link link = new Link(selected, target, type);
            map.addObject(link);

            selected = null;
        }else if(button == MouseButton.SECONDARY){
            MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius(), Link.class);
            if (target!=null && target instanceof Link)
                map.removeObject(target);
        }
    }

    @Override
    public Node createOptionsPanel() {
        optionsPane = new Pane();
        ComboBox<Link.LineType> typeSelector = new ComboBox<>();
        typeSelector.getItems().addAll(Link.LineType.values());
        typeSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> type = newValue);
        typeSelector.getSelectionModel().select(0);
        optionsPane.getChildren().add(typeSelector);
        return optionsPane;
    }
}
