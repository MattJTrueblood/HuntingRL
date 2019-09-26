package huntingrl.scene.menu;

import asciiPanel.AsciiPanel;
import huntingrl.scene.GameScene;
import huntingrl.scene.SceneChangeEvent;

import java.util.ArrayList;

public class StartScene extends MenuScene {

    public StartScene(AsciiPanel terminal) {
        super(terminal);
    }

    @Override
    public void init() {
        super.init();
        setMenuItems(buildMenuItems());
    }

    private ArrayList<MenuItem> buildMenuItems() {
        MenuItem cancelItem = new MenuItem("start", SceneChangeEvent.builder()
                .deleteOldScene(true)
                .goToOldScene(false)
                .scene(new GameScene(getTerminal()))
                .build());
        MenuItem quitItem = new MenuItem("quit", SceneChangeEvent.builder()
                .quitApplication(true)
                .build());
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(cancelItem);
        menuItems.add(quitItem);
        return menuItems;
    }

}