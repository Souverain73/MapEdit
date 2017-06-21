package model;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import helpers.CoordsHelper;
import helpers.ID;
import helpers.ITEXTPDF;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import model.interfaces.MapObject;
import org.w3c.dom.Node;
import window.ViewContext;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class Pillar extends Point {
    public static final double BASE_SIZE = 12;

    public static final String pLabel = "label";
    public static final String pLantern = "lantern";
    public static final String pNumber = "number";

    private MapObject pillarTemplate = null;
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
        double r = BASE_SIZE;
        double fs = r * 0.8;

        if (vc.isFixedSize()){
            r = r/vc.getFixedScale() *vc.getScale();
            fs = fs/vc.getFixedScale() *vc.getScale()/2;
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
                            case PILLAR: result.setPillarTemplate(temp); break;
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

    @Override
    public void SerializeToPDF(PdfWriter writer, ViewContext vc) {
        PdfContentByte content = writer.getDirectContent();
        double size = BASE_SIZE * vc.getObjectsScale();
        double fontSize = size * 0.8;

        content.saveState();
        content.circle(vc.pdfx(x), vc.pdfy(y), size/2);
        content.closePathStroke();
        content.newPath();

        if (hasParam(pLantern)) {
            content.circle(vc.pdfx(x), vc.pdfy(y), size);
            content.closePathStroke();
            content.newPath();
        }

        content.beginText();
        content.setFontAndSize(ITEXTPDF.getFontByName("Arial"), (float)fontSize);
        String[] labelStrings = formatLabel().split("\n");
        int i=0;
        for (String s : labelStrings) {
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, s, (float) (vc.pdfx(x) + size + 2), (float) (vc.pdfy(y) + size + 2 - (fontSize*i++)), 0);
        }
        content.endText();

        content.restoreState();
    }

    public Pillar setPillarTemplate(MapObject pillarTemplate) {
        if (this.pillarTemplate != null){
            parents.remove(this.pillarTemplate);
        }
        addTemplate(pillarTemplate);
        this.pillarTemplate = pillarTemplate;
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

    public MapObject getPillarTemplate() {
        return pillarTemplate;
    }

    public MapObject getLanternTemplate() {
        return lanternTemplate;
    }
}
