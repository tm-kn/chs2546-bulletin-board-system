import java.awt.*;
import javax.swing.*;


class LoginScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private EventListener onLogin;

    public LoginScreen(BulletinBoardClientManager manager) {
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

		Container cp = getContentPane();
		cp.setLayout (new BorderLayout ());

		JPanel usernamePanel = new JPanel();
		usernamePanel.setLayout (new FlowLayout ());

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameTextField = new JTextField(12);

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        JPanel loginButtonPanel = new JPanel();

        JButton loginButton = new JButton();
        loginButton.setText("Login");
        getRootPane().setDefaultButton(loginButton);
        loginButtonPanel.add(loginButton);

		loginButton.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                usernameTextField.setEditable(false);
                manager.authenticateUser(usernameTextField.getText());
                onLogin.onEvent();
			}
		});

        cp.add(usernamePanel, "North");
        cp.add(loginButtonPanel, "South");

        pack();
        setLocationRelativeTo(null);
    }

    public void setOnLoginEventListener(EventListener onLogin) {
        this.onLogin = onLogin;
    }
}