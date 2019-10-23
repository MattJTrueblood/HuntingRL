package huntingrl.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import huntingrl.ecs.components.*;

public class ComponentMappers {
    public static final ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<GraphicsComponent> graphicsMapper = ComponentMapper.getFor(GraphicsComponent.class);
    public static final ComponentMapper<LocalOnlyComponent> localOnlyMapper = ComponentMapper.getFor(LocalOnlyComponent.class);
    public static final ComponentMapper<CastsShadowComponent> castsShadowMapper = ComponentMapper.getFor(CastsShadowComponent.class);
    public static final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);

}
