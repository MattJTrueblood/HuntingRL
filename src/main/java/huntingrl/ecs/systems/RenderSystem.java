package huntingrl.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.*;
import huntingrl.view.RenderBuffer;
import huntingrl.view.panel.ViewFrame;
import huntingrl.world.World;
import huntingrl.world.WorldPoint;

import java.awt.*;

public class RenderSystem extends EntitySystem {

    private static final Color MAX_NEGATIVE_DELTA_ELEVATION_COLOR = new Color(180, 255, 255);
    private static final Color MAX_POSITIVE_DELTA_ELEVATION_COLOR = new Color(0, 0, 0);

    private ImmutableArray<Entity> renderableEntities;
    private Entity playerEntity;
    private WorldChunkingSystem worldChunkingSystem;

    private RenderBuffer buffer;

    public RenderSystem(RenderBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get()).first();
        renderableEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, GraphicsComponent.class).get());
        worldChunkingSystem = engine.getSystem(WorldChunkingSystem.class);
    }

    public void renderInView(ViewFrame viewFrame) {
        renderWorldInView(viewFrame);
        renderEntitiesInView(viewFrame);
    }
    
    private void renderWorldInView(ViewFrame viewFrame) {
        WorldPoint[][] worldPointsInFrame = worldChunkingSystem.retrieveWorldPointsInFrame(viewFrame);
        PositionComponent playerPosition = playerEntity.getComponent(PositionComponent.class);
        for(int i = 0; i < viewFrame.getPanelBounds().getWidth(); i++) {
            for(int j = 0; j < viewFrame.getPanelBounds().getHeight(); j++) {
                int elevationAtIJ = worldPointsInFrame[i][j].getElevation();
                boolean ijIsUnderwater = elevationAtIJ < World.WATER_ELEVATION;

                Color terrainColor = ijIsUnderwater
                        ? new Color(0, 64, 200)
                        : new Color(0, 100, 0);

                if(!ijIsUnderwater) {
                    int playerElevation = worldChunkingSystem.getWorldPointAt(playerPosition.getX(), playerPosition.getY(), viewFrame)
                            .getElevation();
                    int deltaElevation = elevationAtIJ - playerElevation;
                    if (deltaElevation > 0) {
                        terrainColor = modifyColorByDeltaElevation(terrainColor, MAX_POSITIVE_DELTA_ELEVATION_COLOR,
                                deltaElevation, viewFrame.getTerrainColorModSoftness());
                    } else if (deltaElevation < 0) {
                        terrainColor = modifyColorByDeltaElevation(terrainColor, MAX_NEGATIVE_DELTA_ELEVATION_COLOR,
                                Math.abs(deltaElevation), viewFrame.getTerrainColorModSoftness());
                    }
                }

                buffer.write((char) 0, viewFrame.getPanelBounds().getX() + i,
                        viewFrame.getPanelBounds().getY() + j,
                        Color.GRAY, terrainColor);
            }
        }
    }

    private Color modifyColorByDeltaElevation(Color baseColor, Color goalColor, int deltaElevation, int softness) {
       return new Color(
            ((baseColor.getRed() * softness) + (Math.abs(deltaElevation) * goalColor.getRed()))
                    / (1 + Math.abs(deltaElevation) + softness),
            ((baseColor.getGreen() * softness) + (Math.abs(deltaElevation) * goalColor.getGreen()))
                    / (1 + Math.abs(deltaElevation) + softness),
            ((baseColor.getBlue() * softness) + (Math.abs(deltaElevation) * goalColor.getBlue()))
                    / (1 + Math.abs(deltaElevation) + softness));
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


