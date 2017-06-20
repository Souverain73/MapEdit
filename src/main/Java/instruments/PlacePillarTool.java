package instruments;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import model.*;
import model.interfaces.MapObject;


/**
 * Created by Souverain73 on 31.05.2017.
 */
public class PlacePillarTool extends ToolAdapter {
    private Pane optionsPane;
    private MapObject lastPillar;
    private MapObject dragTarget = null;

    private Template pillarTemplate;
    private Template lanternTemplate;
    private boolean placeLine;
    private Link.LineType lineType;

    @Override
    protected void click(double x, double y, MouseButton button) {
        if (button == MouseButton.PRIMARY){
            Pillar pillar = new Pillar();
            pillar.setPillarTeplate(pillarTemplate);
            pillar.setLanternTemplate(lanternTemplate);

            pillar.place(vc.vtix(x), vc.vtiy(y));
            map.addObject(pillar);

            if (placeLine && lastPillar != null){
                map.addObject(new Link(lastPillar, pillar, lineType));
            }
            lastPillar = pillar;

        }else if (button == MouseButton.SECONDARY){
            MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius(), Pillar.class);
            if (target!=null && target instanceof Pillar) {
                map.removeObject(target);
                for (Link l : map.getLinksById(target.getID())) {
                    map.removeObject(l);
                }
            }
        }
    }

    @Override
    public Node createOptionsPanel() {
        optionsPane = new Pane();

        Label lbType = new Label("Тип столба:");
        ComboBox<Template> cbType = new ComboBox<>();
        cbType.getItems().addAll(map.getTemplatesByOC(ObjectClass.PILLAR).stream().map(o->(Template)o).toArray(Template[]::new));
        cbType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                pillarTemplate = newValue);
        cbType.getSelectionModel().select(0);
        cbType.setLayoutY(15);

        Label lbLantern = new Label("Тип светильника:");
        lbLantern.setLayoutY(50);
        ComboBox<Template> cbLantern = new ComboBox<>();
        cbLantern.getItems().addAll(map.getTemplatesByOC(ObjectClass.LANTERN).stream().map(o->(Template)o).toArray(Template[]::new));
        cbLantern.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                lanternTemplate = newValue);
        cbLantern.getSelectionModel().select(0);
        cbLantern.setLayoutY(65);

        CheckBox cbCheck = new CheckBox("Проставлять линии:");
        cbCheck.selectedProperty().addListener((observable, oldValue, newValue) -> placeLine=newValue);
        cbCheck.setLayoutY(100);
        ComboBox<Link.LineType> cbLine = new ComboBox<>();
        cbLine.getItems().addAll(Link.LineType.values());
        cbLine.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                lineType = newValue);
        cbLine.getSelectionModel().select(0);
        cbLine.setLayoutY(125);

        optionsPane.getChildren().addAll(lbType, cbType, lbLantern, cbLantern, cbCheck, cbLine);

        return optionsPane;
    }

    @Override
    protected void drag(double oldx, double oldy, double newx, double newy, MouseButton button) {
        super.drag(oldx, oldy, newx, newy, button);
        if (button != MouseButton.PRIMARY) return;

        if (dragTarget == null) {
            dragTarget = map.getObjectByPoint(vc.vtix(oldx), vc.vtiy(oldy), vc.getCollideRadius(), Pillar.class);
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
