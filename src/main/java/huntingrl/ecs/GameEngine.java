package huntingrl.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import huntingrl.ecs.components.*;
import huntingrl.ecs.systems.*;
import huntingrl.view.RenderBuffer;
import huntingrl.view.SceneChangeEvent;
import huntingrl.view.panel.ViewFrame;
import huntingrl.world.World;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {

    private Engine gameEngine;

    public GameEngine(RenderBuffer buffer) {
        gameEngine = new Engine();
        addWorld();
        addPlayer();
        gameEngine.addSystem(new WorldChunkingSystem());
        gameEngine.addSystem(new RenderSystem(buffer));
        gameEngine.addSystem(new InputSystem(buffer));
        gameEngine.addSystem(new FrameSystem());
        gameEngine.addSystem(new AIControllerSystem());
        addACoupleOfDeer();
    }

    private void addACoupleOfDeer() {
        for(int i = 0; i < 3; i++) {
            Entity deer = new Entity();
            deer.add(new PositionComponent(
                    ThreadLocalRandom.current().nextLong(-10, 10),
                    ThreadLocalRandom.current().nextLong(-10, 10)));
            deer.add(new GraphicsComponent(
                    (char) 100,
                    new Color(235, 177, 54),
                    null, (short) 10));
            deer.add(WanderAIComponent.builder()
                    .currentPath(new ArrayDeque<>())
                    .maxWaitTurns(20)
                    .minWaitTurns(10)
                    .maxWanderDistance(20)
                    .minWanderDistance(10)
                    .movementSpeed(1)
                    .turnsUntilNextWander(5)
                    .build());
            deer.add(new LocalOnlyComponent());
            deer.add(new BlocksMovementComponent());
            gameEngine.addEntity(deer);
        }
    }

    private void addWorld() {
        Entity world = new Entity();
        world.add(new WorldComponent(new World()));
        gameEngine.addEntity(world);
    }

    private void addPlayer() {
        Entity player = new Entity();
        player.add(GraphicsComponent.builder()
                .character((char) 2)
                .fgColor(new Color(255, 255, 0, 255))
                .zIndex((short) 100)
                .build());
        player.add(new PlayerComponent());
        player.add(PositionComponent.builder().x(World.WORLD_WIDTH / 2).y(World.WORLD_HEIGHT / 2).build());
        gameEngine.addEntity(player);
    }

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        SceneChangeEvent event = gameEngine.getSystem(InputSystem.class).receiveInput(inputEvent);
        if(event == null) {
            update(1);
        }
        return event;
    }

    public void renderInView(ViewFrame viewFrame) {
        //Engine update will call the RenderSystem to render the world
        gameEngine.getSystem(FrameSystem.class).updateViewFrameOffset(viewFrame);
        RenderSystem renderSystem = gameEngine.getSystem(RenderSystem.class);
        renderSystem.renderInView(viewFrame);
    }

    public void update(float deltaTime) {
        gameEngine.update(deltaTime);
    }
}
