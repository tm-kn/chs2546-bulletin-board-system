import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class TopicListScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private DefaultListModel<Topic> topicJListModel = new DefaultListModel<Topic>();
    private JButton refreshJButton;

    TopicListScreen(BulletinBoardClientManager manager) {
        this.manager = manager;
        createGUI();
        refreshData();
    }

    private void createGUI() {
		setTitle("Bulletin Board");

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				System.exit (0);
			}
		});

		Container cp = getContentPane();
		cp.setLayout (new BorderLayout ());

        createWestPanel();
        createSouthPanel();

        setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width/2, screenSize.height/2);
    }

    private void createWestPanel() {
        JList topicJList = new JList(topicJListModel);

        topicJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    Topic selectedTopic = (Topic) list.getModel().getElementAt(index);
                    TopicListScreen.this.createTopicScreen(selectedTopic);
                }
            }
        });

        JScrollPane topicJScrollPane = new JScrollPane(topicJList);
        topicJScrollPane.setMinimumSize (new Dimension (1000,200));

        TitledBorder topicBorder = new TitledBorder("Topics");
        topicBorder.setTitleJustification(TitledBorder.CENTER);
        topicBorder.setTitlePosition(TitledBorder.TOP);

        JPanel topicJPanel = new JPanel();
        topicJPanel.add(topicJScrollPane);
        topicJPanel.setBorder(topicBorder);

        JButton openTopicJButton = new JButton("Open topic");
        openTopicJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                Topic selectedTopic = (Topic) topicJList.getSelectedValue();

                if (selectedTopic == null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "You need to select topic first",
                        "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                TopicListScreen.this.createTopicScreen(selectedTopic);
            }
        });

        JPanel westPanel = new JPanel();
        westPanel.add(topicJPanel);
        westPanel.add(openTopicJButton);

		Container cp = getContentPane();
        cp.add(westPanel, "West");
    }

    private TopicScreen createTopicScreen(Topic topic) {
        EventListener onDelete = new EventListener() {
            public void onEvent() {
                refreshData();
            }
        };

        TopicScreen topicScreen = new TopicScreen(manager, topic, onDelete);
        topicScreen.setVisible(true);
        return topicScreen;
    }

    private void createSouthPanel() {
        JPanel southPanel = new JPanel();

        JButton topicJButton = new JButton("New topic");
        topicJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                EventListener onTopicSubmit = new EventListener() {
                    public void onEvent() {
                        TopicListScreen.this.refreshData();
                    }
                };
                NewTopicScreen newTopicScreen = new NewTopicScreen(manager, onTopicSubmit);
                newTopicScreen.setLocationRelativeTo(TopicListScreen.this);
                newTopicScreen.setVisible(true);
			}
		});

        refreshJButton = new JButton("Refresh");
        refreshJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                TopicListScreen.this.refreshData();
			}
		});

        southPanel.add(topicJButton);
        southPanel.add(refreshJButton);

		Container cp = getContentPane();
        cp.add(southPanel, "South");
    }

    private void refreshData() {
        refreshJButton.setEnabled(false);
        topicJListModel.clear();
        for(Topic topic: manager.getTopicList()) {
            topicJListModel.addElement(topic);
        }
        refreshJButton.setEnabled(true);
        System.out.println("Refreshed topic list");
    }
}
