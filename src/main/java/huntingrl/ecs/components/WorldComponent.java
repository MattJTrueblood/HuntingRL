package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import huntingrl.world.World;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorldComponent implements Component {
    private World world;
}
