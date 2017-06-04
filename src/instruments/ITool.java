package instruments;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import model.Map;
import window.ViewContext;

/**
 * Created by Souverain73 on 26.05.2017.
 */
public interface ITool {
    void init(GraphicsContext gc, ViewContext vc, Map map);
    String getName();
    Node createOptionsPanel();
    /** base method for click event */
    void onClick(MouseEvent clickEvent);
    /** base method for drag event */
    void onDrag(MouseEvent dragEvent);
    /** base method for move event */
    void onMove(MouseEvent moveEvent);
}
