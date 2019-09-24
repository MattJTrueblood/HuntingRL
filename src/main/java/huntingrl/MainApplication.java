package huntingrl;

import javax.swing.JFrame;
import asciiPanel.AsciiPanel;

public class MainApplication extends JFrame {

  private AsciiPanel terminal;

  private MainApplication() {
    super();
    this.terminal = new AsciiPanel();
    terminal.write("hello world", 1, 1);
    this.add(terminal);
    this.pack();
  }

  public static void main(String[] args) {
    MainApplication app = new MainApplication();
    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    app.setVisible(true);
  }
}
