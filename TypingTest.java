import javax.swing.*;

public class TypingTest extends JFrame {
    private String[] passages;
    private TypingPanel typingPanel;

    public TypingTest(String[] passages) {
        this.passages = passages;
        setupUI();
    }

    private void setupUI() {
        setTitle("Typing Speed Test");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        typingPanel = new TypingPanel(passages);
        add(typingPanel);
    }
}
