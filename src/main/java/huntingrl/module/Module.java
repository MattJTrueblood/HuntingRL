package huntingrl.module;

import asciiPanel.AsciiPanel;
import huntingrl.scene.SceneChangeEvent;

import java.awt.event.InputEvent;

public interface Module {
    public SceneChangeEvent receiveInput(InputEvent event);
    public void draw(AsciiPanel terminal);
}
