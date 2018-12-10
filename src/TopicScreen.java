import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;


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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screenSize.width/2), (int)(screenSize.height/2));
        setMinimumSize(new Dimension(700, 400));
        setResizable(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container cp = getContentPane();
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(titleJLabel);

        JScrollPane centerPanelScrollable = new JScrollPane(centerPanel);

        postListPanel.setLayout(new BoxLayout(postListPanel, BoxLayout.Y_AXIS));
        centerPanel.add(postListPanel);

        cp.add(centerPanelScrollable, "Center");


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
            postListPanel.add(new JSeparator());
            JPanel postPanel = new JPanel();
            postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.PAGE_AXIS));
            JTextArea contentArea = new JTextArea(post.content);

            contentArea.setFocusable(false);
            contentArea.setCursor(null);
            contentArea.setEditable(false);
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setOpaque(false);
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(contentArea);
            contentPanel.setBorder(new EmptyBorder(20, 50, 50, 20));

            User author = manager.getUserOfId(post.authorID);

            JPanel metadataPanel = new JPanel();

            StringBuffer metadataLabelString = new StringBuffer();
            metadataLabelString.append(
                "(#" + post.id + ") " +  author.username + " wrote at "
                + post.datetime
            );

            if (post.isPrivate) {
                metadataLabelString.append(" (private)");
            }

            metadataPanel.add(new JLabel(metadataLabelString.toString()));

            postPanel.add(metadataPanel);
            postPanel.add(contentPanel);
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

        if (topic.ownerId.equals(manager.getUserId())) {
            deleteJButton.setVisible(true);
        } else {
            deleteJButton.setVisible(false);
        }

        updatePostList();

        setTopicTitle(topic.toString());
    }
}
