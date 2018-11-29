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

        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordTextField = new JTextField(12);

		JPanel passwordPanel = new JPanel();
		passwordPanel.setLayout (new FlowLayout ());

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);

        JPanel loginButtonPanel = new JPanel();

        JButton loginButton = new JButton();
        loginButton.setText("Login");
        getRootPane().setDefaultButton(loginButton);
        loginButtonPanel.add(loginButton);

		loginButton.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                usernameTextField.setEditable(false);
                passwordTextField.setEditable(false);
                boolean authenticated = manager.authenticateUser(
                    usernameTextField.getText(),
                    passwordTextField.getText()
                );

                if (authenticated) {
                    onLogin.onEvent();
                    return;
                }

                usernameTextField.setEditable(true);
                passwordTextField.setEditable(true);

                JOptionPane.showMessageDialog(
                    LoginScreen.this, "Wrong username/password", "Bulletin Board",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.PAGE_AXIS));
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);

        cp.add(formPanel, "Center");
        cp.add(loginButtonPanel, "South");

        pack();
        setLocationRelativeTo(null);
    }

    public void setOnLoginEventListener(EventListener onLogin) {
        this.onLogin = onLogin;
    }
}
