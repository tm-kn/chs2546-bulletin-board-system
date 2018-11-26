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
        pack();
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
        setState(Frame.NORMAL);
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

        JPanel westPanel = new JPanel();
        westPanel.add(topicJPanel);

		Container cp = getContentPane();
        cp.add(westPanel, "West");
    }

    private void createSouthPanel() {
        JPanel southPanel = new JPanel();

        JButton topicJButton = new JButton("New topic");

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
