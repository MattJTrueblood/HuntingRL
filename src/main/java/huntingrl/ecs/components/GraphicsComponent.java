package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.awt.*;

@AllArgsConstructor
@Builder
public class GraphicsComponent implements Component {
    public char character;
    public Color fgColor;
    public Color bgColor;
}
