package huntingrl.scene.menu;

import huntingrl.scene.SceneChangeEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MenuItem {
    public String text;
    public SceneChangeEvent sceneChangeEvent;
}