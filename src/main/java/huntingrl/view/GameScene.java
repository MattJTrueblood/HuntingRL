package huntingrl.view;

import huntingrl.ecs.GameEngine;
import huntingrl.view.panel.GamePanel;
import huntingrl.util.Constants;

import java.awt.event.InputEvent;

public class GameScene extends MultiPanelScene {

    GameEngine engine;

    public GameScene(RenderBuffer buffer) {
        super(buffer);
    }

    public void init() {
        engine = new GameEngine(buffer);
        //zoomed out panel
        addPanel( new GamePanel(buffer, 1,1,
                (Constants.TERMINAL_WIDTH / 2) - 1, Constants.TERMINAL_HEIGHT - 2,
                engine, 32,false));
        //zoomed in panel
        addPanel( new GamePanel(buffer, (Constants.TERMINAL_WIDTH / 2) + 1,1,
                (Constants.TERMINAL_WIDTH / 2) - 2, Constants.TERMINAL_HEIGHT - 2,
                engine, 1, false));
    }

    /**
     * Normally, each panel receives input.  Howevever, with multiple
     * @param event
     * @return
     */
    @Override
    public SceneChangeEvent receiveInput(InputEvent event) {
        return engine.receiveInput(event);

    }
}
