package huntingrl.world;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WorldPoint {
    WorldCoord coords;
    private short elevation;
}
