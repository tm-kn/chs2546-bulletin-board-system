import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class TopicScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private Topic topic;
    private JLabel titleJLabel = new JLabel("Topic");
    private JButton refreshJButton;
    private JButton deleteJButton;
    private JPanel postListPanel = new JPanel();
    private NewPostScreen newPostScreen;
    private EventListener onTopicDelete;

    public TopicScreen(BulletinBoardClientManager manager, Topic topic, EventListener onTopicDelete) {
        this.manager = manager;
        this.topic = topic;
        this.onTopicDelete = onTopicDelete;
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

        postListPanel.setLayout(new BoxLayout(postListPanel, BoxLayout.PAGE_AXIS));
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


        deleteJButton = new JButton("Delete");
        deleteJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int dialogResult = JOptionPane.showConfirmDialog (
                    TopicScreen.this,
                    "Do you want to delete the topic?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION
                );
                if(dialogResult != JOptionPane.YES_OPTION){
                    return;
                }
                try {
                    if (!manager.deleteTopic(topic.id)) {
                        JOptionPane.showMessageDialog(
                            TopicScreen.this,
                            "Something went wrong.",
                            "Bulletin Board",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                        TopicScreen.this,
                        "Something went wrong.",
                        "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(
                            null,
                            "Topic deleted",
                            "Bulletin Board",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                });


                if (onTopicDelete != null) {
                    onTopicDelete.onEvent();
                }

                dispatchEvent(new java.awt.event.WindowEvent(
                    TopicScreen.this,
                    java.awt.event.WindowEvent.WINDOW_CLOSING
                ));
            }
        });

        southPanel.add(refreshJButton);
        southPanel.add(replyJButton);
        southPanel.add(deleteJButton);
        deleteJButton.setVisible(false);

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
            JPanel postPanel = new JPanel();
            postPanel.add(new Label(post.content));
            User author = manager.getUserOfId(post.authorID);
            postPanel.add(new JLabel(author.username));
            postPanel.add(new JLabel(post.datetime.toString()));
            postListPanel.add(postPanel);
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
                TopicScreen.this, "Can't load the topic.", "Bulletin Board",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        topic = returnedTopic;

        if (topic.ownerId == manager.getUserId()) {
            deleteJButton.setVisible(true);
        } else {
            deleteJButton.setVisible(false);
        }

        updatePostList();

        setTopicTitle(topic.toString());
    }
}
