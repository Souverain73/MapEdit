package instruments;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import model.Lantern;
import model.Link;
import model.MapObject;
import model.Pillar;


/**
 * Created by Souverain73 on 31.05.2017.
 */
public class PlacePillarTool extends ToolAdapter {
    private Pane optionsPane;
    private MapObject lastPillar;
    private boolean placeLine;
    private Link.LineType lineType = Link.LineType.AIR;
    private MapObject dragTarget = null;
    private boolean withLantern = true;

    @Override
    protected void click(double x, double y, MouseButton button) {
        if (button == MouseButton.PRIMARY){
            Pillar pillar;
            if (withLantern){
                pillar = new Lantern();
            }else{
                pillar = new Pillar();
            }
            pillar.place(vc.vtix(x), vc.vtiy(y));
            map.addObject(pillar);
            if (placeLine && lastPillar != null){
                map.addObject(new Link(lastPillar, pillar, lineType));
            }
            lastPillar = pillar;
        }else if (button == MouseButton.SECONDARY){
            MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y));
            if (target!=null)
                map.removeObject(target);
                for (Link l : map.getLinksById(target.getID())){
                    map.removeObject(l);
                }
        }
    }

    @Override
    public Node createOptionsPanel() {
        optionsPane = new Pane();

        CheckBox withLanternCheck = new CheckBox("Столб с фонарем");
        withLanternCheck.setSelected(true);
        withLanternCheck.selectedProperty().addListener((observable, oldValue, newValue) -> withLantern = newValue);


        CheckBox placeLineCheck = new CheckBox("Проставлять линии");
        placeLineCheck.selectedProperty().addListener((observable, oldValue, newValue) -> placeLine = newValue);
        placeLineCheck.setLayoutY(25);

        ComboBox<Link.LineType> lineTypeChk = new ComboBox<>();
        lineTypeChk.setLayoutY(50);
        lineTypeChk.getItems().addAll(Link.LineType.values());
        lineTypeChk.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> lineType = newValue);
        lineTypeChk.getSelectionModel().select(0);

        optionsPane.getChildren().addAll(withLanternCheck, placeLineCheck, lineTypeChk);

        return optionsPane;
    }

    @Override
    protected void drag(double oldx, double oldy, double newx, double newy, MouseButton button) {
        super.drag(oldx, oldy, newx, newy, button);
        if (button != MouseButton.PRIMARY) return;

        if (dragTarget == null) {
            dragTarget = map.getObjectByPoint(vc.vtix(oldx), vc.vtiy(oldy));
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
