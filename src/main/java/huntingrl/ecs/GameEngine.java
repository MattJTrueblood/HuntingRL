package huntingrl.ecs;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import huntingrl.ecs.components.*;
import huntingrl.ecs.systems.InputSystem;
import huntingrl.ecs.systems.RenderSystem;
import huntingrl.ecs.systems.WorldRenderSystem;
import huntingrl.view.SceneChangeEvent;
import huntingrl.view.panel.PanelBounds;
import huntingrl.world.World;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {

    private Engine gameEngine;
    private PanelBounds bounds;

    public GameEngine(AsciiPanel terminal, PanelBounds bounds) {
        this.bounds = bounds;
        gameEngine = new Engine();
        addViewFrame();
        addWorld();
        addPlayer();
        gameEngine.addSystem(new WorldRenderSystem(100,terminal));
        gameEngine.addSystem(new RenderSystem(101, terminal));
        gameEngine.addSystem(new InputSystem(terminal));
    }

    private void addWorld() {
        Entity world = new Entity();
        world.add(new WorldComponent(new World(ThreadLocalRandom.current().nextInt())));
        gameEngine.addEntity(world);
    }

    private void addViewFrame() {
        Entity viewFrame = new Entity();
        viewFrame.add(new ViewFrameComponent(bounds, 0, 0, 100));
        gameEngine.addEntity(viewFrame);
    }

    private void addPlayer() {
        Entity player = new Entity();
        player.add(GraphicsComponent.builder()
                .character((char) 64)
                .fgColor(Color.YELLOW)
                .build());
        player.add(new PlayerComponent());
        player.add(PositionComponent.builder().x(50).y(50).build());
        gameEngine.addEntity(player);
    }

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        return gameEngine.getSystem(InputSystem.class).receiveInput(inputEvent);
    }

    public void render() {
        //Engine update will call the RenderSystem to render the world
        this.gameEngine.update(1);
    }
}
