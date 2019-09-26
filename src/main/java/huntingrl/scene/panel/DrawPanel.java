package huntingrl.scene.panel;

import asciiPanel.AsciiPanel;
import huntingrl.scene.SceneChangeEvent;
import lombok.Getter;
import lombok.Setter;

import java.awt.event.InputEvent;

/**
 * A DrawPanel is a panel that represents a specific fixed area of a screen to render to.  This can be used for game
 * windows, ui portions of the screen, and menu popups, for example.
 */
@Getter
@Setter
public class DrawPanel implements Panel {

    private AsciiPanel terminal;
    private final PanelBounds bounds;
    private final int zIndex;

    public DrawPanel(AsciiPanel terminal, int x, int y, int width, int height, int zIndex) {
        this.terminal = terminal;
        this.bounds = new PanelBounds(x, y, width, height);
        this.zIndex = zIndex;
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        return null;
    }

    /**
     * Default implementation should be used for debugging only.  Fills the panel with the z index (in hexadecimal)
     */
    public void draw() {
        for(int i = bounds.getX(); i < (bounds.getX() + bounds.getWidth()); i++) {
            for(int j = bounds.getY(); j < (bounds.getY() + bounds.getHeight()); j++) {
                terminal.write(Character.forDigit(zIndex, 16), i, j);
            }
        }
    }
}
