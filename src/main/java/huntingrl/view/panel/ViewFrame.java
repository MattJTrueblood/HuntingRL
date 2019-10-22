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
    long offsetWorldX;
    long offsetWorldY;
    short tileSize; //length/height of tile in world coords.  Increase this to zoom out, decrease to zoom in.
    boolean localFrame; //entities will only be loaded on chunks in a local frame.  Only 1 local frame at a time please!
    int terrainColorModSoftness;
}
