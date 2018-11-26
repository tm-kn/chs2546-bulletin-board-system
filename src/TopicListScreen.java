import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class TopicListScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private DefaultListModel<Topic> topicJListModel = new DefaultListModel<Topic>();
    private Topic[] topicList = {};

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

                TopicScreen topicScreen = new TopicScreen(manager, selectedTopic);
                topicScreen.setVisible(true);
            }
        });

        JPanel westPanel = new JPanel();
        westPanel.add(topicJPanel);
        westPanel.add(openTopicJButton);

		Container cp = getContentPane();
        cp.add(westPanel, "West");
    }

    private void createSouthPanel() {
        JPanel southPanel = new JPanel();

        JButton topicJButton = new JButton("New topic");
        topicJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                NewTopicScreen newTopicScreen = new NewTopicScreen(manager);
                newTopicScreen.setLocationRelativeTo(TopicListScreen.this);
                newTopicScreen.setVisible(true);
			}
		});

        southPanel.add(topicJButton);

		Container cp = getContentPane();
        cp.add(southPanel, "South");
    }

    private void refreshData() {
        for(Topic topic: manager.getTopicList()) {
            topicJListModel.addElement(topic);
        }
    }
}
