package huntingrl.view;

import java.awt.event.InputEvent;

public interface Scene {
    public SceneChangeEvent receiveInput(InputEvent inputEvent);
    public void init();
    public void draw();
}
