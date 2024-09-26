import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainClass {
    private JFrame frame;
    private JPanel cards;
    private CardLayout cardLayout;

    private JTextArea passageArea;
    private JTextField inputField;
    private JLabel timerLabel;
    private JLabel accuracyLabel;
    private JLabel speedLabel;
    private JButton startButton;
    private JButton nextButton;
    private JButton yesButton;
    private JButton noButton;
    private JComboBox<String> languageComboBox;
    private String[] passages;
    private int currentPassageIndex;
    private Timer timer;
    private int timeElapsed;

    public MainClass() {
        frame = new JFrame("Typing Speed Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        createLanguageSelectionPanel();
        createTypingTestPanel();
        createResultsPanel();

        frame.add(cards);
        frame.setVisible(true);
    }

    private void createLanguageSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        languageComboBox = new JComboBox<>(new String[]{"Bengali", "English"});
        startButton = new JButton("Start");

        startButton.addActionListener(new StartButtonActionListener());

        panel.add(new JLabel("Select Language:"));
        panel.add(languageComboBox);
        panel.add(startButton);

        cards.add(panel, "LanguageSelection");
    }

    private void createTypingTestPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        passageArea = new JTextArea();
        passageArea.setFont(new Font("Serif", Font.PLAIN, 18));
        passageArea.setLineWrap(true);
        passageArea.setWrapStyleWord(true);
        passageArea.setEditable(false);

        inputField = new JTextField();
        inputField.setFont(new Font("Serif", Font.PLAIN, 18));
        inputField.addActionListener(new TypingActionListener());

        timerLabel = new JLabel("Time: 0s");

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 1));
        topPanel.add(timerLabel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(passageArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        cards.add(panel, "TypingTest");
    }

    private void createResultsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1));
        accuracyLabel = new JLabel("Accuracy: 100%");
        speedLabel = new JLabel("Speed: 0 WPM");
        yesButton = new JButton("Yes");
        noButton = new JButton("No");

        yesButton.addActionListener(new YesButtonActionListener());
        noButton.addActionListener(new NoButtonActionListener());

        panel.add(accuracyLabel);
        panel.add(speedLabel);
        panel.add(new JLabel("Do you want more passages for practice?"));
        panel.add(yesButton);
        panel.add(noButton);

        cards.add(panel, "Results");
    }

    private void loadPassages(String language) {
        if (language.equals("Bengali")) {
            passages = BenPassages.getPassages();
        } else if (language.equals("English")) {
            passages = EngPassages.getPassages();
        }
        currentPassageIndex = 0;
        System.out.println("Loaded " + passages.length + " passages for " + language);
    }

    private void showNextPassage() {
        if (currentPassageIndex < passages.length) {
            String passage = passages[currentPassageIndex];
            passageArea.setText(passage);
            System.out.println("Showing passage: " + passage);
            inputField.setText("");
            startTimer();
            cardLayout.show(cards, "TypingTest");
        } else {
            JOptionPane.showMessageDialog(frame, "You have completed all passages!");
        }
    }

    private void startTimer() {
        timeElapsed = 0;
        timer = new Timer(1000, e -> {
            timeElapsed++;
            timerLabel.setText("Time: " + timeElapsed + "s");
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void checkAccuracy() {
        String inputText = inputField.getText();
        String passageText = passages[currentPassageIndex];
        int distance = LevenshteinDistance.calculate(inputText, passageText);
        int maxLen = Math.max(inputText.length(), passageText.length());
        double accuracy = 100.0 - ((double) distance / maxLen) * 100.0;
        accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));
    }

    private void calculateSpeed() {
        String inputText = inputField.getText();
        int wordCount = inputText.split("\\s+").length;
        double minutes = timeElapsed / 60.0;
        double speed = wordCount / minutes;
        speedLabel.setText(String.format("Speed: %.2f WPM", speed));
    }

    private class TypingActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            stopTimer();
            checkAccuracy();
            calculateSpeed();
            cardLayout.show(cards, "Results");
        }
    }

    private class StartButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            loadPassages(selectedLanguage);
            if (passages != null && passages.length > 0) {
                showNextPassage();
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a language and load passages first.");
            }
        }
    }

    private class YesButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentPassageIndex++;
            if (currentPassageIndex < passages.length) {
                showNextPassage();
            } else {
                JOptionPane.showMessageDialog(frame, "You have completed all passages!");
            }
        }
    }

    private class NoButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Thank you for using the Typing Speed Test!");
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainClass::new);
    }
}

class LevenshteinDistance {
    public static int calculate(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
                }
            }
        }

        return dp[a.length()][b.length()];
    }
}
