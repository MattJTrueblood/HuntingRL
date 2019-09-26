package huntingrl.scene;

import lombok.Builder;

@Builder
public class SceneChangeEvent {
    @Builder.Default
    public Scene scene = null;
    @Builder.Default
    public boolean saveOldScene = false;
    @Builder.Default
    public boolean goToSavedOldScene = false;
    @Builder.Default
    public boolean quitApplication = false;
}
