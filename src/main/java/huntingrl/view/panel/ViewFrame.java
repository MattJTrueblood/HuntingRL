package huntingrl.view.panel;

import huntingrl.view.panel.PanelBounds;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ViewFrame {
    PanelBounds panelBounds;
    double offsetWorldX;
    double offsetWorldY;
    double tileSize; //length/height of tile in world coords.  Increase this to zoom out, decrease to zoom in.
}
