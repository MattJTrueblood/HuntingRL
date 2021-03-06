package huntingrl.view.menu;

import asciiPanel.AsciiPanel;
import huntingrl.view.RenderBuffer;
import huntingrl.view.SceneChangeEvent;

import java.util.ArrayList;

public class QuitScene extends MenuScene {

    public QuitScene(RenderBuffer buffer) {
        super(buffer);
    }

    @Override
    public void init() {
        super.init();
        setMenuItems(buildMenuItems());
    }

    private ArrayList<MenuItem> buildMenuItems() {
        MenuItem cancelItem = new MenuItem("cancel", SceneChangeEvent.builder()
                .saveOldScene(false)
                .goToSavedOldScene(true)
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
