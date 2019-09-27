package huntingrl.ecs.systems;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.components.ViewFrameComponent;
import huntingrl.ecs.components.WorldComponent;
import huntingrl.world.WorldPoint;

import java.awt.*;

public class WorldRenderSystem extends EntitySystem {

    private WorldComponent worldComponent;
    private ViewFrameComponent viewFrameComponent;

    private AsciiPanel terminal;

    public WorldRenderSystem(int priority, AsciiPanel terminal) {
        super(priority);
        this.terminal = terminal;
    }

    public void addedToEngine(Engine engine) {
        worldComponent = engine.getEntitiesFor(Family.all(WorldComponent.class).get()).first().getComponent(WorldComponent.class);
        viewFrameComponent = engine.getEntitiesFor(Family.all(ViewFrameComponent.class).get()).first().getComponent(ViewFrameComponent.class);
    }

    public void update(float deltaTime) {
        for(int i = 0; i < viewFrameComponent.getPanelBounds().getWidth(); i++) {
            for (int j = 0; j < viewFrameComponent.getPanelBounds().getHeight(); j++) {
                //out of world bounds check
                if(worldComponent.getWorld().pointOutOfBounds(i + viewFrameComponent.getOffsetX(), j + viewFrameComponent.getOffsetY())) {
                    terminal.write((char) 0, viewFrameComponent.getPanelBounds().getX() + i,
                            viewFrameComponent.getPanelBounds().getY() + j, Color.BLACK, Color.BLACK);
                } else {
                    //render world tile
                    WorldPoint pointAtIJ = worldComponent.getWorld()
                            .pointAt(i + viewFrameComponent.getOffsetX(), j + viewFrameComponent.getOffsetY());
                    int pointElevationFactor = pointAtIJ.getElevation();
                    Color terrainColor = pointElevationFactor < 50
                            ? new Color(0, 0, 255)
                            : new Color(0, pointElevationFactor, 0);

                    terminal.write((char) 0, viewFrameComponent.getPanelBounds().getX() + i,
                            viewFrameComponent.getPanelBounds().getY() + j,
                            Color.GRAY, terrainColor);
                }
            }
        }
    }
}
