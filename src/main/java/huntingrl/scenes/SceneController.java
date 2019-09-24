package huntingrl.scenes;

import asciiPanel.AsciiPanel;

import java.awt.event.InputEvent;

public class SceneController {
    private Scene currentScene;
    private Scene savedOldScene;

    public SceneController(Scene startingScene) {
        currentScene = startingScene;
    }

    public void receiveEvent(InputEvent event) {
        SceneChangeEvent sceneChangeEvent = currentScene.receiveInput(event);
        if(sceneChangeEvent != null) {
            if(!sceneChangeEvent.deleteOldScene) {
                savedOldScene = currentScene;
            }
            if(sceneChangeEvent.goToOldScene) {
                currentScene = savedOldScene;
            } else {
                currentScene = sceneChangeEvent.scene;
            }
        }
    }

    public void drawScene(AsciiPanel terminal) {
        this.currentScene.draw(terminal);
    }
}