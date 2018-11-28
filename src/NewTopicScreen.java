import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class NewTopicScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private EventListener onTopicSubmit;

    public NewTopicScreen(BulletinBoardClientManager manager, EventListener onTopicSubmit) {
        this.manager = manager;
        this.onTopicSubmit = onTopicSubmit;
        createGUI();
    }

    private void createGUI() {
		setTitle("New Topic");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        newTopicJButton.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                Topic createdTopic = manager.addNewTopic(
                    titleJTextField.getText(),
                    contentJTextArea.getText()
                );
                if (createdTopic == null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Could not add the topic (unknown error). Please try again.",
                        "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                onTopicSubmit.onEvent();
                TopicScreen topicScreen = new TopicScreen(manager, createdTopic);
                topicScreen.setVisible(true);
                topicScreen.setLocationRelativeTo(NewTopicScreen.this);

                dispatchEvent(new java.awt.event.WindowEvent(
                    NewTopicScreen.this,
                    java.awt.event.WindowEvent.WINDOW_CLOSING
                ));
			}
		});

        JPanel southPanel = new JPanel();
        southPanel.add(newTopicJButton);

        Container cp = getContentPane();
        cp.add(centerPanel, "Center");
        cp.add(southPanel, "South");
        pack();
    }
}
