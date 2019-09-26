package huntingrl.scene;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.Engine;
import huntingrl.ecs.systems.RenderSystem;
import huntingrl.module.panel.MainGamePanel;
import huntingrl.util.Constants;

public class GameScene extends MultiPanelScene {

    MainGamePanel gamePanel;

    public GameScene(AsciiPanel terminal) {
        super(terminal);
        this.gamePanel = new MainGamePanel(terminal, 0,0, Constants.TERMINAL_WIDTH, Constants.TERMINAL_HEIGHT);
        addPanel(gamePanel);
    }
}
