package huntingrl.view.menu;

import huntingrl.view.SceneChangeEvent;
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