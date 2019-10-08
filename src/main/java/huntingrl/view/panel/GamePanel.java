package huntingrl.view.panel;

import huntingrl.ecs.GameEngine;
import huntingrl.view.RenderBuffer;

import java.awt.*;

/**
 * The main view of the game, displaying the local world around the player.
 */
public class GamePanel extends DrawPanel {

    private final GameEngine mainGameEngine;
    private final boolean inputEnabled;
    private final ViewFrame viewFrame;

    public GamePanel(RenderBuffer buffer, int x, int y, int width, int height, GameEngine engine, short tileSize, boolean inputEnabled) {
        super(buffer, x, y, width, height, 0);
        setBaseColor(Color.BLACK);
        this.viewFrame = new ViewFrame(this.getBounds(), 0, 0, tileSize);
        this.inputEnabled = inputEnabled;
        mainGameEngine = engine;
    }

    @Override
    public void draw() {
        super.draw();
        mainGameEngine.renderInView(this.viewFrame);
    }

}
