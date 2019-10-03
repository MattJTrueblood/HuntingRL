package huntingrl.view.panel;

import asciiPanel.AsciiPanel;
import huntingrl.view.RenderBuffer;
import huntingrl.view.SceneChangeEvent;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * A DrawPanel is a panel that represents a specific fixed area of a screen to render to.  This can be used for game
 * windows, ui portions of the screen, and menu popups, for example.
 */
@Getter
@Setter
public class DrawPanel implements Panel {

    private RenderBuffer buffer;
    private final PanelBounds bounds;
    private final int zIndex;
    private Color baseColor = Color.BLACK;

    public DrawPanel(RenderBuffer buffer, int x, int y, int width, int height, int zIndex) {
        this.buffer = buffer;
        this.bounds = new PanelBounds(x, y, width, height);
        this.zIndex = zIndex;
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        return null;
    }

    public void draw() {
        for(int i = bounds.getX(); i < (bounds.getX() + bounds.getWidth()); i++) {
            for(int j = bounds.getY(); j < (bounds.getY() + bounds.getHeight()); j++) {
                buffer.write((char) 0, i, j, baseColor, baseColor);
            }
        }
    }
}
