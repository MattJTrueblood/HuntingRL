package huntingrl.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PlayerComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.components.WorldComponent;
import huntingrl.ecs.systems.FrameSystem;
import huntingrl.ecs.systems.InputSystem;
import huntingrl.ecs.systems.RenderSystem;
import huntingrl.view.RenderBuffer;
import huntingrl.view.SceneChangeEvent;
import huntingrl.view.panel.ViewFrame;
import huntingrl.world.World;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {

    private Engine gameEngine;

    public GameEngine(RenderBuffer buffer) {
        gameEngine = new Engine();
        addWorld();
        addPlayer();
        gameEngine.addSystem(new RenderSystem(buffer));
        gameEngine.addSystem(new InputSystem(buffer));
        gameEngine.addSystem(new FrameSystem());
    }

    private void addWorld() {
        Entity world = new Entity();
        world.add(new WorldComponent(new World(ThreadLocalRandom.current().nextInt())));
        gameEngine.addEntity(world);
    }

    private void addPlayer() {
        Entity player = new Entity();
        player.add(GraphicsComponent.builder()
                .character((char) 2)
                .fgColor(new Color(255, 255, 0, 255))
                .build());
        player.add(new PlayerComponent());
        player.add(PositionComponent.builder().x(50).y(50).build());
        gameEngine.addEntity(player);
    }

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        return gameEngine.getSystem(InputSystem.class).receiveInput(inputEvent);
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
