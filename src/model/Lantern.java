package model;

import helpers.XML;
import javafx.scene.canvas.GraphicsContext;
import window.ViewContext;

/**
 * Created by Maksim on 31.05.2017.
 */
public class Lantern extends Pillar {
    @Override
    public void draw(GraphicsContext gc, ViewContext vc) {
        if (vc.getDrawMode() == ViewContext.DrawMode.FULL) {
            gc.strokeOval(vc.absX(x) - 3, vc.absY(y) - 3, 6, 6);
            gc.strokeOval(vc.absX(x) - 6, vc.absY(y) - 6, 12, 12);
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
                XML.tag("label", label),
                XML.coordsTag(x, -y));
    }
}
