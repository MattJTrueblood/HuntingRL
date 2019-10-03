package huntingrl.view;

import asciiPanel.AsciiPanel;
import huntingrl.view.panel.MainGamePanel;
import huntingrl.util.Constants;

public class GameScene extends MultiPanelScene {

    public GameScene(AsciiPanel terminal) {
        super(terminal);
    }

    public void init() {
        addPanel( new MainGamePanel(terminal, 1,1, Constants.TERMINAL_WIDTH - 2, Constants.TERMINAL_HEIGHT - 2));
    }
}
