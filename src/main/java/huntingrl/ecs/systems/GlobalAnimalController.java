package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import huntingrl.ecs.components.*;

public class GlobalAnimalController extends EntitySystem {
    private static final double MAX_ANIMAL_DENSITY_PER_ACRE = 0.125; // 1/8
    private static final double MIN_ANIMAL_DENSITY_PER_ACRE = 0.008; // 1/25

    //1 acre = approx. 4048 meters, or 64x64 chunk.
    private static final long ACRE_CHUNK_SIZE = 64;

    WorldComponent worldComponent;

    public void addedToEngine(Engine engine) {
        worldComponent = engine.getEntitiesFor(Family.all(WorldComponent.class).get()).first().getComponent(WorldComponent.class);
        spawnInitialAnimals();
    }

    private void spawnInitialAnimals() {
        //divide world into acre chunks
        //for(int i = 0;)
    }

}
