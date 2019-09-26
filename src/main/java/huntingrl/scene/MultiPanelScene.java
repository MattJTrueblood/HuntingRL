package huntingrl.scene;

import asciiPanel.AsciiPanel;
import huntingrl.scene.panel.DrawPanel;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class MultiPanelScene implements Scene {
    private List<DrawPanel> panels = new ArrayList<>();
    protected AsciiPanel terminal;

    public MultiPanelScene(AsciiPanel terminal) {
        this.terminal = terminal;
    }

    public void addPanel(DrawPanel panel) {
        panels.add(panel);
        sortPanelsByZIndex();
    }

    public void removePanel(DrawPanel panel) {
        panels.remove(panel);
    }

    private void sortPanelsByZIndex() {
        panels.sort(Comparator.comparing(DrawPanel::getZIndex));
    }

    public void draw() {
        for(DrawPanel drawPanel : panels) {
            drawPanel.draw();
        }
    }

    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        for(DrawPanel drawPanel : panels) {
            SceneChangeEvent sceneChangeEvent = drawPanel.receiveInput(inputEvent);
            if(sceneChangeEvent != null) {
                return sceneChangeEvent;
            }
        }
        return null;
    }
}
