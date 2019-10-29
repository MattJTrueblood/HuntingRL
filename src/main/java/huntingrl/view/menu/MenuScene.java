package huntingrl.view.menu;

import asciiPanel.AsciiPanel;
import huntingrl.view.RenderBuffer;
import huntingrl.view.Scene;
import huntingrl.view.SceneChangeEvent;
import huntingrl.util.Constants;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

@Getter
@Setter
public abstract class MenuScene implements Scene {

    private static final int NAVIGATE_UP_KEYCODE = KeyEvent.VK_UP;
    private static final int NAVIGATE_DOWN_KEYCODE = KeyEvent.VK_DOWN;
    private static final int SELECT_ITEM_KEYCODE = KeyEvent.VK_ENTER;

    private RenderBuffer buffer;
    private List<MenuItem> menuItems;
    private int currentSelectedIndex;

    public MenuScene(RenderBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public SceneChangeEvent receiveInput(InputEvent inputEvent) {
        if(inputEvent instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) inputEvent;
            switch (keyEvent.getKeyCode()) {
                case NAVIGATE_UP_KEYCODE:
                    navigateMenuUp();
                    break;
                case NAVIGATE_DOWN_KEYCODE:
                    navigateMenuDown();
                    break;
                case SELECT_ITEM_KEYCODE:
                    return menuItems.get(currentSelectedIndex).sceneChangeEvent;
            }
        }
        return null;
    }

    private void navigateMenuUp() {
        if(currentSelectedIndex == 0) {
            currentSelectedIndex = menuItems.size() - 1;
        } else {
            currentSelectedIndex--;
        }
    }

    private void navigateMenuDown() {
        if(currentSelectedIndex ==  menuItems.size() - 1) {
            currentSelectedIndex = 0;
        } else {
            currentSelectedIndex++;
        }
    }

    @Override
    public void draw() {
        this.buffer.clearBuffer();
        for(int i = 0; i < menuItems.size(); i++) {
            MenuItem itemToWrite = menuItems.get(i);
            Color textFgColor = Color.WHITE;
            Color textBgColor = Color.BLACK;
            if(i == currentSelectedIndex) {
                textFgColor = Color.BLACK;
                textBgColor = Color.WHITE;
            }
            this.buffer.getTerminal().writeCenter(itemToWrite.getText(),
                    Constants.TERMINAL_HEIGHT / 2 + (i - (menuItems.size() / 2)),
                    textFgColor, textBgColor);
        }
    }

    public void init() {
        currentSelectedIndex = 0;
    }
}
