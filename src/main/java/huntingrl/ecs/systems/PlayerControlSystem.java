package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.PlayerComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.scene.SceneChangeEvent;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PlayerControlSystem extends EntitySystem implements InputHandlerSystem {
    private static final int UP_KEYCODE = KeyEvent.VK_UP;
    private static final int DOWN_KEYCODE = KeyEvent.VK_DOWN;
    private static final int LEFT_KEYCODE = KeyEvent.VK_LEFT;
    private static final int RIGHT_KEYCODE = KeyEvent.VK_RIGHT;

    private ImmutableArray<Entity> playerEntities;

    public void addedToEngine(Engine engine) {
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get());
    }

    public SceneChangeEvent receiveInput(InputEvent event) {
        for(Entity playerEntity: playerEntities) {
            if(event instanceof KeyEvent) {
                handleKeyInputForPlayer((KeyEvent) event, playerEntity);
            }
            else if (event instanceof MouseEvent) {
                handleMouseInputForPlayer((MouseEvent) event, playerEntity);
            }
        }
        return null;
    }

    private void handleKeyInputForPlayer(KeyEvent event, Entity playerEntity) {
        switch(event.getKeyCode()) {
            case UP_KEYCODE:
                movePlayer( playerEntity, 0, -1);
                break;
            case DOWN_KEYCODE:
                movePlayer( playerEntity, 0, 1);
                break;
            case LEFT_KEYCODE:
                movePlayer( playerEntity, -1, 0);
                break;
            case RIGHT_KEYCODE:
                movePlayer( playerEntity, 1, 0);
        }
    }

    private void movePlayer(Entity playerEntity, int dx, int dy) {
        PositionComponent positionComponent = ComponentMappers.positionMapper.get(playerEntity);
        positionComponent.setX(positionComponent.getX() + dx);
        positionComponent.setY(positionComponent.getY() + dy);
    }

    private void handleMouseInputForPlayer(MouseEvent event, Entity playerEntity) {
        //none right now
    }

}
