import java.awt.*;
import javax.swing.*;


class RegisterScreen extends JFrame {
    private BulletinBoardClientManager manager;
    private EventListener onLogin;

    public RegisterScreen(BulletinBoardClientManager manager, EventListener onLogin) {
        this.manager = manager;
        this.onLogin = onLogin;
        createGUI();
    }

    public void createGUI() {
		setTitle("Bulletin Board");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
		passwordPanel.setLayout(new FlowLayout ());

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);

        JLabel repeatPasswordLabel = new JLabel("Repeat password:");
        JTextField repeatPasswordTextField = new JPasswordField(12);

		JPanel repeatPasswordPanel = new JPanel();
		repeatPasswordPanel.setLayout(new FlowLayout ());

        repeatPasswordPanel.add(repeatPasswordLabel);
        repeatPasswordPanel.add(repeatPasswordTextField);

        JPanel registerButtonPanel = new JPanel();

        JButton registerButton = new JButton();
        registerButton.setText("Register");
        getRootPane().setDefaultButton(registerButton);
        registerButtonPanel.add(registerButton);

		registerButton.addActionListener(new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
                usernameTextField.setEditable(false);
                passwordTextField.setEditable(false);
                repeatPasswordTextField.setEditable(false);
                registerButton.setEnabled(false);
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();

                boolean passwordsMatch = password.equals(
                    repeatPasswordTextField.getText()
                );

                if (!passwordsMatch) {
                    JOptionPane.showMessageDialog(
                        RegisterScreen.this, "Passwords do not match", "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );

                    usernameTextField.setEditable(true);
                    passwordTextField.setEditable(true);
                    repeatPasswordTextField.setEditable(true);
                    registerButton.setEnabled(true);
                    return;
                }

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        RegisterScreen.this,
                        "Form cannot be empty.",
                        "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );
                    usernameTextField.setEditable(true);
                    passwordTextField.setEditable(true);
                    repeatPasswordTextField.setEditable(true);
                    registerButton.setEnabled(true);
                    return;

                }

                if (manager.isUsernameTaken(username, null)) {
                    JOptionPane.showMessageDialog(
                        RegisterScreen.this,
                        "Username \"" + username + "\" is taken.",
                        "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );
                    usernameTextField.setEditable(true);
                    passwordTextField.setEditable(true);
                    repeatPasswordTextField.setEditable(true);
                    registerButton.setEnabled(true);
                    return;
                }

                User registered = manager.createUser(
                    usernameTextField.getText(),
                    passwordTextField.getText()
                );


                if (registered == null) {
                    JOptionPane.showMessageDialog(
                        RegisterScreen.this, "Cannot register. Try again later.", "Bulletin Board",
                        JOptionPane.ERROR_MESSAGE
                    );

                    usernameTextField.setEditable(true);
                    passwordTextField.setEditable(true);
                    repeatPasswordTextField.setEditable(true);
                    registerButton.setEnabled(true);
                    return;
                }

                manager.authenticateUser(
                    usernameTextField.getText(),
                    passwordTextField.getText()
                );

                if (onLogin != null) {
                    onLogin.onEvent();
                }

                dispatchEvent(new java.awt.event.WindowEvent(
                    RegisterScreen.this,
                    java.awt.event.WindowEvent.WINDOW_CLOSING
                ));
            }
        });

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.PAGE_AXIS));
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(repeatPasswordPanel);

        cp.add(formPanel, "Center");
        cp.add(registerButtonPanel, "South");

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }
}
