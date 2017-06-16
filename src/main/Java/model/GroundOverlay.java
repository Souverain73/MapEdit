package model;

import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import org.w3c.dom.Node;
import window.Main;
import window.ViewContext;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Souverain73 on 30.05.2017.
 */
public class GroundOverlay implements MapObject, IPlaceable {
    double south;
    double north;
    double east;
    double west;
    double w;
    double h;
    Image overlay;
    String imageName;
    String imagePath;

    @Override
    public void place(double x, double y) {

    }

    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        gc.drawImage(overlay, west*vc.getScalex() + vc.getX(), north*vc.getScaley() + vc.getY(),
                w * vc.getScalex(), h * vc.getScaley());
    }

    @Override
    public Point2D getPos() {
        return new Point2D(east, north);
    }

    @Override
    public int getID() {
        return -1;
    }

    @Override
    public boolean isPointIn(double x, double y, double r) {
        return false;
    }

    @Override
    public void setPos(Point2D pos) {

    }

    public static GroundOverlay fromXML(Node root, File file){
        GroundOverlay result = new GroundOverlay();
        for (Node el : new XML.IterableNodeList(root.getChildNodes())){
            switch (el.getNodeName()){
                case "name" :
                    result.imageName = el.getTextContent();
                    result.imagePath = file.getParent() + "/" +el.getTextContent();
                    result.overlay = new Image("file:" + file.getParent() + "/" +el.getTextContent()); break;
                case "LatLonBox" : parseLatLonBox(el, result); break;
            }
        }
        result.w = Math.abs(result.east - result.west);
        result.h = Math.abs(result.north - result.south);

        if (result.overlay.isError()){
            (new Alert(Alert.AlertType.ERROR, "Ошибка при загрузке изображения", ButtonType.CLOSE)).showAndWait();
        }else{
            (new Alert(Alert.AlertType.INFORMATION, "Изображение загружено", ButtonType.OK)).showAndWait();
        }

        if (result.overlay.getException() != null){
            (new Alert(Alert.AlertType.ERROR, result.overlay.getException().toString(), ButtonType.CLOSE)).showAndWait();
        }

        if (Main.debug){
            System.out.println(result.toString());
        }

        return result;
    }

    private static void parseLatLonBox(Node root, GroundOverlay overlay) {
        for (Node el : new XML.IterableNodeList(root.getChildNodes())) {
            switch (el.getNodeName()){
                case "north": overlay.north = -Double.parseDouble(el.getTextContent()); break;
                case "south": overlay.south = -Double.parseDouble(el.getTextContent()); break;
                case "west": overlay.west = Double.parseDouble(el.getTextContent()); break;
                case "east": overlay.east = Double.parseDouble(el.getTextContent()); break;
            }
        }
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("GroundOverlay",
                XML.tag("name", imageName),
                XML.tag("LatLonBox",
                        XML.tag("north", String.valueOf(-north)),
                        XML.tag("south", String.valueOf(-south)),
                        XML.tag("east", String.valueOf(east)),
                        XML.tag("west", String.valueOf(west))));
    }

    public double getSouth() {
        return south;
    }

    public double getNorth() {
        return north;
    }

    public double getEast() {
        return east;
    }

    public double getWest() {
        return west;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getW() {
        return w;
    }

    public double getH() {
        return h;
    }

    @Override
    public String getParam(String name) {
        return null;
    }

    @Override
    public void setParam(String name, String value) {

    }

    @Override
    public Map<String, String> getRawParams() {
        return null;
    }

    @Override
    public String toString() {
        return "GroundOverlay{" +
                "south=" + south +
                ", north=" + north +
                ", east=" + east +
                ", west=" + west +
                ", w=" + w +
                ", h=" + h +
                ", overlay=" + overlay +
                ", imageName='" + imageName + '\'' +
                '}';
    }

    @Override
    public ObjectClass getObjectClass() {
        return ObjectClass.OVERLAY;
    }

    @Override
    public void addTemplate(MapObject template) {

    }

    @Override
    public List<MapObject> getTemplates() {
        return null;
    }

    @Override
    public boolean hasParam(String name) {
        return false;
    }
}
