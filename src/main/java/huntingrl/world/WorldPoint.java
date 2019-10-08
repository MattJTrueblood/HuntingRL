package huntingrl.world;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WorldPoint {
    private long x;
    private long y;
    private short elevation;
}
