package huntingrl.ecs.systems;

import asciiPanel.AsciiPanel;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import huntingrl.ecs.ComponentMappers;
import huntingrl.ecs.components.GraphicsComponent;
import huntingrl.ecs.components.PositionComponent;

public class RenderSystem extends IteratingSystem {

    private AsciiPanel terminal;

    public RenderSystem(AsciiPanel terminal) {
        super(Family.all(PositionComponent.class, GraphicsComponent.class).get());
        this.terminal = terminal;
    }

    public void processEntity(Entity entity, float DeltaTime) {
        PositionComponent position = ComponentMappers.positionMapper.get(entity);
        GraphicsComponent graphics = ComponentMappers.graphicsMapper.get(entity);
        terminal.write(graphics.character, position.x, position.y, graphics.fgColor, graphics.bgColor);
    }
}


