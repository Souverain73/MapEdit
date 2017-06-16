package model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.CoordsHelper;
import helpers.ID;
import helpers.ITEXTPDF;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import org.w3c.dom.Node;
import window.ViewContext;

import java.io.IOException;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class Pillar extends Point {
    public static final String pLabel = "label";
    public static final String pLantern = "lantern";
    public static final String pNumber = "number";

    private MapObject pillarTeplate = null;
    private MapObject lanternTemplate = null;

    public Pillar(){

    }


    public Pillar(String label){
        super();
        if (label!=null){
            setParam(pLabel, label);
        }
    }

    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        double r = 12;
        double fs = 10;

        if (vc.isFixedSize()){
            r = r/ViewContext.FIXED_SCALE *vc.getScale();
            fs = fs/ViewContext.FIXED_SCALE *vc.getScale()/2;
        }

        if (vc.getDrawMode() == ViewContext.DrawMode.FULL) {
            gc.strokeOval(vc.absX(x) - r/4, vc.absY(y) - r/4, r/2, r/2);
            if (hasParam(pLantern)){
                gc.strokeOval(vc.absX(x) - r/2, vc.absY(y) - r/2, r, r);
            }
        }else{
            super.draw(gc, vc);
        }

        if (vc.isShowText()){
            gc.setFont(new Font(fs));
            gc.fillText(formatLabel(), vc.absX(x)+r/2, vc.absY(y)-r/2);
        }
    }

    private String formatLabel(){
        StringBuilder sb = new StringBuilder();
        if (hasParam(pNumber))
            sb.append(String.format("%s ", getParam(pNumber)));

        if (hasParam(pLabel))
            sb.append(String.format("%s", getParam(pLabel)));

        if (hasParam(pLantern))
            sb.append(String.format("\n%s", getParam(pLantern)));

        return sb.toString();
    }

    public static Pillar fromXML(Node root, Map map){
        Pillar result = new Pillar(null);
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
                case "parent":
                    int id = Integer.parseInt(el.getTextContent());
                    MapObject temp = map.getObjectById(id);
                    if (temp!=null) {
                        switch (temp.getObjectClass()){
                            case LANTERN: result.setLanternTemplate(temp); break;
                            case PILLAR: result.setPillarTeplate(temp); break;
                        }
                    }else{
                        throw new IllegalStateException("Used not defined template");
                    }
                    break;
                default:
                    result.addParamFromXML(el);
            }
        });
        return result;
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Pillar",
                XML.tag("id", String.valueOf(id)),
                parentsToXMLString(),
                paramsToXMLString(),
                XML.coordsTag(x, -y));
    }

    public void SerializeToPDF(PdfWriter writer, ViewContext vc) throws DocumentException, IOException {
        PdfContentByte content = writer.getDirectContent();
        content.saveState();
        content.circle(vc.pdfx(x), vc.pdfy(y), 6);
        if (hasParam(pLantern))
            content.circle(vc.pdfx(x), vc.pdfy(y), 12);

        content.closePathStroke();

        content.beginText();
        content.setFontAndSize(ITEXTPDF.getFontByName("Arial"), 10);
        String[] labelStrings = formatLabel().split("\n");
        int i=0;
        for (String s : labelStrings) {
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, s, (float) vc.pdfx(x) + 15, (float) vc.pdfy(y) + 15 - (10*i++), 0);
        }
        content.endText();

        content.restoreState();
    }

    public Pillar setPillarTeplate(MapObject pillarTeplate) {
        if (this.pillarTeplate != null){
            parents.remove(this.pillarTeplate);
        }
        addTemplate(pillarTeplate);
        this.pillarTeplate = pillarTeplate;
        return this;
    }

    public Pillar setLanternTemplate(MapObject lanternTemplate) {
        if (this.lanternTemplate != null){
            parents.remove(this.lanternTemplate);
        }
        addTemplate(lanternTemplate);
        this.lanternTemplate = lanternTemplate;
        return this;
    }

    public MapObject getPillarTeplate() {
        return pillarTeplate;
    }

    public MapObject getLanternTemplate() {
        return lanternTemplate;
    }
}
