import javax.swing.JOptionPane;


public class NotificationScreen {
    private String message;

    public NotificationScreen(String message) {
        this.message = message;
    }

    public void show() {
        JOptionPane.showMessageDialog(
            null, message, "Notification",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
