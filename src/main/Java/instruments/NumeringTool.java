package instruments;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import model.Item;
import model.interfaces.MapObject;
import model.Pillar;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Souverain73 on 16.06.2017.
 */
public class NumeringTool extends ToolAdapter {
    int startingNumber = 1;
    MapObject selected = null;
    @Override
    public Node createOptionsPanel() {
        VBox optionsPane = new VBox();
        TextField textField = new TextField(String.valueOf(startingNumber));
        textField.textProperty().addListener((observable, oldValue, newValue) ->{
                String correctValue = newValue;
                if (!newValue.matches("\\d*")) {
                    correctValue = newValue.replaceAll("[^\\d]", "");
                    textField.setText(correctValue);
                }
                startingNumber = Integer.valueOf(correctValue);
            }
        );
        optionsPane.getChildren().addAll(new Label("Начальное значение"), textField);
        return optionsPane;
    }

    @Override
    protected void click(double x, double y, MouseButton button) {
        MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius(), Item.class);

        if (target!=null){
            if (selected == null){
                selected = target;
                return;
            }

            renumber(selected, target, startingNumber);
            selected = null;
        }
    }

    private void renumber(MapObject start, MapObject second, int sn){
        if (!map.getLinksById(start.getID()).stream().filter(l->l.getAnother(start) == second).findAny().isPresent())
            return;

        int i = sn;
        MapObject last = start;
        MapObject current = second;
        MapObject next = null;
        num(start, i++);
        while (true){
            num(current, i++);
            List<MapObject> nextCandidates = getConnectedObjects(current);
            if (nextCandidates.size() == 0) return;
            if (nextCandidates.size() > 2) return;
            if (nextCandidates.size() == 1 && nextCandidates.get(0) == last) return;
            if (nextCandidates.get(0) == last) next = nextCandidates.get(1);
            else next = nextCandidates.get(0);
            last = current;
            current = next;
        }
    }

    private List<MapObject> getConnectedObjects(MapObject target){
        return map.getLinksById(target.getID()).stream().map(l->l.getAnother(target)).collect(Collectors.toList());
    }

    private void num(MapObject obj, int number){
        obj.setParam(Pillar.pNumber, String.valueOf(number));
    }
}
