package instruments;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import model.*;
import model.interfaces.MapObject;
import operations.ObjectsManipulation;

import java.util.List;

import static com.itextpdf.text.pdf.PdfName.op;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class PlaceLineTool extends ToolAdapter{
    private MapObject selected;
    private Pane optionsPane;
    private Link.LineType type = Link.LineType.AIR;
    private boolean cntLine = false;
    private Item drawPoint = new Point();

    private class Op extends ObjectsManipulation {
        MapObject newLast;
        MapObject currentLast;
        public Op(Manipulation type, Map map, MapObject newLast, MapObject ... objects) {
            super(type, map, objects);
            this.newLast = newLast;
            currentLast = selected;
        }

        @Override
        public void apply() {
            super.apply();
            selected = newLast;
        }

        @Override
        public void undo() {
            super.undo();
            if (this.type == Manipulation.CREATE)
            selected = null;
        }
    }

    @Override
    protected void click(double x, double y, MouseButton button) {
        if (button == MouseButton.PRIMARY) {

            MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius(), Item.class);

            if (target == null) return;

            if (selected == null) {
                selected = target;
                return;
            }

            if (selected == target){
                return;
            }

            Link link = new Link(selected, target, type);

            if (cntLine){
                selected = target;
            }else {
                selected = null;
            }
            opMan.apply(new Op(ObjectsManipulation.Manipulation.CREATE, map, selected, link));

        }else if(button == MouseButton.SECONDARY){
            MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius(), Link.class);
            if (target!=null && target instanceof Link)
                opMan.apply(new Op(ObjectsManipulation.Manipulation.REMOVE, map, selected, target));
            else
                selected = null;
        }
    }

    @Override
    public Node createOptionsPanel() {
        optionsPane = new Pane();
        ComboBox<Link.LineType> typeSelector = new ComboBox<>();
        typeSelector.getItems().addAll(Link.LineType.values());
        typeSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> type = newValue);
        typeSelector.getSelectionModel().select(0);
        CheckBox cbContinue = new CheckBox("Продолжать линию");
        cbContinue.selectedProperty().addListener((observable, oldValue, newValue) ->{
            cntLine = newValue;
            if (cntLine == false) selected = null;
        });
        cbContinue.setLayoutY(30);
        optionsPane.getChildren().addAll(typeSelector, cbContinue);
        return optionsPane;
    }

    @Override
    public void draw(double x, double y) {
        super.draw(x, y);
        drawPoint.place(vc.vtix(x), vc.vtiy(y));
        if (selected != null){
            new Link(selected, drawPoint, type).draw(gc, vc);
        }
    }
}
