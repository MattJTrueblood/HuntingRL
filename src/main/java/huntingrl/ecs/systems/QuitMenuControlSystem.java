package huntingrl.ecs.systems;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.EntitySystem;
import huntingrl.scene.SceneChangeEvent;
import huntingrl.scene.menu.QuitScene;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class QuitMenuControlSystem extends EntitySystem implements InputHandlerSystem {

    private static final int ESC_KEYCODE = KeyEvent.VK_ESCAPE;

    private AsciiPanel terminal;

    public QuitMenuControlSystem(AsciiPanel terminal) {
        this.terminal = terminal;
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        if(event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            if(keyEvent.getKeyCode() == ESC_KEYCODE) {
                return SceneChangeEvent.builder()
                        .scene(new QuitScene(terminal))
                        .deleteOldScene(false)
                        .build();
            }
        }
        return null;
    }
}
