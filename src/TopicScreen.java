import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class TopicScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private Topic topic;

    public TopicScreen(BulletinBoardClientManager manager, Topic topic) {
        this.manager = manager;
        this.topic = topic;
        createGUI();
    }

    public void createGUI() {
        Container cp = getContentPane();
        JPanel centerPanel = new JPanel();
        setTitle(topic.toString());
        JLabel titleJLabel = new JLabel(topic.toString());
        centerPanel.add(titleJLabel);
        cp.add(centerPanel, "Center");
        pack();
    }
}
