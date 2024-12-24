import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class AddDetailsInterface extends JFrame {
    private JLabel photoLabel;
    private JButton selectPhotoButton; // Button for selecting photo
    private JButton submitButton;
    private JPanel panel;
    private JLabel fullNameLabel;
    static JTextField fullNameField;
    private JLabel addphotoInformation;
    private final int PHOTO_SIZE = 150; // Size of the circular photo

    public void showInterface() {
        setTitle("Adding Information Page");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // center form
        setLocationRelativeTo(null);
        setResizable(false);

        panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(null);

        fullNameLabel = new JLabel("Full Name");
        fullNameField = new JTextField();
        submitButton = new JButton("Start");
        addphotoInformation = new JLabel("Add Your Information");
        addphotoInformation.setFont(new Font("Arial", Font.PLAIN, 20));
        // Create a button for selecting photo
        selectPhotoButton = new JButton(new ImageIcon(Constant.kDefulteCircleAvatar));
        selectPhotoButton.setContentAreaFilled(false);
        selectPhotoButton.setBorderPainted(false);
        selectPhotoButton.setFocusPainted(false);
        selectPhotoButton.setOpaque(false);

        selectPhotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(AddDetailsInterface.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    displaySelectedImage(selectedFile);
                }
            }
        });

        photoLabel = new JLabel("");
        fullNameLabel.setBounds(58, 260, 100, 25);
        fullNameField.setBounds(128, 260, 200, 25);
        photoLabel.setBounds(150, 65, PHOTO_SIZE, PHOTO_SIZE);
        selectPhotoButton.setBounds(150, 65, PHOTO_SIZE, PHOTO_SIZE);
        submitButton.setBounds(128, 310, 200, 25);
        addphotoInformation.setBounds(130, 15, 200, 20);

        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(Color.BLACK);

        submitButton.addActionListener(e -> {
            String fullName = fullNameField.getText();
            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "The fullName entry field is empty",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (fullName.length() < 3) {
                JOptionPane.showMessageDialog(
                        this, "The fullName is short",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    Socket socket = new Socket("localhost", 5000);
                    MultiClients client = new MultiClients(socket, fullName);
                    CustomInterface chatCustomInterface = new CustomInterface(client);
                    if (selectedImage != null) {
                        chatCustomInterface.setProfileImage(selectedImage);
                    }
                    CustomInterface.fullName = fullName;
                    setVisible(false);
                    chatCustomInterface.showInterface();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        panel.add(fullNameLabel);
        panel.add(fullNameField);
        panel.add(submitButton);
        panel.add(selectPhotoButton);
        panel.add(photoLabel);
        panel.add(addphotoInformation);
        add(panel);
        setVisible(true);
    }

    private ImageIcon selectedImage;

    private void displaySelectedImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file);

            // Scale image to fixed size
            Image scaledImg = img.getScaledInstance(PHOTO_SIZE, PHOTO_SIZE, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImg);
            selectedImage = icon; // Store the selected image
            // Create a circular mask for the image
            Image image = icon.getImage();
            BufferedImage bufferedImage = new BufferedImage(PHOTO_SIZE, PHOTO_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, PHOTO_SIZE, PHOTO_SIZE));
            g2.drawImage(image, 0, 0, PHOTO_SIZE, PHOTO_SIZE, null);
            g2.dispose();
            // Set the circular image as the icon of selectPhotoButton
            selectPhotoButton.setIcon(new ImageIcon(bufferedImage));
            // Set the circular image as the icon of photoLabel
            photoLabel.setIcon(new ImageIcon(bufferedImage));
            selectPhotoButton.setVisible(false); // Hide the select photo button after selecting photo
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}