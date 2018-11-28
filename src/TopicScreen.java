import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class TopicScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private Topic topic;
    private JLabel titleJLabel = new JLabel("Topic");
    private JButton refreshJButton;
    private JPanel postListPanel = new JPanel();
    private NewPostScreen newPostScreen;

    public TopicScreen(BulletinBoardClientManager manager, Topic topic) {
        this.manager = manager;
        this.topic = topic;
        createGUI();
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
        refreshData();
    }

    private void createGUI() {
        setTopicTitle(topic.toString());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        JButton replyJButton = new JButton("Reply");
        replyJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TopicScreen.this.openReplyWindow();
            }
        });

        southPanel.add(refreshJButton);
        southPanel.add(replyJButton);

        cp.add(southPanel, "South");
        pack();
    }


    private void setTopicTitle(String title) {
        setTitle(title);
        titleJLabel.setText(title);
    }

    private void openReplyWindow() {
        EventListener onResponseSubmit = new EventListener() {
            public void onEvent() {
                TopicScreen.this.refreshData();
            }
        };
        if (newPostScreen == null) {
            newPostScreen = new NewPostScreen(manager, topic, onResponseSubmit);
        }

        newPostScreen.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                newPostScreen = null;
            }
        });

        newPostScreen.setVisible(true);
        newPostScreen.setLocationRelativeTo(this);
    }


    private void updatePostList() {
        postListPanel.removeAll();
        for(Post post: topic.getPostList()) {
            JLabel postContent = new JLabel(post.content);
            postListPanel.add(postContent);
        }
        postListPanel.revalidate();
        postListPanel.repaint();
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
