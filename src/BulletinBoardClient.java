import java.io.IOException;
import javax.swing.JOptionPane;


public class BulletinBoardClient {
    private LoginScreen loginScreen;
    private BulletinBoardClientManager manager;

	public static void main(String[] args) {
        new BulletinBoardClient().launchLoginScreen();
	}

    BulletinBoardClient() {
        createManager();
    }

    private void createManager() {
        manager = new BulletinBoardClientManager();

        // Connect to JavaSpace
        try {
            manager.connectToJavaSpace();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null, "Cannot connect to JavaSpace.", "Bulletin Board",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void launchLoginScreen() {
        loginScreen = new LoginScreen(manager);

        loginScreen.setOnLoginEventListener(new EventListener() {
            public void onEvent() {
                launchLoggedInScreen(manager);
                loginScreen.setVisible(false);
                loginScreen.dispose();
            }
        });

        loginScreen.setVisible(true);
    }

    private void launchLoggedInScreen(BulletinBoardClientManager manager) {
        TopicListScreen topicListScreen = new TopicListScreen(manager);
        topicListScreen.setLocationRelativeTo(null);
        topicListScreen.setVisible(true);
    }
}
