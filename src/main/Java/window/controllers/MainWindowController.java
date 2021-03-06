package window.controllers;

import helpers.XML;
import instruments.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Lantern;
import model.Map;
import model.Pillar;
import operations.OperationsManager;
import window.ViewContext;


import java.io.*;

public class MainWindowController {
    @FXML
    Canvas mainCanvas;

    @FXML
    Canvas toolCanvas;

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
    @FXML
    RadioMenuItem fixedSizeToggle;

    Stage stage;

    ViewContext vc = new ViewContext();
    OperationsManager opMan = new OperationsManager();

    Map map;
    ITool instrument;
    private MoveTool moveTool;
    private PlacePillarTool placePillarTool;
    private PlaceLineTool placeLineTool;

    @FXML
    private void initialize(){
        canvasPane.widthProperty().addListener((observable, oldValue, newValue) -> fitCanvasToPane());
        canvasPane.heightProperty().addListener((observable, oldValue, newValue) -> fitCanvasToPane());
        rootPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null)
                stage = (Stage) newValue.getWindow();
        });
        instrument = new ToolAdapter();
        updateViewMode(null);
    }

    public void onResize(Scene scene){
        fitCanvasToPane();
    }

    private void fitCanvasToPane() {
        mainCanvas.setWidth(canvasPane.getWidth());
        mainCanvas.setHeight(canvasPane.getHeight());
        toolCanvas.setWidth(canvasPane.getWidth());
        toolCanvas.setHeight(canvasPane.getHeight());
        updateCanvasContent();
    }

    private void updateCanvasContent(){
        mainCanvas.getGraphicsContext2D().clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
        if (map!=null){
            map.draw(mainCanvas.getGraphicsContext2D(), vc);
        }
    }

    @FXML
    private void onMouseClick(MouseEvent event){
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
        map.removeAllTemplates();
        initDefaultTemplates(map);
        updateCanvasContent();
    }

    private void initDefaultTemplates(Map map) {
        map.addTemplate(new Pillar("СКЦ9-2"));
        map.addTemplate(new Lantern("РКУ-250"));
        map.addTemplate(new Pillar("ПАРК"));
        map.addTemplate(new Lantern("ЛСД"));
        map.addTemplate(new Pillar());
        map.addTemplate(new Lantern());
        map.addTemplate(new Lantern("РКУ-125"));
        map.addTemplate(new Lantern("ЖКУ-150"));
        map.addTemplate(new Lantern("РКУ-150"));
    }

    @FXML
    private void saveMap(){
        map.fixErrors();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Карта с объектами (kml)", "*.kml"));
        fc.setInitialDirectory(new File("."));
        File file = fc.showSaveDialog(stage);
        if (file!=null) {
            try(OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
                outputStream.write(XML.xmlHeader());
                outputStream.write(XML.tag("kml", map.SerializeToXML()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void exportPDF(){
        map.fixErrors();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pdf файл (pdf)", "*.pdf"));
        fc.setInitialDirectory(new File("."));
        File file = fc.showSaveDialog(stage);
        if (file!=null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SavePdfDialog.fxml"));
                Parent root = loader.load();
                ((SavePdfDialogController)loader.getController()).setInitialParams(map, file);
                Stage dialog = new Stage();
                dialog.setScene(new Scene(root));
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.showAndWait();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void setToolMove(){
        if (moveTool == null) {
            moveTool = new MoveTool();
        }
        setTool(moveTool);
    }

    @FXML
    private void setToolPlacePillar(){
        if (placePillarTool == null) {
            placePillarTool = new PlacePillarTool();
        }
        setTool(placePillarTool);
    }

    @FXML
    private void setToolPlaceLine(){
        if (placeLineTool == null) {
            placeLineTool = new PlaceLineTool();
        }
        setTool(placeLineTool);
    }

    @FXML
    private void setToolEdit(){ setTool(new EditTool(toolOptionsPane));}

    @FXML
    private void setToolStation(){
        setTool(new StationTool());
    }

    @FXML
    private void setToolNumering(){
        setTool(new NumeringTool());
    }

    @FXML
    private void exit(){
        Platform.exit();
    }

    @FXML
    private void onCanvasScroll(ScrollEvent event){
        zoom(event.getDeltaY()/event.getMultiplierY(), event.getX(), event.getY());
    }

    @FXML
    private void renumber(){
        map.renumber();
        (new Alert(Alert.AlertType.INFORMATION, "Столбы пронумерованы в порядке установки", ButtonType.OK))
                .showAndWait();
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

    private void updateToolCanvas(double x, double y){
        toolCanvas.getGraphicsContext2D().clearRect(0, 0, toolCanvas.getWidth(), toolCanvas.getHeight());
        instrument.draw(x, y);
    }

    @FXML
    private void onMouseMove(MouseEvent event){
        instrument.onMove(event);
        updateToolCanvas(event.getX(), event.getY());
    }

    @FXML
    private void onDrag(MouseEvent event){
        instrument.onDrag(event);
        updateCanvasContent();
        updateToolCanvas(event.getX(), event.getY());
    }

    @FXML
    private void updateViewMode(Event event){
        if (viewModeToggle.isSelected()){
            vc.setDrawMode(ViewContext.DrawMode.FULL);
        }else{
            vc.setDrawMode(ViewContext.DrawMode.SIMPLE);
        }

        vc.setShowText(showTextToggle.isSelected());

        vc.setFixedSize(fixedSizeToggle.isSelected());

        updateCanvasContent();
    }

    @FXML
    private void buttonUndo(){
        opMan.undo();

        updateCanvasContent();
    }

    void setTool(ITool tool){
        tool.init(toolCanvas.getGraphicsContext2D(), vc, map, opMan);
        instrument = tool;
        toolOptionsPane.getChildren().clear();
        Node optionsPanel = tool.createOptionsPanel();
        if (optionsPanel != null){
            toolOptionsPane.getChildren().add(optionsPanel);
        }
    }
}
