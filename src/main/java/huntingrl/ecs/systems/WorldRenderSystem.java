package huntingrl.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import huntingrl.ecs.components.ViewFrameComponent;
import huntingrl.ecs.components.WorldComponent;
import huntingrl.view.RenderBuffer;
import huntingrl.world.WorldPoint;

import java.awt.*;

public class WorldRenderSystem extends EntitySystem {

    private WorldComponent worldComponent;
    private ViewFrameComponent viewFrameComponent;

    private RenderBuffer buffer;

    public WorldRenderSystem(int priority, RenderBuffer buffer) {
        super(priority);
        this.buffer = buffer;
    }

    public void addedToEngine(Engine engine) {
        worldComponent = engine.getEntitiesFor(Family.all(WorldComponent.class).get()).first().getComponent(WorldComponent.class);
        viewFrameComponent = engine.getEntitiesFor(Family.all(ViewFrameComponent.class).get()).first().getComponent(ViewFrameComponent.class);
    }

    public void update(float deltaTime) {
        for(int i = 0; i < viewFrameComponent.getPanelBounds().getWidth(); i++) {
            for (int j = 0; j < viewFrameComponent.getPanelBounds().getHeight(); j++) {
                //render world tile
                WorldPoint pointAtIJ = worldComponent.getWorld()
                        .pointAt(viewFrameComponent.getOffsetWorldX() + (viewFrameComponent.getTileSize() * i),
                                viewFrameComponent.getOffsetWorldY() + (viewFrameComponent.getTileSize() * j));
                int pointElevationFactor = pointAtIJ.getElevation();
                Color terrainColor = pointElevationFactor < 50
                        ? new Color(0, 0, 255)
                        : new Color(0, pointElevationFactor, 0);

                buffer.write((char) 0, viewFrameComponent.getPanelBounds().getX() + i,
                        viewFrameComponent.getPanelBounds().getY() + j,
                        Color.GRAY, terrainColor);
            }
        }
    }
}
