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
    private int x;
    private int y;
}
