import java.awt.Color;
import java.io.*;
import java.net.Socket;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MultiClients {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public MultiClients(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            if (socket.isConnected()) {
                bufferedWriter.write(username + ": " + CustomInterface.messageField.getText());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        if (CustomInterface.messagePane.getText().isEmpty()) {
                            CustomInterface.messagePane
                                    .setText(messageFromGroupChat + "\n");
                        } else {
                            CustomInterface.messagePane
                                    .setText(
                                            CustomInterface.messagePane.getText() + "\n" + messageFromGroupChat + "\n");
                        }
                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void appendMessage(String message, boolean isServer) {
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
