package src.client;

import src.common.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class PrivateChatHandler {
    private ChatClient client;
    private String recipient;
    private JFrame chatFrame;
    private JTextArea chatArea;
    private JTextField messageField;

    private final Color PRIMARY_DARK = new Color(25, 25, 30);
    private final Color PANEL_BG = new Color(30, 31, 36);
    private final Color ACCENT_BLUE = new Color(0, 122, 255);
    private final Color TEXT_WHITE = new Color(240, 240, 245);

    public PrivateChatHandler(ChatClient client, String recipient) {
        this.client = client;
        this.recipient = recipient;
        initGUI();
    }

    private void initGUI() {
        chatFrame = new JFrame();
        chatFrame.getContentPane().setBackground(PRIMARY_DARK);
        chatFrame.setUndecorated(true);
        chatFrame.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 450, 500, 20, 20));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PRIMARY_DARK);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65), 1));

        // En-tête
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Zone de chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatArea.setBackground(PANEL_BG);
        chatArea.setForeground(TEXT_WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(PANEL_BG);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Panneau d'envoi
        JPanel sendPanel = createSendPanel();
        mainPanel.add(sendPanel, BorderLayout.SOUTH);

        chatFrame.add(mainPanel);
        chatFrame.setSize(450, 500);
        chatFrame.setLocationRelativeTo(null);

        chatFrame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ACCENT_BLUE);
        panel.setPreferredSize(new Dimension(0, 60));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        String icon = recipient.equals("Serveur") ? "🎮" : "👤";
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(Color.WHITE);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel(recipient);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        panel.add(titlePanel, BorderLayout.WEST);

        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(255, 255, 255, 50));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(50, 60));
        closeButton.addActionListener(e -> chatFrame.dispose());

        panel.add(closeButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSendPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageField.setBackground(PANEL_BG);
        messageField.setForeground(TEXT_WHITE);
        messageField.setCaretColor(TEXT_WHITE);
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        messageField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("📤");
        sendButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        sendButton.setBackground(ACCENT_BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(60, 45));
        sendButton.addActionListener(e -> sendMessage());

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private void sendMessage() {
        String content = messageField.getText().trim();
        if (!content.isEmpty()) {
            // ✅ AFFICHER UNE SEULE FOIS ici
            appendMessage("[Vous à " + recipient + "]: " + content);

            // ✅ Envoyer au client (NE PAS réafficher dans receiveMessage)
            if (recipient.equals("Serveur")) {
                client.sendMessageToServer(content);
            } else {
                client.sendPrivateMessage(recipient, content);
            }

            messageField.setText("");
        }
    }

    // ✅ Cette méthode est appelée par sendPrivateMessage mais on n'affiche RIEN
    // car on a déjà affiché dans sendMessage()
    public void displaySentMessage(String content) {
        // NE RIEN FAIRE - L'affichage a déjà été fait dans sendMessage()
        System.out.println("🔍 displaySentMessage ignoré pour éviter doublon");
    }

    public void receiveMessage(Message message) {
        String time = new java.text.SimpleDateFormat("HH:mm").format(new Date());

        if (message.getType().equals(Constants.MSG_SERVER_PRIVATE)) {
            appendMessage("🔵 [Serveur]: " + message.getContent());
        } else {
            // ✅ Message d'un AUTRE client
            appendMessage("[" + message.getSender() + "]: " + message.getContent());
        }
        chatFrame.toFront();
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = new java.text.SimpleDateFormat("HH:mm").format(new Date());
            chatArea.append("[" + time + "] " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public void show() {
        chatFrame.setVisible(true);
        chatFrame.toFront();
    }

    public void dispose() {
        if (chatFrame != null) {
            chatFrame.dispose();
        }
    }
}