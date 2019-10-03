package huntingrl.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.components.ViewFrameComponent;
import huntingrl.view.RenderBuffer;

import java.awt.*;

public class RenderSystem extends IteratingSystem {

    private ViewFrameComponent viewFrame;

    private RenderBuffer buffer;

    public RenderSystem(int priority, RenderBuffer buffer) {
        super(Family.all(PositionComponent.class, GraphicsComponent.class).get(), priority);
        this.buffer = buffer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        viewFrame = engine.getEntitiesFor(Family.all(ViewFrameComponent.class).get()).first().getComponent(ViewFrameComponent.class);
    }

    public void processEntity(Entity entity, float DeltaTime) {
        PositionComponent position = ComponentMappers.positionMapper.get(entity);
        GraphicsComponent graphics = ComponentMappers.graphicsMapper.get(entity);

        if(position.getX() >= viewFrame.getOffsetWorldX() &&
            position.getY() >= viewFrame.getOffsetWorldY() &&
            position.getX() < viewFrame.getOffsetWorldX() + (viewFrame.getPanelBounds().getWidth() * viewFrame.getTileSize()) &&
            position.getY() < viewFrame.getOffsetWorldY() + (viewFrame.getPanelBounds().getHeight() * viewFrame.getTileSize())) {
            //Entity is in frame, render it.  Make sure to convert properly between screen coords and world coords!!!
            if(graphics.getBgColor() == null) {
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


