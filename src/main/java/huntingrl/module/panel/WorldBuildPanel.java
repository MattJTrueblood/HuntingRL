package huntingrl.module.panel;

import asciiPanel.AsciiPanel;
import huntingrl.scene.SceneChangeEvent;

import java.awt.event.InputEvent;

public class WorldBuildPanel extends DrawPanel {

    public WorldBuildPanel(int x, int y, int height, int width) {
        super(x, y, height, width, 0);
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        return super.receiveInput(event);
    }

    public void draw(AsciiPanel terminal) {
        //TODO:  actually implement this instead of using the default debug functionality
        super.draw(terminal);
    }
}
