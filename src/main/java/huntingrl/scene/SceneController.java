package huntingrl.scene;

import java.awt.event.InputEvent;

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
            if(sceneChangeEvent.saveOldScene) {
                savedOldScene = currentScene;
            }
            if(sceneChangeEvent.goToSavedOldScene) {
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
