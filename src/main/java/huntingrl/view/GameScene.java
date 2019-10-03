package huntingrl.view;

import huntingrl.ecs.GameEngine;
import huntingrl.view.panel.GamePanel;
import huntingrl.util.Constants;

public class GameScene extends MultiPanelScene {

    public GameScene(RenderBuffer buffer) {
        super(buffer);
    }

    public void init() {
        GameEngine engine = new GameEngine(buffer);
        //zoomed out panel
        addPanel( new GamePanel(buffer, 1,1,
                (Constants.TERMINAL_WIDTH / 2) - 1, Constants.TERMINAL_HEIGHT - 2,
                engine, 32,false));
        //zoomed in panel
        addPanel( new GamePanel(buffer, (Constants.TERMINAL_WIDTH / 2) + 1,1,
                (Constants.TERMINAL_WIDTH / 2) - 2, Constants.TERMINAL_HEIGHT - 2,
                engine, 1, false));
    }
}
