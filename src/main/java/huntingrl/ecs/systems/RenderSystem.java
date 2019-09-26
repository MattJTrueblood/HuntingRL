package huntingrl.ecs.systems;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PositionComponent;
import huntingrl.view.panel.PanelBounds;

public class RenderSystem extends IteratingSystem {

    private AsciiPanel terminal;
    private PanelBounds bounds;

    public RenderSystem(AsciiPanel terminal, PanelBounds bounds) {
        super(Family.all(PositionComponent.class, GraphicsComponent.class).get());
        this.terminal = terminal;
        this.bounds = bounds;
    }

    public void processEntity(Entity entity, float DeltaTime) {
        PositionComponent position = ComponentMappers.positionMapper.get(entity);
        GraphicsComponent graphics = ComponentMappers.graphicsMapper.get(entity);
        terminal.write(graphics.getCharacter(), position.getX(), position.getY(), graphics.getFgColor(), graphics.getBgColor());
    }
}


