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

        centerPanel.add(contentPanel);

        JPanel privateCheckboxPanel = new JPanel();
        privateCheckboxPanel.setLayout(new FlowLayout());

        privateCheckboxPanel.add(new JLabel("Private (visible to you and topic owner"));

        JCheckBox privateJCheckBox = new JCheckBox();
        privateCheckboxPanel.add(privateJCheckBox);

        centerPanel.add(privateCheckboxPanel);

        JButton replyJButton = new JButton("Submit a reply");
        replyJButton.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                Post createdPost = manager.addNewPost(
                    topic.id,
                    contentJTextArea.getText(),
                    privateJCheckBox.isSelected()
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
                dispatchEvent(new java.awt.event.WindowEvent(
                    NewPostScreen.this,
                    java.awt.event.WindowEvent.WINDOW_CLOSING
                ));
			}
		});

        JButton cancelJButton = new JButton("Cancel");
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewPostScreen.this.dispatchEvent(new java.awt.event.WindowEvent(
                    NewPostScreen.this,
                    java.awt.event.WindowEvent.WINDOW_CLOSING
                ));
            }
        });

        JPanel southPanel = new JPanel();
        southPanel.add(replyJButton);
        southPanel.add(cancelJButton);

        Container cp = getContentPane();
        cp.add(centerPanel, "Center");
        cp.add(southPanel, "South");
        pack();
    }
}
