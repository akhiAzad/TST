import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MainClass extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea passageArea;
    private JTextArea typingArea;
    private JComboBox<String> languageComboBox;
    private String[] bengaliPassages;
    private String[] englishPassages;
    private int currentPassageIndex;
    private long startTime;
    private String selectedLanguage;
    
    public MainClass() {
        setTitle("Typing Speed Software");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bengaliPassages = BenPassages.getPassages();
        englishPassages = EngPassages.getPassages();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        addLanguageSelectionPage();
        addInstructionPage();
        addTypingPage();
        addResultPage();

        add(mainPanel);
    }

    private void addLanguageSelectionPage() {
        JPanel languageSelectionPage = new JPanel(new BorderLayout());
        JLabel title = new JLabel("TYPING SPEED SOFTWARE", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        languageSelectionPage.add(title, BorderLayout.CENTER);

        languageComboBox = new JComboBox<>(new String[]{"Bengali", "English"});
        languageComboBox.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JButton startButton = new JButton("Start Typing");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(Color.RED);
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedLanguage = (String) languageComboBox.getSelectedItem();
                cardLayout.show(mainPanel, "InstructionPage");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(languageComboBox);
        buttonPanel.add(startButton);
        languageSelectionPage.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(languageSelectionPage, "LanguageSelectionPage");
    }

    private void addInstructionPage() {
        JPanel instructionPage = new JPanel(new BorderLayout());
        JLabel instructions = new JLabel("<html><ul><li>Type the given paragraph exactly as it appears, including punctuation and capitalization.</li><li>Your typing speed will be measured in words per minute (WPM) based on how quickly and accurately you can type the entire paragraph.</li></ul></html>", JLabel.CENTER);
        instructions.setFont(new Font("Arial", Font.PLAIN, 18));
        instructionPage.add(instructions, BorderLayout.CENTER);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(Color.RED);
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "TypingPage");
                startTypingTest();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        instructionPage.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(instructionPage, "InstructionPage");
    }

    private void addTypingPage() {
        JPanel typingPage = new JPanel(new BorderLayout());

        passageArea = new JTextArea();
        passageArea.setFont(new Font("Arial", Font.PLAIN, 24));
        passageArea.setLineWrap(true);
        passageArea.setWrapStyleWord(true);
        passageArea.setEditable(false);
        typingPage.add(new JScrollPane(passageArea), BorderLayout.NORTH);

        typingArea = new JTextArea();
        typingArea.setFont(new Font("Arial", Font.PLAIN, 24));
        typingArea.setLineWrap(true);
        typingArea.setWrapStyleWord(true);
        typingPage.add(new JScrollPane(typingArea), BorderLayout.CENTER);

        JButton endButton = new JButton("End Typing");
        endButton.setFont(new Font("Arial", Font.BOLD, 24));
        endButton.setBackground(Color.RED);
        endButton.setForeground(Color.WHITE);
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endTypingTest();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(endButton);
        typingPage.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(typingPage, "TypingPage");
    }

    private void addResultPage() {
        JPanel resultPage = new JPanel(new BorderLayout());

        JLabel resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        resultPage.add(resultLabel, BorderLayout.CENTER);

        JButton moreButton = new JButton("Do you want more passages for practice?");
        moreButton.setFont(new Font("Arial", Font.BOLD, 24));
        moreButton.setBackground(Color.RED);
        moreButton.setForeground(Color.WHITE);
        moreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "LanguageSelectionPage");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(moreButton);
        resultPage.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(resultPage, "ResultPage");
    }

    private void startTypingTest() {
        Random rand = new Random();
        if (selectedLanguage.equals("Bengali")) {
            currentPassageIndex = rand.nextInt(bengaliPassages.length);
            passageArea.setText(bengaliPassages[currentPassageIndex]);
        } else {
            currentPassageIndex = rand.nextInt(englishPassages.length);
            passageArea.setText(englishPassages[currentPassageIndex]);
        }
        typingArea.setText("");
        startTime = System.currentTimeMillis();
    }

    private void endTypingTest() {
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0 / 60.0; // time in minutes
        int wordCount = typingArea.getText().split("\\s+").length;
        double wpm = wordCount / totalTime;
        double accuracy = calculateAccuracy(passageArea.getText(), typingArea.getText());

        JLabel resultLabel = (JLabel) ((JPanel) mainPanel.getComponent(3)).getComponent(0);
        resultLabel.setText(String.format("<html>Speed: %.2f WPM<br>Accuracy: %.2f%%</html>", wpm, accuracy));

        cardLayout.show(mainPanel, "ResultPage");
    }

    private double calculateAccuracy(String passage, String typing) {
        int totalChars = passage.length();
        int correctChars = 0;

        for (int i = 0; i < typing.length() && i < totalChars; i++) {
            if (passage.charAt(i) == typing.charAt(i)) {
                correctChars++;
            }
        }

        return (double) correctChars / totalChars * 100;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainClass().setVisible(true);
        });
    }
}
