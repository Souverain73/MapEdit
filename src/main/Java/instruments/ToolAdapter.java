package instruments;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Map;
import operations.OperationsManager;
import window.ViewContext;

/**
 * Created by Souverain73 on 26.05.2017.
 */
public class ToolAdapter implements ITool {
    double lx, ly;
    boolean drag;
    protected GraphicsContext gc;
    protected ViewContext vc;
    protected Map map;
    protected OperationsManager opMan;

    @Override
    public void init(GraphicsContext gc, ViewContext vc, Map map, OperationsManager opMan) {
        this.gc = gc;
        this.vc = vc;
        this.map = map;
        this.opMan = opMan;
    }

    @Override
    public String getName() {
        return "ToolAdapter";
    }

    @Override
    public Node createOptionsPanel() {
        return null;
    }

    @Override
    public void onClick(MouseEvent clickEvent) {
        if (drag) return;
        click(clickEvent.getX(), clickEvent.getY(), clickEvent.getButton());
    }

    protected void click(double x, double y, MouseButton button){

    }

    @Override
    public void onDrag(MouseEvent dragEvent) {
        drag = true;
        drag(lx, ly, dragEvent.getX(), dragEvent.getY(), dragEvent.getButton());
        lx = dragEvent.getX();
        ly = dragEvent.getY();
    }

    protected void drag(double oldx, double oldy, double newx, double newy, MouseButton button){
        if (button != MouseButton.SECONDARY) return;
        vc.move(newx-oldx, newy-oldy);
    }

    @Override
    public void onMove(MouseEvent moveEvent) {
        drag = false;
        move(lx, ly, moveEvent.getX(), moveEvent.getY());
        lx = moveEvent.getX();
        ly = moveEvent.getY();
    }

    protected void move(double oldx, double oldy, double newx, double newy){

    }


    @Override
    public String toString() {
        return getName();
    }
}
