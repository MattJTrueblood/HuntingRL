package huntingrl.module.panel;

import asciiPanel.AsciiPanel;
import huntingrl.module.Module;
import huntingrl.scene.SceneChangeEvent;
import lombok.Getter;
import lombok.Setter;

import java.awt.event.InputEvent;

@Getter
@Setter
public class DrawPanel implements Module {
    private final int x;
    private final int y;
    private final int height;
    private final int width;
    private final int zIndex;

    public DrawPanel(int x, int y, int height, int width, int zIndex) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.zIndex = zIndex;
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        return null;
    }

    /**
     * Default implementation should be used for debugging only.  Fills the panel with the z index (in hexadecimal)
     * @param terminal the AsciiPanel to use to draw
     */
    public void draw(AsciiPanel terminal) {
        for(int i = x; i < (x + width); i++) {
            for(int j = y; j < (y + height); j++) {
                terminal.write(Character.forDigit(zIndex, 16), i, j);
            }
        }
    }
}
