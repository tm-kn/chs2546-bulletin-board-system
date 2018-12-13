import java.awt.*;
import javax.swing.*;


public class LoginScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private EventListener onLogin;

    public LoginScreen(BulletinBoardClientManager manager) {
        this.manager = manager;
        createGUI();
    }

    public void createGUI() {
		setTitle("Bulletin Board");


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        JTextField passwordTextField = new JPasswordField(12);

		JPanel passwordPanel = new JPanel();
		passwordPanel.setLayout (new FlowLayout ());

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);


        JButton registerButton = new JButton();
        registerButton.setText("Register a new account");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterScreen register = new RegisterScreen(manager, onLogin);
                register.setVisible(true);
            }
        });

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
                    if (onLogin != null) {
                        onLogin.onEvent();
                    }
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
        formPanel.add(registerButton);
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);

        cp.add(formPanel, "Center");
        cp.add(loginButtonPanel, "South");

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void setOnLoginEventListener(EventListener onLogin) {
        this.onLogin = onLogin;
    }
}
