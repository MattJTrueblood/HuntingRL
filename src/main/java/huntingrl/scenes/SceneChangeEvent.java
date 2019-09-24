package huntingrl.scenes;

import lombok.Builder;

@Builder
public class SceneChangeEvent {
    @Builder.Default
    public Scene scene = null;
    @Builder.Default
    public boolean deleteOldScene = true;
    @Builder.Default
    public boolean goToOldScene = false;
}
