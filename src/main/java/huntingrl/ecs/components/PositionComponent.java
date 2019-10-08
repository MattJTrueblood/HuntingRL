package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class PositionComponent implements Component {
    //x and y are in world coords
    private long x;
    private long y;
}
