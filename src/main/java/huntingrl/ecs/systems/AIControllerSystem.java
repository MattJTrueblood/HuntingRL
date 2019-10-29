package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.*;
import huntingrl.util.Math.MathUtils;
import huntingrl.util.Math.Point;

import java.util.ArrayDeque;
import java.util.Arrays;
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
        WanderAIComponent wanderAI = ComponentMappers.wanderAIMapper.get(entity);

        if(!wanderAI.getCurrentPath().isEmpty()) {
            walkAlongPath(wanderAI, entity);
        }
        else if(wanderAI.getTurnsUntilNextWander() == 0) {
            beginNewPath(wanderAI, entity);
        }
        else {
            decrementWaitCounter(wanderAI);
        }
    }

    private void walkAlongPath(WanderAIComponent wanderAI, Entity entity) {
        Point nextPoint = wanderAI.getCurrentPath().removeFirst();
        boolean success = attemptMove(entity, nextPoint.getX(), nextPoint.getY());
        if(wanderAI.getCurrentPath().isEmpty() && success) {
            wanderAI.setTurnsUntilNextWander(ThreadLocalRandom.current().nextInt(
                    wanderAI.getMinWaitTurns(), wanderAI.getMaxWaitTurns() + 1));
            wanderAI.setCurrentPath(new ArrayDeque<>());
        }
    }

    private void beginNewPath(WanderAIComponent wanderAI, Entity entity) {
        PositionComponent position = ComponentMappers.positionMapper.get(entity);
        long goalX = position.getX() + ThreadLocalRandom.current().nextLong(
                wanderAI.getMinWanderDistance(), wanderAI.getMaxWanderDistance() * 2)
                - ((wanderAI.getMaxWanderDistance() + wanderAI.getMinWanderDistance()) / 2);
        long goalY = position.getY() + ThreadLocalRandom.current().nextLong(
                wanderAI.getMinWanderDistance(), wanderAI.getMaxWanderDistance() * 2)
                - ((wanderAI.getMaxWanderDistance() + wanderAI.getMinWanderDistance()) / 2);
        Point[] line = MathUtils.bresenhamLine(new Point(position.getX(), position.getY()),
                new Point(goalX, goalY));
        wanderAI.setCurrentPath(new ArrayDeque<>(Arrays.asList(line)));
        wanderAI.getCurrentPath().removeFirst(); //first point overlaps with self.
    }

    private void decrementWaitCounter(WanderAIComponent wanderAI) {
        wanderAI.setTurnsUntilNextWander(wanderAI.getTurnsUntilNextWander() - 1);
    }

    private boolean attemptMove(Entity entity, long newX, long newY) {
        PositionComponent position = ComponentMappers.positionMapper.get(entity);
        if(moveIsValid(newX, newY)) {
            position.setX(newX);
            position.setY(newY);
            return true;
        }
        else {
            WanderAIComponent wanderAI = ComponentMappers.wanderAIMapper.get(entity);
            wanderAI.setCurrentPath(new ArrayDeque<>());
            wanderAI.setTurnsUntilNextWander(0);
            return false;
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
