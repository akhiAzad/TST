import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TypingPanel extends JPanel implements ActionListener {
    private String[] passages;
    private String inputPassage;
    private JTextArea passageArea;
    private JTextPane inputArea;
    private JButton startButton;
    private JButton endButton;
    private JLabel timerLabel;
    private Timer timer;
    private int timeLimit = 60; // Set the time limit in seconds
    private int timeRemaining;
    private long startTime;

    public TypingPanel(String[] passages) {
        if (passages == null || passages.length == 0) {
            throw new IllegalArgumentException("Passages cannot be null or empty.");
        }
        this.passages = passages;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());

        passageArea = new JTextArea();
        passageArea.setWrapStyleWord(true);
        passageArea.setLineWrap(true);
        passageArea.setEditable(false); // Disable editing for the passage area
        passageArea.setFont(new Font("Serif", Font.PLAIN, 18)); // Set font size to 18
        add(new JScrollPane(passageArea), BorderLayout.NORTH);

        inputArea = new JTextPane();
        inputArea.setEditable(false); // Start as non-editable
        inputArea.setFont(new Font("Serif", Font.PLAIN, 18)); // Set font size to 18
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                highlightErrors();
                if (inputArea.getText().length() >= inputPassage.length()) {
                    endTest();
                }
            }
        });
        add(new JScrollPane(inputArea), BorderLayout.CENTER);

        timerLabel = new JLabel("Time Remaining: " + timeLimit + " seconds");
        timerLabel.setFont(new Font("Serif", Font.PLAIN, 18)); // Set font size to 18
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timerLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        buttonPanel.add(startButton);

        endButton = new JButton("End");
        endButton.addActionListener(this);
        endButton.setEnabled(false);
        buttonPanel.add(endButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void startTest() {
        inputPassage = passages[(int) (Math.random() * passages.length)];
        passageArea.setText(inputPassage);
        inputArea.setText("");
        inputArea.setEditable(true); // Allow editing now
        inputArea.requestFocusInWindow();
        startButton.setEnabled(false);
        endButton.setEnabled(true);
        timeRemaining = timeLimit;
        startTime = System.currentTimeMillis();

        // Start the timer
        if (timer != null && timer.isRunning()) {
            timer.stop(); // Stop the previous timer if it exists
        }
        timer = new Timer(1000, e -> updateTimer());
        timer.start();
    }

    private void updateTimer() {
        timeRemaining--;
        timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");

        if (timeRemaining <= 0) {
            timer.stop();
            endTest();
        }
    }

    private void endTest() {
        if (timer != null) {
            timer.stop();
        }
        long endTime = System.currentTimeMillis();
        double timeTaken = (endTime - startTime) / 1000.0;
        int wordCount = inputPassage.split("\\s+").length;
        double speed = (wordCount / timeTaken) * 60;
        double accuracy = calculateLevenshteinAccuracy(inputPassage, inputArea.getText());

        JOptionPane.showMessageDialog(this, String.format(
                "Time taken: %.2f seconds\nTyping speed: %.2f words per minute\nAccuracy: %.2f%%", 
                timeTaken, speed, accuracy));

        startButton.setEnabled(true);
        endButton.setEnabled(false);
        inputArea.setEditable(false); // Make the input area non-editable again
    }

    private double calculateLevenshteinAccuracy(String original, String typed) {
        int distance = levenshteinDistance(original, typed);
        int maxLength = Math.max(original.length(), typed.length());
        return maxLength == 0 ? 100.0 : ((double)(maxLength - distance) / maxLength) * 100; // Avoid division by zero
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(dp[i - 1][j] + 1,    // Deletion
                            Math.min(dp[i][j - 1] + 1,    // Insertion
                            dp[i - 1][j - 1] + cost));  // Substitution
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    private void highlightErrors() {
        String originalText = inputPassage;
        String typedText = inputArea.getText();
        StyledDocument doc = inputArea.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style errorStyle = doc.addStyle("Error", null);
        StyleConstants.setForeground(errorStyle, Color.RED);

        doc.setCharacterAttributes(0, typedText.length(), defaultStyle, true);

        int minLength = Math.min(originalText.length(), typedText.length());

        for (int i = 0; i < minLength; i++) {
            if (originalText.charAt(i) != typedText.charAt(i)) {
                doc.setCharacterAttributes(i, 1, errorStyle, true);
            }
        }

        if (typedText.length() > originalText.length()) {
            doc.setCharacterAttributes(originalText.length(), typedText.length() - originalText.length(), errorStyle, true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startTest();
        } else if (e.getSource() == endButton) {
            endTest();
        }
    }
}
