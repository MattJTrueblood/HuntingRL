package huntingrl;

import javax.swing.JFrame;
import asciiPanel.AsciiPanel;
import huntingrl.scenes.SceneController;
import huntingrl.scenes.StartScene;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainApplication extends JFrame implements KeyListener {

  private AsciiPanel terminal;
  private SceneController sceneController;

  private MainApplication() {
    super();
    this.terminal = new AsciiPanel(120, 60);
    this.add(terminal);
    this.pack();

    this.sceneController = new SceneController(new StartScene());
    this.addKeyListener(this);
    this.drawScene();
  }

  private void drawScene() {
    terminal.clear();
    this.sceneController.drawScene(terminal);
    this.repaint();
  }

  public void keyPressed(KeyEvent e) {
    this.sceneController.receiveEvent(e);
    this.drawScene();
  }

  public void keyReleased(KeyEvent e) {}

  public void keyTyped(KeyEvent e) {}

  public static void main(String[] args) {
    MainApplication app = new MainApplication();
    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    app.setVisible(true);
  }
}
