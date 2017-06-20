package instruments;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.*;
import model.interfaces.MapObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Souverain73 on 15.06.2017.
 */
public class EditTool extends ToolAdapter {
    private Pane editPane;
    private Map<String, TextField> paramFields = new HashMap<>();
    private Button saveButton;
    private MapObject target;

    public EditTool(Pane editPane) {
        this.editPane = editPane;
        saveButton = new Button("Сохранить");
        saveButton.setOnAction(
                event -> paramFields.
                        keySet().
                        forEach(p->target.setParam(p, paramFields.get(p).getText())));
    }

    @Override
    protected void click(double x, double y, MouseButton button) {
        super.click(x, y, button);
        MapObject target = map.getObjectByPoint(vc.vtix(x), vc.vtiy(y), vc.getCollideRadius());
        if (target!=null){
            this.target = target;
            showEditPane(target);
        }
    }

    private void showEditPane(MapObject target){
        editPane.getChildren().clear();
        VBox vb = new VBox();
        if (target instanceof Pillar) {
            Pillar pillar = (Pillar) target;
            vb.getChildren().addAll(createPillarEditPane(pillar), createParamsEditTable(target.getRawParams(), "number"), saveButton);
        }else if (target instanceof Link) {
            Link link = (Link) target;
            vb.getChildren().add(createLineEditPane(link));
        }else if (target instanceof Station) {
            Station stat = (Station) target;
            vb.getChildren().addAll(createParamsEditTable(stat.getRawParams(), Pillar.pLabel), saveButton);
        }
        editPane.getChildren().addAll(vb);
    }

    private Node createParamsEditTable(Map<String, String> params, String ... additionalParams){
        paramFields = new HashMap<>();
        GridPane gp = new GridPane();
        int i=0;

        for (String k : params.keySet()){
            gp.add(new Label(k), 0, i);
            TextField tf = new TextField(params.get(k));
            paramFields.put(k, tf);
            gp.add(tf, 1, i);
            i++;
        }

        for (String p: additionalParams){
            if (paramFields.containsKey(p)) continue;
            gp.add(new Label(p), 0, i);
            TextField tf = new TextField();
            paramFields.put(p, tf);
            gp.add(tf, 1, i);
            i++;
        }
        return gp;
    }

    private Node createPillarEditPane(Pillar target){
        Pane optionsPane = new Pane();

        Label lbType = new Label("Тип столба:");
        ComboBox<Template> cbType = new ComboBox<>();
        cbType.getItems().addAll(map.getTemplatesByOC(ObjectClass.PILLAR).stream().map(o->(Template)o).toArray(Template[]::new));
        cbType.getSelectionModel().select((Template) target.getPillarTeplate());
        cbType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                target.setPillarTeplate(newValue));
        cbType.setLayoutY(15);

        Label lbLantern = new Label("Тип светильника:");
        lbLantern.setLayoutY(50);
        ComboBox<Template> cbLantern = new ComboBox<>();
        cbLantern.getItems().addAll(map.getTemplatesByOC(ObjectClass.LANTERN).stream().map(o->(Template)o).toArray(Template[]::new));
        cbLantern.getSelectionModel().select((Template) target.getLanternTemplate());
        cbLantern.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                target.setLanternTemplate(newValue));
        cbLantern.setLayoutY(65);

        optionsPane.getChildren().addAll(lbType, cbType, lbLantern, cbLantern);

        return optionsPane;
    }

    private Node createLineEditPane(Link target){
        Pane optionsPane = new Pane();
        ComboBox<Link.LineType> typeSelector = new ComboBox<>();
        typeSelector.getItems().addAll(Link.LineType.values());
        typeSelector.getSelectionModel().select(target.getType());
        typeSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> target.setType(newValue));
        optionsPane.getChildren().add(typeSelector);
        return optionsPane;
    }
}
