package model.commonObjects;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.ITEXTPDF;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import window.ViewContext;

/**
 * Created by Souverain73 on 20.06.2017.
 */
public class StationOuter extends CommonObject {
    @Override
    public void draw(Point2D pos, double size, String label, GraphicsContext gc, ViewContext vc) {
        double fontSize = size * 0.3;
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.fillRect(pos.getX()-size/2, pos.getY()-size/4, size, size/2);
        gc.strokeRect(pos.getX()-size/2, pos.getY()-size/4, size, size/2);
        gc.setFill(Color.BLACK);
        gc.fillRect(pos.getX() - size/2, pos.getY()+size/12, size, size/6);
        if (vc.isShowText()) {
            gc.setFont(new Font(fontSize));
            gc.fillText(label, pos.getX() - size / 2, pos.getY() - size / 4 - 10);
        }
    }

    @Override
    public void drawPDF(Point2D pos, double size, String label, PdfWriter writer, ViewContext vc) {
        double fontSize = size * 0.3;
        PdfContentByte dc = writer.getDirectContent();
        dc.saveState();
        dc.setColorFill(BaseColor.WHITE);
        dc.rectangle(pos.getX()-size/2, pos.getY()-size/4, size, size/2);
        dc.closePathFillStroke();
        dc.setColorFill(BaseColor.BLACK);
        dc.rectangle(pos.getX() - size/2, pos.getY()+size/12, size, size/6);
        dc.closePathFillStroke();
        dc.beginText();
        dc.setFontAndSize(ITEXTPDF.getFontByName("Arial"), (float)fontSize);
        dc.showTextAligned(PdfContentByte.ALIGN_LEFT, label, (float)(pos.getX() - size/2), (float)(pos.getY() + size/4+5), 0);
        dc.endText();
        dc.restoreState();
    }

    @Override
    public boolean isPointIn(Point2D pos, Point2D point, double size) {
        double left =  pos.getX() - size / 2;
        double right =  pos.getX() + size / 2;
        double top = pos.getY() - size / 4;
        double bot = pos.getY() + size / 4;
        if (point.getX() < left || point.getX() > right || point.getY() < top || point.getY() > bot) return false;
        return true;
    }
}
