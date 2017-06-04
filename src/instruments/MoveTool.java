package instruments;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;

/**
 * Created by Souverain73 on 26.05.2017.
 */
public class MoveTool extends ToolAdapter {
    @Override
    public String getName() {
        return "Move";
    }

    @Override
    public Node createOptionsPanel() {
        return super.createOptionsPanel();
    }

    @Override
    protected void drag(double oldx, double oldy, double newx, double newy, MouseButton button) {
        vc.move(newx-oldx, newy-oldy);
    }
}
