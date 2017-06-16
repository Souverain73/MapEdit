package model;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.CoordsHelper;
import helpers.ID;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Node;
import window.ViewContext;

/**
 * Created by Maksim on 31.05.2017.
 */
public class Lantern extends Pillar {
    public Lantern() {
    }

    public Lantern(String lantern) {
        if (lantern!=null)
            setParam("lantern", lantern);
    }

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

    @Override
    public String SerializeToXML() {
        return XML.tag("Lantern",
                XML.tag("id", String.valueOf(id)),
                XML.coordsTag(x, -y));
    }

    public static Lantern fromXML(Node root){
        Lantern result = new Lantern(null);
        XML.iterateChilds(root, (name, el) -> {
            switch (name) {
                case "coordinates" :
                    Point2D coords = CoordsHelper.parse(el.getTextContent());
                    result.x = coords.getX();
                    result.y = -coords.getY();
                    break;
                case "id":
                    result.id = Integer.parseInt(el.getTextContent());
                    ID.use(result.id);
                    break;
                default:
                    result.addParamFromXML(el);
            }
        });
        return result;
    }

    public void SerializeToPDF(PdfWriter writer, ViewContext vc){
        PdfContentByte content = writer.getDirectContent();
        content.saveState();
        content.circle(vc.pdfx(x), vc.pdfy(y), 5);
        content.circle(vc.pdfx(x), vc.pdfy(y), 10);
        content.closePathStroke();
        content.restoreState();
    }

    @Override
    public ObjectClass getObjectClass() {
        return ObjectClass.LANTERN;
    }
}
