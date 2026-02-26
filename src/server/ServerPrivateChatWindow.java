package src.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerPrivateChatWindow extends JFrame {
    private ChatServer server;
    private String clientUsername;
    private ServerGUI serverGUI;
    private JTextArea chatArea;
    private JTextField messageField;

    public ServerPrivateChatWindow(ChatServer server, String clientUsername, ServerGUI serverGUI) {
        this.server = server;
        this.clientUsername = clientUsername;
        this.serverGUI = serverGUI;
        initGUI();
    }

    private void initGUI() {
        setTitle("💬 Chat avec " + clientUsername);
        setLayout(new BorderLayout());
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(chatArea);

        JPanel sendPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Envoyer");
        sendButton.addActionListener(e -> sendMessage());

        sendPanel.add(messageField, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);

        add(scroll, BorderLayout.CENTER);
        add(sendPanel, BorderLayout.SOUTH);

        appendMessage("🔵 Chat avec " + clientUsername + " démarré");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                serverGUI.closePrivateChatWindow(clientUsername);
            }
        });

        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && server != null) {
            appendMessage("[Vous]: " + message);
            server.sendServerPrivateMessage(clientUsername, message);
            messageField.setText("");
        }
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}