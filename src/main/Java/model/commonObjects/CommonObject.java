package model.commonObjects;

import com.itextpdf.text.pdf.PdfWriter;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import window.ViewContext;

/**
 * Created by Souverain73 on 20.06.2017.
 */
public class CommonObject {
    public void draw(Point2D pos, double size, String label, GraphicsContext gc, ViewContext vc) {

    }

    public void drawPDF(Point2D pos, double size, String label, PdfWriter writer, ViewContext vc){

    }

    public boolean isPointIn(Point2D pos, Point2D point, double size){
        return false;
    }
}
