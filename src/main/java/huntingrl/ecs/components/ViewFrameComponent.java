package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import huntingrl.view.panel.PanelBounds;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ViewFrameComponent implements Component {
    PanelBounds panelBounds;
    int offsetX;
    int offsetY;
}
