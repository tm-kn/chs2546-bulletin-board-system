import java.awt.*;
import javax.swing.*;


public class TopicListScreen extends JFrame {
    private BulletinBoardClientManager manager;

    TopicListScreen(BulletinBoardClientManager manager) {
        this.manager = manager;
        createGUI();
    }

    public void createGUI() {
		setTitle("Bulletin Board");

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				System.exit (0);
			}
		});
    }
}
