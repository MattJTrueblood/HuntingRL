package huntingrl.world;

import huntingrl.util.Math.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WorldPoint {
    Point coords;
    private short elevation;
}
