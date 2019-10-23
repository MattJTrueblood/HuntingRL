package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import huntingrl.util.Math.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WanderAIComponent implements Component {
    final int movementSpeed;
    int turnsUntilNextWander;
    final int minWaitTurns;
    final int maxWaitTurns;
    final int minWanderDistance;
    final int maxWanderDistance;
    List<Point> currentPath;
}
