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
    ViewFrameProperties properties;

    public short getTileSize() {
        return properties.tileSize;
    }

    public boolean isLocalFrame() {
        return properties.localFrame;
    }

    public int getTerrainColorModPositiveSoftness() {
        return properties.terrainColorModPositiveSoftness;
    }

    public int getTerrainColorModNegativeSoftness() {
        return properties.terrainColorModNegativeSoftness;
    }
}
