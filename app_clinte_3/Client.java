import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Client {
    private Socket clientSocket;
    private DataInputStream clientRead;
    private DataOutputStream clientWrite;

    void ConnectToTheServer() {
        try {
            clientSocket = new Socket("localhost", 30000);
            clientRead = new DataInputStream(clientSocket.getInputStream());
            clientWrite = new DataOutputStream(clientSocket.getOutputStream());
            new Thread(() -> {
                try {
                    String receivedMessage = "";
                    while (!receivedMessage.equals("End")) {
                        receivedMessage = clientRead.readUTF();
                        int newMessageIndex = receivedMessage.indexOf("\n");
                        String newMessageString = receivedMessage.substring(0, newMessageIndex);
                        String newMessageDateString = receivedMessage.substring(newMessageIndex + 1);
                        appendMessage(CustomInterface.fullName + ": " + newMessageString + "\n" + newMessageDateString
                                + "\n", true);
                    }
                } catch (IOException e) {
                    Constant.kShowErrorException();
                }
            }).start();
        } catch (IOException e) {
            Constant.kShowErrorException();
        }
    }

    void sendMessageButton() {
        new Thread(() -> {
            try {
                String messageToSend = CustomInterface.messageField.getText();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                if (!messageToSend.equals("End")) {
                    appendMessage(AddDetailsInterface.fullNameField.getText() + ": " + messageToSend + "\n"
                            + sdf.format(cal.getTime()) + "\n", false);
                    clientWrite.writeUTF(messageToSend + "\n" + sdf.format(cal.getTime()));
                    clientWrite.flush();
                }
                CustomInterface.messageField.setText("");
            } catch (IOException e) {
                Constant.kShowErrorException();
            }
        }).start();
    }

    private void appendMessage(String message, boolean isServer) {
        SwingUtilities.invokeLater(() -> {
            SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
            StyleConstants.setForeground(messageAttributes, isServer ? Color.BLUE : Color.RED);
            StyleConstants.setFontSize(messageAttributes, 14); // Set font size for message

            SimpleAttributeSet dateAttributes = new SimpleAttributeSet();
            StyleConstants.setForeground(dateAttributes, Color.GRAY);
            StyleConstants.setFontSize(dateAttributes, 12); // Set font size for date

            // Create paragraph style
            StyleConstants.setAlignment(messageAttributes,
                    isServer ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            StyleConstants.setAlignment(dateAttributes,
                    isServer ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            StyleConstants.setSpaceBelow(messageAttributes, 5); // Add some space between paragraphs

            // Apply paragraph style to the message
            AttributeSet paragraphStyle = CustomInterface.messagePane.getParagraphAttributes();
            SimpleAttributeSet newParagraphStyle = new SimpleAttributeSet(paragraphStyle);
            StyleConstants.setAlignment(newParagraphStyle,
                    isServer ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            CustomInterface.messagePane.setParagraphAttributes(newParagraphStyle, true);

            // Split message into content and date
            String[] parts = message.split("\n");
            String content = parts[0];
            String date = parts[1];

            // Append message content to the JTextPane
            Document doc = CustomInterface.messagePane.getDocument();
            try {
                doc.insertString(doc.getLength(), content, messageAttributes);
                // Insert new line
                doc.insertString(doc.getLength(), "\n", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            // Append date to the JTextPane with smaller font size
            try {
                doc.insertString(doc.getLength(), date + "\n", dateAttributes);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

}