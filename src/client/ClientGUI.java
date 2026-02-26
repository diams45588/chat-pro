package src.client;

import src.common.Constants;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Date;
import java.util.List;

public class ClientGUI extends JFrame {
    private ChatClient client;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextField usernameField;
    private JTextField serverField;
    private JTextField portField;
    private JButton serverChatButton;
    private JLabel statusLabel;

    // Couleurs modernes
    private final Color PRIMARY_DARK = new Color(18, 18, 20);
    private final Color PRIMARY_MEDIUM = new Color(30, 31, 34);
    private final Color ACCENT_BLUE = new Color(0, 122, 255);
    private final Color ACCENT_GREEN = new Color(40, 205, 65);
    private final Color ACCENT_RED = new Color(255, 59, 48);
    private final Color ACCENT_PURPLE = new Color(175, 82, 222);
    private final Color MESSAGE_MINE = new Color(0, 122, 255);
    private final Color MESSAGE_OTHER = new Color(40, 40, 45);
    private final Color TEXT_WHITE = new Color(240, 240, 245);
    private final Color TEXT_GRAY = new Color(140, 140, 150);
    private final Color PANEL_BG = new Color(25, 25, 30);

    public ClientGUI() {
        setLookAndFeel();
        initComponents();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Label.foreground", TEXT_WHITE);
            UIManager.put("Panel.background", PRIMARY_DARK);
        } catch (Exception e) {}
    }

    private void initComponents() {
        setTitle("✨ CHAT - Application de Messagerie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PRIMARY_DARK);

        // En-tête avec gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panneau principal
        JSplitPane splitPane = createMainSplitPane();
        add(splitPane, BorderLayout.CENTER);

        // Barre d'état
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        setSize(1300, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 122, 255),
                        getWidth(), 0, new Color(175, 82, 222)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Effet de brillance
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRect(0, 0, getWidth(), getHeight() / 2);

                g2d.dispose();
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 90));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Logo et titre
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("💬");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        iconLabel.setForeground(Color.WHITE);
        titlePanel.add(iconLabel);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Chat Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        textPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Communication en temps réel");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        textPanel.add(subtitleLabel);

        titlePanel.add(textPanel);
        header.add(titlePanel, BorderLayout.WEST);

        return header;
    }

    private JSplitPane createMainSplitPane() {
        // Panneau de chat
        JPanel chatPanel = createChatPanel();

        // Panneau latéral
        JPanel sidePanel = createSidePanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatPanel, sidePanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBackground(PRIMARY_DARK);

        return splitPane;
    }

    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 15));

        // Zone de chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(PANEL_BG);
        chatArea.setForeground(TEXT_WHITE);
        chatArea.setCaretColor(TEXT_WHITE);
        chatArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createEmptyBorder());
        chatScroll.getViewport().setBackground(PANEL_BG);
        chatScroll.setBackground(PANEL_BG);

        panel.add(chatScroll, BorderLayout.CENTER);

        // Panneau d'envoi
        JPanel sendPanel = createSendPanel();
        panel.add(sendPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Dans createSendPanel(), assurez-vous que le champ est bien créé
    private JPanel createSendPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setBackground(PANEL_BG);
        messageField.setForeground(TEXT_WHITE);
        messageField.setCaretColor(TEXT_WHITE);
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        messageField.addActionListener(e -> sendPublicMessage());
        messageField.setEnabled(false); // Désactivé au départ

        // ✅ AJOUTER UN LISTENER POUR DÉBOGUER
        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("🔍 Champ a le focus");
            }
            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("🔍 Champ a perdu le focus");
            }
        });

        sendButton = new JButton("📤") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_BLUE.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_BLUE.brighter());
                } else {
                    g2.setColor(ACCENT_BLUE);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        sendButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        sendButton.setForeground(Color.WHITE);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(70, 55));
        sendButton.setEnabled(false); // Désactivé au départ
        sendButton.addActionListener(e -> sendPublicMessage());

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 20));

        // Panneau de connexion
        JPanel connectionPanel = createConnectionPanel();
        panel.add(connectionPanel, BorderLayout.NORTH);

        // Panneau des utilisateurs
        JPanel usersPanel = createUsersPanel();
        panel.add(usersPanel, BorderLayout.CENTER);

        // Bouton chat serveur
        serverChatButton = createStyledButton("🎮 CHAT AVEC SERVEUR", ACCENT_PURPLE);
        serverChatButton.setEnabled(false);
        serverChatButton.addActionListener(e -> {
            if (client != null) client.openPrivateChat("Serveur");
        });
        panel.add(serverChatButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Serveur
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel serverLabel = new JLabel("🌐 Serveur");
        serverLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        serverLabel.setForeground(TEXT_GRAY);
        panel.add(serverLabel, gbc);

        gbc.gridx = 1;
        serverField = new JTextField(Constants.SERVER_HOST, 10);
        serverField.setBackground(PRIMARY_DARK);
        serverField.setForeground(TEXT_WHITE);
        serverField.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        panel.add(serverField, gbc);

        // Port
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel portLabel = new JLabel("🔌 Port");
        portLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        portLabel.setForeground(TEXT_GRAY);
        panel.add(portLabel, gbc);

        gbc.gridx = 1;
        portField = new JTextField(String.valueOf(Constants.PORT), 5);
        portField.setBackground(PRIMARY_DARK);
        portField.setForeground(TEXT_WHITE);
        portField.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        panel.add(portField, gbc);

        // Pseudo
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel userLabel = new JLabel("👤 Pseudo");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(TEXT_GRAY);
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(10);
        usernameField.setBackground(PRIMARY_DARK);
        usernameField.setForeground(TEXT_WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        panel.add(usernameField, gbc);

        // Boutons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        connectButton = createStyledButton("✅ CONNEXION", ACCENT_GREEN);
        connectButton.addActionListener(e -> connect());

        disconnectButton = createStyledButton("❌ DÉCONNEXION", ACCENT_RED);
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(e -> disconnect());

        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);

        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 35));

        return button;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // En-tête
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("👥 UTILISATEURS EN LIGNE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);

        // Liste des utilisateurs
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setCellRenderer(new ModernUserListRenderer());
        userList.setBackground(PANEL_BG);
        userList.setForeground(TEXT_WHITE);
        userList.setSelectionBackground(new Color(0, 122, 255, 80));
        userList.setSelectionForeground(TEXT_WHITE);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userList.setFixedCellHeight(45);
        userList.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && client != null) {
                    String selected = userList.getSelectedValue();
                    if (selected != null && !selected.contains("(Moi)")) {
                        String clean = selected.replace(" (Serveur)", "").replace(" (Moi)", "");
                        client.openPrivateChat(clean);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(userList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(PANEL_BG);
        scroll.getViewport().setBackground(PANEL_BG);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private class ModernUserListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            if (isSelected) {
                label.setBackground(new Color(0, 122, 255, 80));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(PANEL_BG);
                label.setForeground(TEXT_WHITE);
            }

            String text = value.toString();
            if (text.contains("Serveur")) {
                label.setText("👑 " + text);
            } else if (text.contains("(Moi)")) {
                label.setText("⭐ " + text);
            } else {
                label.setText("👤 " + text);
            }

            return label;
        }
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 15, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        statusLabel = new JLabel("⚡ Déconnecté");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ACCENT_RED);
        panel.add(statusLabel, BorderLayout.WEST);

        JLabel versionLabel = new JLabel("Chat Pro v2.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(TEXT_GRAY);
        panel.add(versionLabel, BorderLayout.EAST);

        return panel;
    }

    private void connect() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            appendMessage("❌ Entrez un pseudo");
            return;
        }

        try {
            int port = Integer.parseInt(portField.getText().trim());
            client = new ChatClient(this);

            if (client.connect(serverField.getText().trim(), port, username)) {
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);

                // ✅ FORCER l'activation de la saisie
                SwingUtilities.invokeLater(() -> {
                    messageField.setEnabled(true);
                    messageField.setEditable(true);
                    messageField.setBackground(new Color(255, 255, 255)); // Blanc
                    messageField.setForeground(Color.BLACK);
                    messageField.setCaretColor(Color.BLACK);
                    messageField.requestFocus();
                    messageField.requestFocusInWindow();

                    sendButton.setEnabled(true);

                    // ✅ Vérification
                    System.out.println("🔍 Champ activé? " + messageField.isEnabled());
                    System.out.println("🔍 Bouton activé? " + sendButton.isEnabled());
                });

                usernameField.setEnabled(false);
                serverField.setEnabled(false);
                portField.setEnabled(false);

                statusLabel.setText("✅ Connecté en tant que " + username);
                statusLabel.setForeground(ACCENT_GREEN);
                appendMessage("✅ Connecté en tant que " + username);
            }
        } catch (NumberFormatException e) {
            appendMessage("❌ Port invalide");
        }
    }
    private void disconnect() {
        if (client != null) client.disconnect();
    }

    public void connectionLost() {
        SwingUtilities.invokeLater(() -> {
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);

            // ✅ DÉSACTIVER la saisie
            messageField.setEnabled(false);
            messageField.setEditable(false);
            sendButton.setEnabled(false);
            serverChatButton.setEnabled(false);

            usernameField.setEnabled(true);
            serverField.setEnabled(true);
            portField.setEnabled(true);
            userListModel.clear();
            statusLabel.setText("⚡ Déconnecté");
            statusLabel.setForeground(ACCENT_RED);
            appendMessage("❌ Déconnecté");
        });
    }
    private void sendPublicMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && client != null && client.isConnected()) {
            client.sendPublicMessage(message);
            messageField.setText("");
        }
    }

    // Dans votre classe ClientGUI, remplacez toute la méthode appendMessage
    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Ajouter le message
                chatArea.append(message + "\n");

                // Forcer le défilement automatique
                chatArea.setCaretPosition(chatArea.getDocument().getLength());

                // Rafraîchir l'affichage
                chatArea.revalidate();
                chatArea.repaint();

                // DEBUG - Vérifier dans la console
                System.out.println("✅ Message affiché: " + message);

            } catch (Exception e) {
                System.err.println("❌ Erreur affichage: " + e.getMessage());
            }
        });
    }
    public void updateUserList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            userListModel.addElement("Serveur");

            for (String user : users) {
                if (!user.equals(client.getUsername())) {
                    userListModel.addElement(user);
                }
            }

            if (client != null && client.getUsername() != null) {
                userListModel.addElement(client.getUsername() + " (Moi)");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
    }
    // Bouton de test pour forcer l'activation
    private void addTestButton() {
        JButton testButton = new JButton("🔧 TEST");
        testButton.addActionListener(e -> {
            messageField.setEnabled(true);
            messageField.setEditable(true);
            messageField.setBackground(Color.WHITE);
            messageField.setForeground(Color.BLACK);
            sendButton.setEnabled(true);
            messageField.requestFocus();
            System.out.println("✅ TEST - Saisie activée manuellement");
        });

        // Ajoutez ce bouton temporairement dans votre interface
        // Par exemple dans le panneau de connexion
        // connectionPanel.add(testButton);
    }
}