package huntingrl.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.LocalOnlyComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.components.WorldComponent;
import huntingrl.view.RenderBuffer;
import huntingrl.view.panel.ViewFrame;
import huntingrl.world.World;
import huntingrl.world.WorldPoint;

import java.awt.*;

public class RenderSystem extends EntitySystem {

    private ImmutableArray<Entity> renderableEntities;
    private WorldChunkingSystem worldChunkingSystem;

    private RenderBuffer buffer;

    public RenderSystem(RenderBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        renderableEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, GraphicsComponent.class).get());
        worldChunkingSystem = engine.getSystem(WorldChunkingSystem.class);
    }

    public void renderInView(ViewFrame viewFrame) {
        renderWorldInView(viewFrame);
        renderEntitiesInView(viewFrame);
    }
    
    private void renderWorldInView(ViewFrame viewFrame) {
        WorldPoint[][] worldPointsInFrame = worldChunkingSystem.retrieveWorldPointsInFrame(viewFrame);
        for(int i = 0; i < viewFrame.getPanelBounds().getWidth(); i++) {
            for(int j = 0; j < viewFrame.getPanelBounds().getHeight(); j++) {
                int pointElevationFactor = worldPointsInFrame[i][j].getElevation();
                Color terrainColor = pointElevationFactor < World.WATER_ELEVATION
                        ? new Color(0, 64, 200)
                        : new Color(0, pointElevationFactor, 0);

                buffer.write((char) 0, viewFrame.getPanelBounds().getX() + i,
                        viewFrame.getPanelBounds().getY() + j,
                        Color.GRAY, terrainColor);
            }
        }
    }

    private void renderEntitiesInView(ViewFrame viewFrame) {
        for(Entity entity: renderableEntities) {
            PositionComponent position = ComponentMappers.positionMapper.get(entity);
            GraphicsComponent graphics = ComponentMappers.graphicsMapper.get(entity);

            if(!viewFrame.isLocalFrame() && ComponentMappers.localOnlyMapper.get(entity) != null) {
                continue;
            }

            if (position.getX() >= viewFrame.getOffsetWorldX() &&
                    position.getY() >= viewFrame.getOffsetWorldY() &&
                    position.getX() < viewFrame.getOffsetWorldX() + (viewFrame.getPanelBounds().getWidth() * viewFrame.getTileSize()) &&
                    position.getY() < viewFrame.getOffsetWorldY() + (viewFrame.getPanelBounds().getHeight() * viewFrame.getTileSize())) {
                //Entity is in frame, render it.  Make sure to convert properly between screen coords and world coords!!!
                if (graphics.getBgColor() == null) {
                    buffer.write(graphics.getCharacter(),
                            viewFrame.getPanelBounds().getX() + (int) ((position.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                            viewFrame.getPanelBounds().getY() + (int) ((position.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                            graphics.getFgColor(), new Color(0f, 0f, 0f, 0f));
                } else {
                    buffer.write(graphics.getCharacter(),
                            viewFrame.getPanelBounds().getX() + (int) ((position.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                            viewFrame.getPanelBounds().getY() + (int) ((position.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                            graphics.getFgColor(), graphics.getBgColor());
                }
            }
        }
    }
}


