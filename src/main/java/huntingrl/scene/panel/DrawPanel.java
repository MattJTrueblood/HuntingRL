package huntingrl.scene.panel;

import asciiPanel.AsciiPanel;
import huntingrl.scene.SceneChangeEvent;
import lombok.Getter;
import lombok.Setter;

import java.awt.event.InputEvent;

/**
 * A DrawPanel is a panel that represents a specific fixed area of a screen to draw to.  This can be used for game
 * windows, ui portions of the screen, and menu popups, for example.
 */
@Getter
@Setter
public class DrawPanel implements Panel {

    private AsciiPanel terminal;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int zIndex;

    public DrawPanel(AsciiPanel terminal, int x, int y, int width, int height, int zIndex) {
        this.terminal = terminal;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        return null;
    }

    /**
     * Default implementation should be used for debugging only.  Fills the panel with the z index (in hexadecimal)
     * @param terminal the AsciiPanel to use to draw
     */
    public void draw() {
        for(int i = x; i < (x + width); i++) {
            for(int j = y; j < (y + height); j++) {
                terminal.write(Character.forDigit(zIndex, 16), i, j);
            }
        }
    }
}
