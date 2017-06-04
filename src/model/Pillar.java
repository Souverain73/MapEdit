package model;

import helpers.CoordsHelper;
import helpers.ID;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Node;
import window.ViewContext;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class Pillar extends Point {
    protected String label = "";

    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        if (vc.getDrawMode() == ViewContext.DrawMode.FULL) {
            gc.strokeOval(vc.absX(x) - 3, vc.absY(y) - 3, 6, 6);
        }else{
            super.draw(gc, vc);
        }

        if (vc.isShowText()){
            gc.fillText(String.valueOf(id), vc.absX(x)+6, vc.absY(y)-4);
        }
    }

    public static Pillar fromXML(Node root){
        Pillar result = new Pillar();
        for (Node el : new XML.IterableNodeList(root.getChildNodes())) {
            switch (el.getNodeName().toLowerCase()) {
                case "coordinates" :
                    Point2D coords = CoordsHelper.parse(el.getTextContent());
                    result.x = coords.getX();
                    result.y = -coords.getY();
                    break;
                case "id":
                    result.id = Integer.parseInt(el.getTextContent());
                    ID.use(result.id);
                    break;
                case "label": result.label = el.getTextContent(); break;
            }
        }
        return result;
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Pillar",
                XML.tag("id", String.valueOf(id)),
                XML.tag("label", label),
                XML.coordsTag(x, -y));
    }
}
