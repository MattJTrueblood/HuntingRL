package huntingrl.ecs.systems;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.ecs.components.ViewFrameComponent;
import huntingrl.view.panel.PanelBounds;

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

        if(position.getX() >= viewFrame.getOffsetX() &&
            position.getY() >= viewFrame.getOffsetY() &&
            position.getX() < viewFrame.getOffsetX() + viewFrame.getPanelBounds().getWidth() &&
            position.getY() < viewFrame.getOffsetY() + viewFrame.getPanelBounds().getHeight()) {
            //Entity is in frame, render it
            terminal.write(graphics.getCharacter(),
                    position.getX() + viewFrame.getPanelBounds().getX() - viewFrame.getOffsetX(),
                    position.getY() + viewFrame.getPanelBounds().getY() - viewFrame.getOffsetY(),
                    graphics.getFgColor(), graphics.getBgColor());
        }

    }
}


