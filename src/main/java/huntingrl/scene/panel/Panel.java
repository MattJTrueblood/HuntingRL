package huntingrl.scene.panel;

import huntingrl.scene.SceneChangeEvent;

import java.awt.event.InputEvent;

/**
 * A panel is a component of a complex scene that can respond to input and draw text to the terminal.  The intention
 * is to break up functionality of scenes into smaller parts, such that different scenes can share common functionality
 */
public interface Panel {
    public SceneChangeEvent receiveInput(InputEvent event);
    public void draw();
}
