import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class TopicScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private Topic topic;
    private JLabel titleJLabel = new JLabel("Topic");
    private JButton refreshJButton;
    private JPanel postListPanel = new JPanel();

    public TopicScreen(BulletinBoardClientManager manager, Topic topic) {
        this.manager = manager;
        this.topic = topic;
        createGUI();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshData();
            }
        });
    }

    private void createGUI() {
        setTopicTitle(topic.toString());

        Container cp = getContentPane();
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(titleJLabel);

        centerPanel.add(postListPanel);

        cp.add(centerPanel, "Center");


        JPanel southPanel = new JPanel();

        refreshJButton = new JButton("Refresh");
        refreshJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshData();
            }
        });

        southPanel.add(refreshJButton);

        cp.add(southPanel, "South");
        pack();
    }


    private void setTopicTitle(String title) {
        setTitle(title);
        titleJLabel.setText(title);
    }


    private void updatePostList() {
        postListPanel.removeAll();
        for(Post post: topic.getPostList()) {
            JLabel postContent = new JLabel(post.content);
            postListPanel.add(postContent);
        }
    }

    private void refreshData() {
        refreshJButton.setEnabled(false);
        Topic returnedTopic = manager.getTopicOfID(topic.id);
        System.out.println("Refreshed data on topic screen " + topic.id);
        refreshJButton.setEnabled(true);
        if (returnedTopic == null) {
            JOptionPane.showMessageDialog(
                null, "Can't load the topic.", "Bulletin Board",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        topic = returnedTopic;

        updatePostList();

        setTopicTitle(topic.toString());
    }
}
