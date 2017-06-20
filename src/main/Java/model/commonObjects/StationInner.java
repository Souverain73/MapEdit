package model.commonObjects;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.ITEXTPDF;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import window.ViewContext;

/**
 * Created by Souverain73 on 20.06.2017.
 */
public class StationInner extends CommonObject {
    @Override
    public void draw(Point2D pos, double size, String label, GraphicsContext gc, ViewContext vc) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.fillRect(pos.getX()-size/2, pos.getY()-size/4, size, size/2);
        gc.strokeRect(pos.getX()-size/2, pos.getY()-size/4, size, size/2);
        double [][] p = new double[2][];
        p[0] = new double[3]; p[1] = new double[3];
        p[0][0] = pos.getX() - size / 2; p[1][0] = pos.getY() - size / 4;
        p[0][1] = pos.getX() - size / 2; p[1][1] = pos.getY() + size / 4;
        p[0][2] = pos.getX() + size / 2; p[1][2] = pos.getY() + size / 4;
        gc.setFill(Color.BLACK);
        gc.fillPolygon(p[0], p[1], 3);
        if (vc.isShowText())
            gc.fillText(label, pos.getX() - size/2, pos.getY() - size/4-10);
    }

    @Override
    public void drawPDF(Point2D pos, double size, String label, PdfWriter writer, ViewContext vc) {
        PdfContentByte dc = writer.getDirectContent();
        dc.saveState();
        dc.setColorFill(BaseColor.WHITE);
        dc.rectangle(pos.getX()-size/2, pos.getY()-size/4, size, size/2);
        dc.closePathFillStroke();
        dc.setColorFill(BaseColor.BLACK);
        dc.moveTo(pos.getX() - size / 2, pos.getY() - size / 4);
        dc.lineTo(pos.getX() - size / 2, pos.getY() + size / 4);
        dc.lineTo(pos.getX() + size / 2, pos.getY() - size / 4);
        dc.lineTo(pos.getX() - size / 2, pos.getY() - size / 4);
        dc.closePath();
        dc.fill();
        dc.beginText();
        dc.setFontAndSize(ITEXTPDF.getFontByName("Arial"), 10);
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
