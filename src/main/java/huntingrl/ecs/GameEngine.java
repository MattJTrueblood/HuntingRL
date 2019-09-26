package huntingrl.ecs;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PlayerComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.systems.InputSystem;
import huntingrl.ecs.systems.RenderSystem;
import huntingrl.view.SceneChangeEvent;
import huntingrl.view.panel.PanelBounds;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {

    private Engine gameEngine;
    private PanelBounds bounds;

    public GameEngine(AsciiPanel terminal, PanelBounds bounds) {
        this.bounds = bounds;
        gameEngine = new Engine();
        gameEngine.addSystem(new RenderSystem(terminal, bounds));
        gameEngine.addSystem(new InputSystem(terminal));
        addPlayer();
        addABunchOfTrees();
    }

    private void addPlayer() {
        Entity player = new Entity();
        player.add(GraphicsComponent.builder()
                .character((char) 64)
                .bgColor(Color.BLACK)
                .fgColor(Color.YELLOW)
                .build());
        player.add(new PlayerComponent());
        player.add(PositionComponent.builder().x(bounds.getWidth() / 2).y(bounds.getHeight() / 2).build());
        gameEngine.addEntity(player);
    }

    private void addABunchOfTrees() {
        for(int i = 0; i < 100; i++) {
            Entity tree = new Entity();
            tree.add(GraphicsComponent.builder()
                    .character((char) 5)
                    .bgColor(Color.BLACK)
                    .fgColor(Color.GREEN)
                    .build());
            tree.add(PositionComponent.builder()
                    .x(ThreadLocalRandom.current().nextInt(bounds.getX(), bounds.getWidth() - 1))
                    .y(ThreadLocalRandom.current().nextInt(bounds.getY(), bounds.getHeight() - 1))
                    .build());
            gameEngine.addEntity(tree);
        }
    }

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        return gameEngine.getSystem(InputSystem.class).receiveInput(inputEvent);
    }

    public void render() {
        //Engine update will call the RenderSystem to render the world
        this.gameEngine.update(1);
    }
}
