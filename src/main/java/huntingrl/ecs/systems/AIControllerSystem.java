package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.*;

import java.util.concurrent.ThreadLocalRandom;

public class AIControllerSystem extends IteratingSystem {

    private ImmutableArray<Entity> aiEntities;
    private ImmutableArray<Entity> blockingEntities;

    public AIControllerSystem() {
        super(Family.all(WanderAIComponent.class, PositionComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        blockingEntities = engine.getEntitiesFor(Family.all(LocalOnlyComponent.class,
                BlocksMovementComponent.class, PositionComponent.class).get());
    }

    public void processEntity(Entity entity, float deltaTime) {
        //WanderAIComponent wanderAI = ComponentMappers.wanderAIMapper.get(entity);
        PositionComponent position = ComponentMappers.positionMapper.get(entity);
        long dx = ThreadLocalRandom.current().nextLong(3)-1;
        long dy = ThreadLocalRandom.current().nextLong(3)-1;
        long newAiX = position.getX() + dx;
        long newAiY = position.getY() + dy;
        if(moveIsValid(newAiX, newAiY)) {
            position.setX(newAiX);
            position.setY(newAiY);
        }
    }

    private boolean moveIsValid(long x, long y) {
        for(Entity blockingEntity : blockingEntities) {
            PositionComponent blockerPosition = ComponentMappers.positionMapper.get(blockingEntity);
            if(blockerPosition.getX() == x && blockerPosition.getY() == y) {
                return false;
            }
        }
        return true;
    }
}
