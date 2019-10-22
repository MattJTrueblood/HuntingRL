package huntingrl.view.panel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ViewFrameProperties {
    short tileSize; //length/height of tile in world coords.  Increase this to zoom out, decrease to zoom in.
    boolean localFrame; //entities will only be loaded on chunks in a local frame.  Only 1 local frame at a time please!
    int terrainColorModPositiveSoftness;  //must be 1 or greater
    int terrainColorModNegativeSoftness;  //must be 1 or greater
}
