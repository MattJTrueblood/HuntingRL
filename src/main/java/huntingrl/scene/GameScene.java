package huntingrl.scene;

import huntingrl.module.panel.DrawPanel;

public class GameScene extends MultiPanelScene {

    public GameScene() {
        //TODO:  test panels only
        addPanel(new DrawPanel(1, 1, 12, 20, 1));
        addPanel(new DrawPanel(8, 8, 18, 70, 3));
        addPanel(new DrawPanel(22, 10, 10, 80, 2));
        addPanel(new DrawPanel(12, 16, 2, 2, 13));
    }
}
