package huntingrl.ecs.systems;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.PlayerComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.components.ViewFrameComponent;
import huntingrl.view.SceneChangeEvent;
import huntingrl.view.menu.QuitScene;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputSystem extends EntitySystem {
    private static final int UP_KEYCODE = KeyEvent.VK_UP;
    private static final int DOWN_KEYCODE = KeyEvent.VK_DOWN;
    private static final int LEFT_KEYCODE = KeyEvent.VK_LEFT;
    private static final int RIGHT_KEYCODE = KeyEvent.VK_RIGHT;
    private static final int QUIT_KEYCODE = KeyEvent.VK_ESCAPE;

    private Entity playerEntity;
    private ViewFrameComponent viewFrame;

    private AsciiPanel terminal;

    public InputSystem(AsciiPanel terminal) {
        this.terminal = terminal;
    }

    public void addedToEngine(Engine engine) {
        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get()).first();
        viewFrame = engine.getEntitiesFor(Family.all(ViewFrameComponent.class).get()).first().getComponent(ViewFrameComponent.class);
        centerViewFrameOnPlayer();
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        if(event instanceof KeyEvent) {
            return handleKeyInputForPlayer((KeyEvent) event);
        }
        else if (event instanceof MouseEvent) {
            return handleMouseInputForPlayer((MouseEvent) event);
        }
        return null;
    }

    private SceneChangeEvent handleKeyInputForPlayer(KeyEvent event) {
        switch(event.getKeyCode()) {
            case UP_KEYCODE:
                movePlayer(0, -1);
                break;
            case DOWN_KEYCODE:
                movePlayer(0, 1);
                break;
            case LEFT_KEYCODE:
                movePlayer(-1, 0);
                break;
            case RIGHT_KEYCODE:
                movePlayer(1, 0);
                break;
            case QUIT_KEYCODE:
                return createQuitMenuSceneEvent();
        }
        return null;
    }

    private void movePlayer(int dx, int dy) {
        PositionComponent positionComponent = ComponentMappers.positionMapper.get(playerEntity);
        positionComponent.setX(positionComponent.getX() + dx);
        positionComponent.setY(positionComponent.getY() + dy);
        centerViewFrameOnLocation(positionComponent.getX(), positionComponent.getY());
    }

    private void centerViewFrameOnLocation(int playerX, int playerY) {
        viewFrame.setOffsetX(playerX - (viewFrame.getPanelBounds().getWidth() / 2));
        viewFrame.setOffsetY(playerY - (viewFrame.getPanelBounds().getHeight() / 2));
    }

    private void centerViewFrameOnPlayer() {
        PositionComponent positionComponent = ComponentMappers.positionMapper.get(playerEntity);
        centerViewFrameOnLocation(positionComponent.getX(), positionComponent.getY());
    }

    private SceneChangeEvent createQuitMenuSceneEvent() {
        return SceneChangeEvent.builder()
                .scene(new QuitScene(terminal))
                .saveOldScene(true)
                .build();
    }

    private SceneChangeEvent handleMouseInputForPlayer(MouseEvent event) {
        //none right now
        return null;
    }

}
