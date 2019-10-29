package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.PlayerComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.view.panel.ViewFrame;

import java.util.ArrayList;
import java.util.List;

public class FrameSystem extends EntitySystem {

    private Entity playerEntity;

    public void addedToEngine(Engine engine) {
        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get()).first();
    }

    public void updateViewFrameOffset(ViewFrame viewFrame) {
        centerViewFrameOnPlayer(viewFrame);
    }

    private void centerViewFrameOnPlayer(ViewFrame viewFrame) {
        PositionComponent positionComponent = ComponentMappers.positionMapper.get(playerEntity);
        centerViewFrameOnLocation(viewFrame, positionComponent.getX(), positionComponent.getY());
    }

    private void centerViewFrameOnLocation(ViewFrame viewFrame, long playerX, long playerY) {
        viewFrame.setOffsetWorldX(playerX - ((viewFrame.getPanelBounds().getWidth() * viewFrame.getTileSize()) / 2));
        viewFrame.setOffsetWorldY(playerY - ((viewFrame.getPanelBounds().getHeight() * viewFrame.getTileSize()) / 2));
    }
}
