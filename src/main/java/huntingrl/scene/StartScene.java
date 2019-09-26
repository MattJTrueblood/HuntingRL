package huntingrl.scene;

import asciiPanel.AsciiPanel;
import lombok.NoArgsConstructor;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@NoArgsConstructor
public class StartScene implements Scene{

    AsciiPanel terminal;

    public StartScene(AsciiPanel terminal) {
        this.terminal = terminal;
    }

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        if(inputEvent instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) inputEvent;
            if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                return SceneChangeEvent.builder()
                        .scene(new GameScene(terminal)).build();
            }
        }
        return null;
    }

    public void draw() {
        terminal.writeCenter("PRESS ENTER PLEASE!", 30);
    }
}
