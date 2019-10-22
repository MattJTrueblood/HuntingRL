package huntingrl.view;

import com.flowpowered.noise.module.source.Const;
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
        //zoomed out panel 1
        addPanel( new GamePanel(buffer, 1,1,
                (Constants.TERMINAL_WIDTH / 2) - 1, (Constants.TERMINAL_HEIGHT / 2) - 1,
                engine, (short) 16, false, 40));
        //zoomed out panel 2
        addPanel( new GamePanel(buffer, 1, (Constants.TERMINAL_HEIGHT / 2) + 1,
                (Constants.TERMINAL_WIDTH / 2) - 1, (Constants.TERMINAL_HEIGHT / 2) - 2,
                engine, (short) 64, false, 40));
        //zoomed in panel
        addPanel( new GamePanel(buffer, (Constants.TERMINAL_WIDTH / 2) + 1,1,
                (Constants.TERMINAL_WIDTH / 2) - 2, Constants.TERMINAL_HEIGHT - 2,
                engine, (short) 1, true, 12));
    }

    @Override
    public SceneChangeEvent receiveInput(InputEvent event) {
        return engine.receiveInput(event);
    }
}
