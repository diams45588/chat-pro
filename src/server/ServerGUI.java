package src.server;

import src.common.Constants;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

public class ServerGUI extends JFrame {
    private ChatServer server;
    private JTextArea logArea;
    private JButton startButton;
    private JButton stopButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JTextField messageField;
    private JComboBox<String> messageTypeCombo;
    private Thread serverThread;
    private Timer updateTimer;
    private Map<String, ServerPrivateChatWindow> privateChatWindows;

    // Couleurs modernes
    private final Color PRIMARY_DARK = new Color(32, 33, 36);
    private final Color PRIMARY_MEDIUM = new Color(41, 42, 45);
    private final Color ACCENT_BLUE = new Color(66, 133, 244);
    private final Color ACCENT_GREEN = new Color(52, 168, 83);
    private final Color ACCENT_RED = new Color(234, 67, 53);
    private final Color ACCENT_ORANGE = new Color(251, 188, 5);
    private final Color TEXT_WHITE = new Color(232, 234, 237);
    private final Color TEXT_GRAY = new Color(154, 160, 166);
    private final Color PANEL_BG = new Color(53, 54, 58);

    public ServerGUI() {
        this.privateChatWindows = new HashMap<>();
        setLookAndFeel();
        initComponents();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Label.foreground", TEXT_WHITE);
            UIManager.put("Panel.background", PRIMARY_DARK);
            UIManager.put("OptionPane.background", PRIMARY_DARK);
            UIManager.put("OptionPane.messageForeground", TEXT_WHITE);
        } catch (Exception e) {}
    }

    private void initComponents() {
        setTitle("🔥 SERVEUR DE CHAT - GESTIONNAIRE PROFESSIONNEL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PRIMARY_DARK);

        // En-tête avec dégradé
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panneau principal avec séparation
        JSplitPane splitPane = createMainSplitPane();
        add(splitPane, BorderLayout.CENTER);

        // Barre d'état
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        setSize(1300, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé horizontal
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(66, 133, 244),
                        getWidth(), 0, new Color(52, 168, 83)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.dispose();
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo et titre
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("⚡");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setForeground(Color.WHITE);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Serveur de Chat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JLabel versionLabel = new JLabel("v2.0 • Mode 3-en-1");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versionLabel.setForeground(new Color(255, 255, 255, 200));
        titlePanel.add(versionLabel);

        header.add(titlePanel, BorderLayout.WEST);

        // Statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);

        addStatLabel(statsPanel, "🟢", "En ligne", "0");
        addStatLabel(statsPanel, "📊", "Uptime", "00:00");

        header.add(statsPanel, BorderLayout.EAST);

        return header;
    }

    private void addStatLabel(JPanel panel, String icon, String text, String value) {
        JLabel label = new JLabel(icon + " " + value + " " + text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        panel.add(label);
    }

    private JSplitPane createMainSplitPane() {
        // Panneau de gauche - Logs
        JPanel logPanel = createLogPanel();

        // Panneau de droite - Utilisateurs et contrôle
        JPanel rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, logPanel, rightPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBackground(PRIMARY_DARK);

        return splitPane;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

        // Titre avec icône
        JLabel titleLabel = new JLabel("📋 CONSOLE DE LOGS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Zone de logs stylisée
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(PANEL_BG);
        logArea.setForeground(TEXT_WHITE);
        logArea.setCaretColor(TEXT_WHITE);
        logArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(66, 66, 66), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(PANEL_BG);
        scroll.setBackground(PANEL_BG);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));

        // Panneau des utilisateurs
        JPanel usersPanel = createUsersPanel();
        panel.add(usersPanel, BorderLayout.CENTER);

        // Panneau de contrôle
        JPanel controlPanel = createControlPanel();
        panel.add(controlPanel, BorderLayout.NORTH);

        // Panneau d'envoi
        JPanel sendPanel = createSendPanel();
        panel.add(sendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(66, 66, 66), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // En-tête
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("👥 CLIENTS CONNECTÉS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel countLabel = new JLabel("0 en ligne");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(ACCENT_GREEN);
        header.add(countLabel, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        // Liste des utilisateurs avec rendu personnalisé
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setCellRenderer(new ModernUserListRenderer());
        userList.setBackground(PANEL_BG);
        userList.setForeground(TEXT_WHITE);
        userList.setSelectionBackground(new Color(66, 133, 244, 100));
        userList.setSelectionForeground(TEXT_WHITE);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userList.setFixedCellHeight(40);
        userList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = userList.getSelectedValue();
                    if (selected != null) {
                        openPrivateChat(selected);
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

            label.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            if (isSelected) {
                label.setBackground(new Color(66, 133, 244, 180));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(PANEL_BG);
                label.setForeground(TEXT_WHITE);
            }

            // Ajouter une icône selon le statut
            if (value.toString().equals("Serveur")) {
                label.setText("👑 " + value);
            } else {
                label.setText("👤 " + value);
            }

            return label;
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        startButton = createStyledButton("🚀 DÉMARRER", ACCENT_GREEN);
        startButton.addActionListener(e -> startServer());

        stopButton = createStyledButton("⛔ ARRÊTER", ACCENT_RED);
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopServer());

        panel.add(startButton);
        panel.add(stopButton);

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

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 45));

        return button;
    }

    private JPanel createSendPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(66, 66, 66), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Type de message
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        typePanel.setOpaque(false);

        JLabel typeLabel = new JLabel("📨 TYPE:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeLabel.setForeground(TEXT_GRAY);
        typePanel.add(typeLabel);

        messageTypeCombo = new JComboBox<>(new String[]{"🌐 PUBLIC", "🔒 PRIVÉ"});
        messageTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageTypeCombo.setBackground(PANEL_BG);
        messageTypeCombo.setForeground(TEXT_WHITE);
        ((JComponent) messageTypeCombo.getRenderer()).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        typePanel.add(messageTypeCombo);

        JButton privateChatBtn = createStyledButton("💬 OUVRIR CHAT PRIVÉ", ACCENT_ORANGE);
        privateChatBtn.setPreferredSize(new Dimension(180, 35));
        privateChatBtn.addActionListener(e -> {
            String selected = userList.getSelectedValue();
            if (selected != null) openPrivateChat(selected);
            else JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur");
        });
        typePanel.add(privateChatBtn);

        panel.add(typePanel, BorderLayout.NORTH);

        // Champ de message
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageField.setBackground(new Color(30, 31, 34));
        messageField.setForeground(TEXT_WHITE);
        messageField.setCaretColor(TEXT_WHITE);
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(66, 66, 66), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        messageField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("📤 ENVOYER") {
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
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sendButton.setForeground(Color.WHITE);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(120, 45));
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(24, 25, 28));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel statusLabel = new JLabel("⚡ Prêt à démarrer");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_GRAY);
        panel.add(statusLabel, BorderLayout.WEST);

        JLabel portLabel = new JLabel("Port: " + Constants.PORT);
        portLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        portLabel.setForeground(TEXT_GRAY);
        panel.add(portLabel, BorderLayout.EAST);

        return panel;
    }

    private void startServer() {
        server = new ChatServer();
        server.setGUI(this);

        serverThread = new Thread(() -> server.start(Constants.PORT));
        serverThread.start();

        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        updateTimer = new Timer(2000, e -> {
            if (server != null) updateUserList(server.getUserList());
        });
        updateTimer.start();

        appendLog("✅ Serveur démarré");
    }

    private void stopServer() {
        if (updateTimer != null) updateTimer.stop();

        for (ServerPrivateChatWindow window : privateChatWindows.values()) {
            window.dispose();
        }
        privateChatWindows.clear();

        if (server != null) {
            server.sendServerPublicMessage("🔴 Le serveur s'arrête");
            new Thread(() -> {
                try { Thread.sleep(500); } catch (Exception e) {}
                server.stop();
                server = null;

                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    userListModel.clear();
                    appendLog("⛔ Serveur arrêté");
                });
            }).start();
        }
    }

    private void openPrivateChat(String username) {
        if (!privateChatWindows.containsKey(username)) {
            ServerPrivateChatWindow window = new ServerPrivateChatWindow(server, username, this);
            privateChatWindows.put(username, window);
        } else {
            privateChatWindows.get(username).toFront();
        }
    }

    public void receivePrivateMessageFromClient(String sender, String content) {
        SwingUtilities.invokeLater(() -> {
            if (!privateChatWindows.containsKey(sender)) {
                openPrivateChat(sender);
            }
            privateChatWindows.get(sender).appendMessage("[" + sender + "]: " + content);
        });
    }

    public void closePrivateChatWindow(String username) {
        privateChatWindows.remove(username);
    }

    public void updateUserList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            String current = userList.getSelectedValue();
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
            if (current != null && userListModel.contains(current)) {
                userList.setSelectedValue(current, true);
            }
        });
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty() || server == null) return;

        if (messageTypeCombo.getSelectedItem().toString().contains("PUBLIC")) {
            server.sendServerPublicMessage(message);
        } else {
            String selected = userList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur");
                return;
            }
            server.sendServerPrivateMessage(selected, message);
            if (privateChatWindows.containsKey(selected)) {
                privateChatWindows.get(selected).appendMessage("[Serveur]: " + message);
            }
        }
        messageField.setText("");
    }

    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());

            // Colorer les logs selon le type
            String coloredMessage = message;
            if (message.contains("✅")) coloredMessage = "✅ " + message.substring(2);
            else if (message.contains("❌")) coloredMessage = "❌ " + message.substring(2);
            else if (message.contains("📩")) coloredMessage = "📩 " + message.substring(2);
            else if (message.contains("📢")) coloredMessage = "📢 " + message.substring(2);

            logArea.append("[" + time + "] " + coloredMessage + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
    }
}