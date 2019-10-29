package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.BlocksMovementComponent;
import huntingrl.ecs.components.LocalOnlyComponent;
import huntingrl.ecs.components.PlayerComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.view.RenderBuffer;
import huntingrl.view.SceneChangeEvent;
import huntingrl.view.menu.QuitScene;
import javafx.geometry.Pos;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputSystem extends EntitySystem {
    private static final int UP_KEYCODE = KeyEvent.VK_UP;
    private static final int DOWN_KEYCODE = KeyEvent.VK_DOWN;
    private static final int LEFT_KEYCODE = KeyEvent.VK_LEFT;
    private static final int RIGHT_KEYCODE = KeyEvent.VK_RIGHT;
    private static final int QUIT_KEYCODE = KeyEvent.VK_ESCAPE;

    private static final short PLAYER_MOVEMENT_DISTANCE = 256;

    private Entity playerEntity;
    private ImmutableArray<Entity> blockingEntities;

    private RenderBuffer buffer;

    public InputSystem(RenderBuffer buffer) {
        this.buffer = buffer;
    }

    public void addedToEngine(Engine engine) {
        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get()).first();
        blockingEntities = engine.getEntitiesFor(Family.all(LocalOnlyComponent.class,
                BlocksMovementComponent.class, PositionComponent.class).get());
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
        long newPlayerX = positionComponent.getX() + (dx * PLAYER_MOVEMENT_DISTANCE);
        long newPlayerY = positionComponent.getY() + (dy * PLAYER_MOVEMENT_DISTANCE);
        if(playerMovementIsValid(newPlayerX, newPlayerY)) {
            positionComponent.setX(newPlayerX);
            positionComponent.setY(newPlayerY);
        }
        System.out.println(newPlayerX + ", " + newPlayerY);
    }

    private boolean playerMovementIsValid(long x, long y) {
        for(Entity blockingEntity : blockingEntities) {
            PositionComponent blockerPosition = ComponentMappers.positionMapper.get(blockingEntity);
            if(blockerPosition.getX() == x && blockerPosition.getY() == y) {
                return false;
            }
        }
        return true;
    }

    private SceneChangeEvent createQuitMenuSceneEvent() {
        return SceneChangeEvent.builder()
                .scene(new QuitScene(buffer))
                .saveOldScene(true)
                .build();
    }

    private SceneChangeEvent handleMouseInputForPlayer(MouseEvent event) {
        //none right now
        return null;
    }

}
