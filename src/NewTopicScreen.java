import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class NewTopicScreen extends JFrame {
    private BulletinBoardClientManager manager;

    public NewTopicScreen(BulletinBoardClientManager manager) {
        this.manager = manager;
        createGUI();
    }

    private void createGUI() {
		setTitle("New Topic");

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        JPanel contentPanel = new JPanel();
        TitledBorder contentPanelBorder = new TitledBorder("Content");
        contentPanel.setBorder(contentPanelBorder);

        JTextArea contentJTextArea = new JTextArea(30, 50);
        contentJTextArea.setLineWrap(true);
        contentJTextArea.setWrapStyleWord(true);
        JScrollPane contentJScrollPane = new JScrollPane(contentJTextArea);
        contentPanel.add(contentJScrollPane);

        JPanel titlePanel = new JPanel();
        JLabel titleJLabel = new JLabel("Title: ");
        titlePanel.add(titleJLabel);

        JTextField titleJTextField = new JTextField(20);
        titlePanel.add(titleJTextField);

        centerPanel.add(titlePanel);

        centerPanel.add(contentPanel);

        JButton newTopicJButton = new JButton("Submit");

        JPanel southPanel = new JPanel();
        southPanel.add(newTopicJButton);

        Container cp = getContentPane();
        cp.add(centerPanel, "Center");
        cp.add(southPanel, "South");
        pack();
    }
}
