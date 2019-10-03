package huntingrl.view;

import asciiPanel.AsciiPanel;
import huntingrl.util.Constants;
import huntingrl.view.panel.DrawPanel;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class MultiPanelScene implements Scene {
    private List<DrawPanel> panels = new ArrayList<>();
    protected RenderBuffer buffer;

    public MultiPanelScene(RenderBuffer buffer) {
        this.buffer = buffer;
        //Add base panel
        DrawPanel basePanel = new DrawPanel(buffer, 0, 0, Constants.TERMINAL_WIDTH, Constants.TERMINAL_HEIGHT, Integer.MIN_VALUE);
        basePanel.setBaseColor(Color.GRAY);
        addPanel(basePanel);
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
        this.buffer.clearBuffer();
        for(DrawPanel drawPanel : panels) {
            drawPanel.draw();
        }
        this.buffer.draw();
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
