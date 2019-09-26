package huntingrl.scene;

import asciiPanel.AsciiPanel;

import java.awt.event.InputEvent;

import static javafx.application.Platform.exit;

public class SceneController {
    private Scene currentScene;
    private Scene savedOldScene;

    public SceneController(Scene startingScene) {
        currentScene = startingScene;
        currentScene.init();
    }

    public void receiveEvent(InputEvent event) {
        SceneChangeEvent sceneChangeEvent = currentScene.receiveInput(event);
        if(sceneChangeEvent != null) {
            if(sceneChangeEvent.quitApplication) {
                System.exit(0);
            }
            if(!sceneChangeEvent.deleteOldScene) {
                savedOldScene = currentScene;
            }
            if(sceneChangeEvent.goToOldScene) {
                currentScene = savedOldScene;
            } else {
                currentScene = sceneChangeEvent.scene;
                currentScene.init();
            }
        }
    }

    public void drawScene() {
        this.currentScene.draw();
    }
}
