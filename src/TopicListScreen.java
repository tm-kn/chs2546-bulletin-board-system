import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;


public class TopicListScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private DefaultTableModel topicJTableModel = new DefaultTableModel();
    private JTable topicJTable = new JTable(topicJTableModel);
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
		cp.setLayout (new BorderLayout());

        createCenterPanel();
        createSouthPanel();

        setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screenSize.width/1.5), (int)(screenSize.height/1.5));
        setResizable(false);
        setMinimumSize(new Dimension(600, 500));
    }

    private void createCenterPanel() {
        topicJTable.setDefaultEditor(Object.class, null);
        topicJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topicJTable.setDragEnabled(false);
        topicJTable.getTableHeader().setReorderingAllowed(false);
        topicJTable.setFillsViewportHeight(true);
        JScrollPane topicJScrollPane = new JScrollPane(topicJTable);
        topicJTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        topicJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTable table = (JTable) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int row = table.rowAtPoint(evt.getPoint());
                    if (row == -1) {
                        return;
                    }
                    Topic selectedTopic = (Topic) topicJTable.getValueAt(
                        row,
                        1
                    );
                    TopicListScreen.this.createTopicScreen(selectedTopic);
                }
            }
        });

        JPanel topicJPanel = new JPanel();

        JButton openTopicJButton = new JButton("Open topic");
        openTopicJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                Integer row = topicJTable.getSelectedRow();

                if (row == -1) {
                    JOptionPane.showMessageDialog(
                        TopicListScreen.this,
                        "You need to select topic first",
                        "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                Topic selectedTopic = (Topic) topicJTable.getValueAt(
                    topicJTable.getSelectedRow(),
                    1
                );

                TopicListScreen.this.createTopicScreen(selectedTopic);
            }
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new TitledBorder("Topics"));
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(topicJScrollPane, "Center");
        centerPanel.add(openTopicJButton, "South");

		Container cp = getContentPane();
        cp.add(centerPanel, "Center");
    }

    private TopicScreen createTopicScreen(Topic topic) {
        EventListener onDelete = new EventListener() {
            public void onEvent() {
                refreshData();
            }
        };

        TopicScreen topicScreen = new TopicScreen(manager, topic, onDelete);
        topicScreen.setLocationRelativeTo(this);
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
        topicJTableModel.getDataVector().removeAllElements();
        topicJTableModel.setColumnIdentifiers(new String[]{
            "#",
            "Title",
            "Author",
            "Date",
        });
        for(Topic topic: manager.getTopicList()) {
            topicJTableModel.addRow(new Object[]{
                topic.id,
                topic,
                manager.getUserOfId(topic.ownerId).username,
                topic.datetime,
            });
        }
        topicJTableModel.fireTableDataChanged();
        topicJTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        refreshJButton.setEnabled(true);
        System.out.println("Refreshed topic list");
    }
}
