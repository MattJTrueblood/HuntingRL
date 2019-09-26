package huntingrl.ecs.systems;

import huntingrl.scene.SceneChangeEvent;

import java.awt.event.InputEvent;

public interface InputHandlerSystem {
    public SceneChangeEvent receiveInput(InputEvent event);
}
