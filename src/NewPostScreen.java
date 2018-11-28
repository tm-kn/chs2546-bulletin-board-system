import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class NewPostScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private Topic topic;
    private EventListener onResponseSubmit;

    public NewPostScreen(BulletinBoardClientManager manager, Topic topic, EventListener onResponseSubmit) {
        this.manager = manager;
        this.topic = topic;
        this.onResponseSubmit = onResponseSubmit;
        createGUI();
    }

    private void createGUI() {
		setTitle("Reply: " + topic.title);

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

        centerPanel.add(contentPanel);

        JButton replyJButton = new JButton("Submit a reply");
        replyJButton.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                Post createdPost = manager.addNewPost(
                    topic.id,
                    contentJTextArea.getText()
                );
                if (createdPost == null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Could not add the reply (unknown error). Please try again.",
                        "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                onResponseSubmit.onEvent();
                dispose();
			}
		});

        JPanel southPanel = new JPanel();
        southPanel.add(replyJButton);

        Container cp = getContentPane();
        cp.add(centerPanel, "Center");
        cp.add(southPanel, "South");
        pack();
    }
}
