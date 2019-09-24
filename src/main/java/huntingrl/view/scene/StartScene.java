package huntingrl.view.scene;

import asciiPanel.AsciiPanel;
import lombok.NoArgsConstructor;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@NoArgsConstructor
public class StartScene implements Scene{

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        if(inputEvent instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) inputEvent;
            if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                return SceneChangeEvent.builder()
                        .scene(new GameScene()).build();
            }
        }
        return null;
    }

    public void draw(AsciiPanel terminal) {
        terminal.writeCenter("PRESS ENTER PLEASE!", 30);
    }
}
