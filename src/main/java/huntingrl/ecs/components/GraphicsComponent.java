package huntingrl.ecs.components;

import com.badlogic.ashley.core.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class GraphicsComponent implements Component {
    private char character;
    private Color fgColor;
    private Color bgColor;
    private short zIndex;
}
