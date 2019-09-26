package huntingrl.scene.panel;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PlayerComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.systems.PlayerControlSystem;
import huntingrl.ecs.systems.QuitMenuControlSystem;
import huntingrl.ecs.systems.RenderSystem;
import huntingrl.scene.SceneChangeEvent;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The main view of the game, displaying the local world around the player.
 */
public class MainGamePanel extends DrawPanel {

    private Engine gameEngine;

    public MainGamePanel(AsciiPanel terminal, int x, int y, int width, int height) {
        super(terminal, x, y, width, height, 0);
        init();
    }

    private void init() {
        gameEngine = new Engine();
        gameEngine.addSystem(new RenderSystem(this.getTerminal()));
        gameEngine.addSystem(new QuitMenuControlSystem(this.getTerminal()));
        gameEngine.addSystem(new PlayerControlSystem());
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
        player.add(PositionComponent.builder().x(getWidth() / 2).y(getHeight() / 2).build());
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
                    .x(ThreadLocalRandom.current().nextInt(getX(), getWidth() - 1))
                    .y(ThreadLocalRandom.current().nextInt(getY(), getHeight() - 1))
                    .build());
            gameEngine.addEntity(tree);
        }
    }

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        gameEngine.getSystem(PlayerControlSystem.class).receiveInput(inputEvent);
        //TODO:  figure out how to handle multiple systems that can call scene changes
        return gameEngine.getSystem(QuitMenuControlSystem.class).receiveInput(inputEvent);
    }

    public void draw() {
        //Engine update will call the RenderSystem to draw the world
        this.gameEngine.update(1);
    }
}
