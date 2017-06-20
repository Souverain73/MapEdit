package model;

import com.itextpdf.text.pdf.PdfWriter;
import helpers.CoordsHelper;
import helpers.ID;
import helpers.XML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import model.commonObjects.CommonObject;
import model.commonObjects.StationInner;
import model.commonObjects.StationOuter;
import org.w3c.dom.Node;
import window.ViewContext;

/**
 * Created by Souverain73 on 19.06.2017.
 */
public class Station extends Item {
    private static final double BASE_SIZE = 30;
    private interface drawMethod{
        void draw(Point2D pos, double size, String label, GraphicsContext gc, ViewContext vc);
    }
    private interface pdfDrawMethod{
        void draw(Point2D pos, double size, String label, PdfWriter writer, ViewContext vc);
    }

    public enum StationType {
        INNER("Внутри ТП, здания", new StationInner()),
        OUTER("Внешняяя", new StationOuter());

        private String name;
        private CommonObject co;


        StationType(String name, CommonObject co){
            this.name = name;
            this.co = co;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private StationType type;

    public Station() {
        super();
    }

    public Station(StationType type) {
        super();
        this.type = type;
    }

    private String formatLabel(){
        if (hasParam(Pillar.pLabel))
            return getParam(Pillar.pLabel);
        return "";
    }

    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        double size = BASE_SIZE;
        if (vc.isFixedSize()){
            size = size / ViewContext.FIXED_SCALE *vc.getScale();
        }
        type.co.draw(new Point2D(vc.absX(x), vc.absY(y)), size, formatLabel(), gc, vc);
    }

    public void SerializeToPDF(PdfWriter writer, ViewContext vc){
        double size = BASE_SIZE;
        if (vc.isFixedSize()){
            size = size / ViewContext.FIXED_SCALE *vc.getScale();
        }
        type.co.drawPDF(new Point2D(vc.pdfx(x), vc.pdfy(y)), size, formatLabel(), writer, vc);
    }

    @Override
    public boolean isPointIn(double x, double y, double r) {
        return type.co.isPointIn(new Point2D(this.x, this.y), new Point2D(x,y), r * BASE_SIZE);
    }

    @Override
    public void place(double x, double y) {
        super.place(x, y);
    }

    @Override
    public String SerializeToXML() {
        return XML.tag("Station",
                XML.tag("id", String.valueOf(id)),
                parentsToXMLString(),
                paramsToXMLString(),
                XML.coordsTag(x, -y),
                XML.tag("type", type.name()));
    }

    public static Station fromXML(Node root){
        Station res = new Station();
        XML.iterateChilds(root, (name, el) -> {
            switch (name){
                case "id":
                    res.id = Integer.parseInt(el.getTextContent());
                    ID.use(res.id);
                    break;
                case "type":
                    res.type = StationType.valueOf(el.getTextContent());
                    break;
                case "coordinates" :
                    Point2D coords = CoordsHelper.parse(el.getTextContent());
                    res.x = coords.getX();
                    res.y = -coords.getY();
                    break;
                default:
                    res.addParamFromXML(el);
            }
        });
        return res;
    }
}
