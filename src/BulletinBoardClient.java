import net.jini.space.*;
import net.jini.core.lease.*;
import java.awt.*;
import javax.swing.*;

public class BulletinBoardClient extends JFrame {
    BulletinBoardClient() {
        createGUI();
    }

	public static void main(String[] args) {
        new BulletinBoardClient().setVisible(true);
	}

    void createGUI() {
		setTitle ("Bulletin Board");
		addWindowListener (new java.awt.event.WindowAdapter () {
			public void windowClosing (java.awt.event.WindowEvent evt) {
				System.exit (0);
			}
		});
    }
}
