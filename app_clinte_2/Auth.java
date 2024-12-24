import javax.swing.*;
import java.awt.*;

public class Auth extends JFrame {
    public void loginWithEmailAndPassword() {
        setTitle(Constant.kAppName);
        setSize(450, 450);
        getContentPane().setBackground(Color.white);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // center form
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel usernameLabel = new JLabel("Username", SwingConstants.RIGHT);
        JLabel passwordLabel = new JLabel("Password", SwingConstants.RIGHT);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Submit");

        JLabel imageLabel = new JLabel();
        ImageIcon imageLogoDtu = new ImageIcon(Constant.kDtuLogo);
        Image image = imageLogoDtu.getImage();
        Image newImg = image.getScaledInstance(290, 90, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(newImg));

        JPanel container1 = new JPanel(new GridLayout(2, 2, 10, 10));

        container1.setBackground(Color.white);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);

        container1.add(usernameLabel);
        container1.add(usernameField);
        container1.add(passwordLabel);
        container1.add(passwordField);

        imageLabel.setBounds(60, 40, 290, 90);

        container1.setBounds(-60, 170, 400, 65);

        loginButton.setBounds(145, 260, 195, 25);

        add(imageLabel);
        add(container1);
        add(loginButton);

        setLayout(null);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "The username entry field is empty",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "The password entry field is empty",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!username.equals("dtu")) {
                JOptionPane.showMessageDialog(
                        this, "Username is incorrect",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals("123")) {
                JOptionPane.showMessageDialog(
                        this, "Password is incorrect",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                setVisible(false);
                AddDetailsInterface customInterface = new AddDetailsInterface();
                customInterface.showInterface();
            }
        });
        setVisible(true);
    }
}
