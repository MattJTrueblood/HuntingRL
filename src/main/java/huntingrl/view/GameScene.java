package huntingrl.view;

import asciiPanel.AsciiPanel;
import huntingrl.view.panel.MainGamePanel;
import huntingrl.util.Constants;

public class GameScene extends MultiPanelScene {

    public GameScene(AsciiPanel terminal) {
        super(terminal);
    }

    public void init() {
        addPanel( new MainGamePanel(terminal, 10,10, Constants.TERMINAL_WIDTH - 20, Constants.TERMINAL_HEIGHT - 20));
    }
}
