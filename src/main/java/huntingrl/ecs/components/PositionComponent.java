package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class PositionComponent implements Component {
    public int x;
    public int y;
}
