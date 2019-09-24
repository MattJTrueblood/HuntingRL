package huntingrl.view.scene;

import asciiPanel.AsciiPanel;
import lombok.NoArgsConstructor;

import java.awt.event.InputEvent;

@NoArgsConstructor
public class GameScene implements Scene {

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        return null;
    }

    public void draw(AsciiPanel terminal) {
        terminal.writeCenter("GAME SCENE PLACEHOLDER", 30);
    }
}
