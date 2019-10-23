package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class PositionComponent implements Component {
    //x and y are in world coords
    private long x;
    private long y;
}
