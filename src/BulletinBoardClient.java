public class BulletinBoardClient {
	public static void main(String[] args) {
        EventListener onLogin = new EventListener() {
            public void onEvent() {
                BulletinBoardClient.launchLoggedInScreen();
            }
        };

        BulletinBoardClientManager manager = new BulletinBoardClientManager();
        new LoginScreen(manager, onLogin).setVisible(true);
	}

    public static void launchLoggedInScreen() {
    }
}
