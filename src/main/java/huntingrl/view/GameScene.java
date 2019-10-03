package huntingrl.view;

import asciiPanel.AsciiPanel;
import huntingrl.view.panel.MainGamePanel;
import huntingrl.util.Constants;

public class GameScene extends MultiPanelScene {

    public GameScene(RenderBuffer buffer) {
        super(buffer);
    }

    public void init() {
        addPanel( new MainGamePanel(buffer, 1,1, Constants.TERMINAL_WIDTH - 2, Constants.TERMINAL_HEIGHT - 2));
    }
}
