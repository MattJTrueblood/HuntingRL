package huntingrl.view;

import java.awt.event.InputEvent;
import java.util.Stack;

public class SceneController {
    private Scene currentScene;
    private Stack<Scene> savedOldScenes;

    public SceneController(Scene startingScene) {
        currentScene = startingScene;
        savedOldScenes = new Stack<>();
        currentScene.init();
    }

    public void receiveEvent(InputEvent event) {
        SceneChangeEvent sceneChangeEvent = currentScene.receiveInput(event);
        if(sceneChangeEvent != null) {
            if(sceneChangeEvent.quitApplication) {
                System.exit(0);
            }
            if(sceneChangeEvent.saveOldScene) {
                savedOldScenes.push(currentScene);
            } else {
                savedOldScenes.empty();
            }
            if(sceneChangeEvent.goToSavedOldScene) {
                currentScene = savedOldScenes.pop();
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
