package huntingrl.ecs.systems;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.components.ViewFrameComponent;

public class RenderSystem extends IteratingSystem {

    private ViewFrameComponent viewFrame;

    private AsciiPanel terminal;

    public RenderSystem(int priority, AsciiPanel terminal) {
        super(Family.all(PositionComponent.class, GraphicsComponent.class).get(), priority);
        this.terminal = terminal;
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
                terminal.write(graphics.getCharacter(),
                        viewFrame.getPanelBounds().getX() + (int) ((position.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                        viewFrame.getPanelBounds().getY() + (int) ((position.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                        graphics.getFgColor());
            } else {
                terminal.write(graphics.getCharacter(),
                        viewFrame.getPanelBounds().getX() + (int) ((position.getX() - viewFrame.getOffsetWorldX()) / viewFrame.getTileSize()),
                        viewFrame.getPanelBounds().getY() + (int) ((position.getY() - viewFrame.getOffsetWorldY()) / viewFrame.getTileSize()),
                        graphics.getFgColor(), graphics.getBgColor());
            }
        }

    }
}


