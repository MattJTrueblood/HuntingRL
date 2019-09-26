package huntingrl.scene;

import asciiPanel.AsciiPanel;
import huntingrl.scene.panel.MainGamePanel;
import huntingrl.util.Constants;

public class GameScene extends MultiPanelScene {

    public GameScene(AsciiPanel terminal) {
        super(terminal);
    }

    public void init() {
        addPanel( new MainGamePanel(terminal, 0,0, Constants.TERMINAL_WIDTH, Constants.TERMINAL_HEIGHT));
    }
}
