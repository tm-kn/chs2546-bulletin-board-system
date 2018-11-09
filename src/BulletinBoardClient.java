public class BulletinBoardClient {
    private LoginScreen loginScreen;
    private BulletinBoardClientManager manager;

	public static void main(String[] args) {
        new BulletinBoardClient().launchLoginScreen();
	}

    BulletinBoardClient() {
        manager = new BulletinBoardClientManager();
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
        topicListScreen.setVisible(true);
    }
}
