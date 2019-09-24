package huntingrl.view.scene;

import asciiPanel.AsciiPanel;

import java.awt.event.InputEvent;

public interface Scene {
    public SceneChangeEvent receiveInput(InputEvent inputEvent);
    public void draw(AsciiPanel terminal);
}
