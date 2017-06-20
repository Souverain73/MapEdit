package instruments;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import model.interfaces.MapObject;
import model.Station;

/**
 * Created by Souverain73 on 19.06.2017.
 */
public class StationTool extends ToolAdapter {
    private Pane optionsPane;
    private Station.StationType type = Station.StationType.INNER;
    MapObject dragTarget = null;

    @Override
    protected void click(double x, double y, MouseButton button) {
        if (button == MouseButton.PRIMARY){
            Station station = new Station(type);

            station.place(vc.vtix(x), vc.vtiy(y));
            map.addObject(station);
        }else if (button == MouseButton.SECONDARY){
            MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius(), Station.class);
            if (target!=null) {
                map.removeObject(target);
            }
        }
    }

    @Override
    public Node createOptionsPanel() {
        optionsPane = new Pane();
        ComboBox<Station.StationType> typeSelector = new ComboBox<>();
        typeSelector.getItems().addAll(Station.StationType.values());
        typeSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> type = newValue);
        typeSelector.getSelectionModel().select(0);
        optionsPane.getChildren().add(typeSelector);
        return optionsPane;
    }

    @Override
    protected void drag(double oldx, double oldy, double newx, double newy, MouseButton button) {
        super.drag(oldx, oldy, newx, newy, button);
        if (button != MouseButton.PRIMARY) return;

        if (dragTarget == null) {
            dragTarget = map.getObjectByPoint(vc.vtix(oldx), vc.vtiy(oldy), vc.getCollideRadius(), Station.class);
            if (dragTarget == null) return;
        }

        Point2D oldPos = dragTarget.getPos();
        dragTarget.setPos(oldPos.add((newx - oldx)/vc.getScalex(), (newy - oldy)/vc.getScaley()));
    }

    @Override
    protected void move(double oldx, double oldy, double newx, double newy) {
        super.move(oldx, oldy, newx, newy);
        dragTarget = null;
    }
}
