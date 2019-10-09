package huntingrl;

import javax.swing.JFrame;
import asciiPanel.AsciiPanel;
import huntingrl.view.RenderBuffer;
import huntingrl.view.SceneController;
import huntingrl.view.menu.StartScene;
import huntingrl.util.Constants;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MainApplication extends JFrame implements KeyListener {

    private AsciiPanel terminal;
    private RenderBuffer buffer;
    private SceneController sceneController;
    private final Queue<InputEvent> inputQueue = new ConcurrentLinkedQueue<>();

    private MainApplication() {
        super();
        this.terminal = new AsciiPanel(Constants.TERMINAL_WIDTH, Constants.TERMINAL_HEIGHT);
        this.add(terminal);
        this.pack();

        this.buffer = new RenderBuffer(terminal);

        this.sceneController = new SceneController(new StartScene(buffer));
        this.addKeyListener(this);
        this.drawScene();
    }

    public void start() {
        while(true) {
            synchronized(inputQueue) {
                if (!inputQueue.isEmpty()) {
                    while (!inputQueue.isEmpty()) {
                        sceneController.receiveEvent(inputQueue.poll());
                    }
                    drawScene();
                }
            }
        }
    }

    private void drawScene() {
        terminal.clear();
        this.sceneController.drawScene();
        this.repaint();
    }

    public void keyPressed(KeyEvent e) {
        this.inputQueue.add(e);
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        MainApplication app = new MainApplication();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
        app.start();
    }
}
