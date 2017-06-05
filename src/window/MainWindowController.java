package window;

import helpers.XML;
import instruments.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Map;


import javax.swing.*;
import java.io.*;

public class MainWindowController {
    @FXML
    Canvas mainCanvas;

    @FXML
    Pane canvasPane;

    @FXML
    AnchorPane rootPane;

    @FXML
    ComboBox toolSelector;

    @FXML
    Pane toolOptionsPane;

    @FXML
    RadioMenuItem viewModeToggle;
    @FXML
    RadioMenuItem showTextToggle;

    Stage stage;

    ViewContext vc = new ViewContext();

    Map map;

    ITool instrument;

    @FXML
    private void initialize(){
        canvasPane.widthProperty().addListener((observable, oldValue, newValue) -> fitCanvasToPane());
        canvasPane.heightProperty().addListener((observable, oldValue, newValue) -> fitCanvasToPane());
        rootPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null)
                stage = (Stage) newValue.getWindow();
        });
        instrument = new ToolAdapter();
    }

    public void onResize(Scene scene){
        fitCanvasToPane();
    }

    private void fitCanvasToPane() {
        mainCanvas.setWidth(canvasPane.getWidth());
        mainCanvas.setHeight(canvasPane.getHeight());
        updateCanvasContent();
    }

    private void updateCanvasContent(){
        System.out.println("Update canvas");
        mainCanvas.getGraphicsContext2D().clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
        if (map!=null){
            System.out.println("Draw map");
            map.draw(mainCanvas.getGraphicsContext2D(), vc);
        }
    }

    @FXML
    private void onMouseClick(MouseEvent event){
        System.out.println(String.format("Image coords: [%f, %f]", vc.vtix(event.getX()), vc.vtiy(event.getY())));
        System.out.println(String.format("Geo coords: [%d, %d]", 0, 0));

        instrument.onClick(event);

        updateCanvasContent();
    }

    @FXML
    private void openMap() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Карта с объектами (kml)", "*.kml"));
        fc.setInitialDirectory(new File("."));
        File file = fc.showOpenDialog(stage);
        if (file==null) return;
        map = new Map(file);
        vc.reset();
        int sc = 65000;
        vc.setScale(sc);
        vc.setX(-map.getGeox()*vc.getScalex());
        vc.setY(-map.getGeoy()*vc.getScaley());
        setTool(new MoveTool());
        updateCanvasContent();
    }

    @FXML
    private void saveMap(){
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Карта с объектами (kml)", "*.kml"));
        fc.setInitialDirectory(new File("."));
        File file = fc.showSaveDialog(stage);
        if (file!=null) {
            try(OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file))) {
                outputStream.write(XML.xmlHeader());
                outputStream.write(XML.tag("kml", map.SerializeToXML()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void setToolMove(){
        setTool(new MoveTool());
    }

    @FXML
    private void setToolPlacePillar(){
        setTool(new PlacePillarTool());
    }

    @FXML
    private void setToolPlaceLine(){
        setTool(new PlaceLineTool());
    }

    @FXML
    private void exit(){
        Platform.exit();
    }

    @FXML
    private void onCanvasScroll(ScrollEvent event){
        zoom(event.getDeltaY()/event.getMultiplierY(), event.getX(), event.getY());
        System.out.println(vc);
    }

    private void zoom(double ammount, double tx, double ty){
        double oldScalex = vc.getScalex();
        double oldScaley = vc.getScaley();
        double ix = (tx  - vc.getX()) / oldScalex;
        double iy = (ty - vc.getY()) / oldScaley;

        vc.setScale(oldScalex * Math.pow(2, ammount));

        double newScalex = vc.getScalex();
        double newScaley = vc.getScaley();
        vc.setY((ty / newScaley - iy) * newScaley);
        vc.setX((tx / newScalex - ix) * newScalex);

        updateCanvasContent();
    }

    private void move(double dx, double dy){
        vc.move(dx, dy);
        updateCanvasContent();
    }

    @FXML
    private void onMouseMove(MouseEvent event){
        instrument.onMove(event);
    }

    @FXML
    private void onDrag(MouseEvent event){
        instrument.onDrag(event);
        updateCanvasContent();
    }

    @FXML
    private void onChangeTool(Event event){
        setTool((ITool) toolSelector.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void updateViewMode(Event event){
        if (viewModeToggle.isSelected()){
            vc.setDrawMode(ViewContext.DrawMode.FULL);
        }else{
            vc.setDrawMode(ViewContext.DrawMode.SIMPLE);
        }

        vc.setShowText(showTextToggle.isSelected());

        updateCanvasContent();
    }

    void setTool(ITool tool){
        tool.init(mainCanvas.getGraphicsContext2D(), vc, map);
        instrument = tool;
        toolOptionsPane.getChildren().clear();
        Node optionsPanel = tool.createOptionsPanel();
        if (optionsPanel != null){
            toolOptionsPane.getChildren().add(optionsPanel);
        }
    }
}
