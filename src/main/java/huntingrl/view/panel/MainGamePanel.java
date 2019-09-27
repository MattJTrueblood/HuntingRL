package huntingrl.view.panel;

import asciiPanel.AsciiPanel;
import huntingrl.ecs.GameEngine;
import huntingrl.view.SceneChangeEvent;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * The main view of the game, displaying the local world around the player.
 */
public class MainGamePanel extends DrawPanel {

    private final GameEngine mainGameEngine;

    public MainGamePanel(AsciiPanel terminal, int x, int y, int width, int height) {
        super(terminal, x, y, width, height, 0);
        setBaseColor(Color.BLACK);
        mainGameEngine = new GameEngine(terminal, this.getBounds());
    }

    @Override
    public SceneChangeEvent receiveInput(InputEvent event) {
        return mainGameEngine.receiveInput(event);
    }

    @Override
    public void draw() {
        super.draw();
        mainGameEngine.render();
    }

}
