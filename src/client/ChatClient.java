package src.client;

import src.common.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private ClientGUI gui;
    private boolean connected;
    private List<String> userList;
    private Map<String, PrivateChatHandler> privateChats;

    public ChatClient(ClientGUI gui) {
        this.gui = gui;
        this.userList = new ArrayList<>();
        this.privateChats = new HashMap<>();
    }

    public boolean connect(String host, int port, String username) {
        try {
            this.username = username;
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;

            sendMessage(new Message(Constants.MSG_LOGIN, username, "Serveur", "Login"));

            new Thread(this::receiveMessages).start();
            return true;
        } catch (IOException e) {
            gui.appendMessage("❌ Erreur de connexion: " + e.getMessage());
            return false;
        }
    }

    private void receiveMessages() {
        try {
            while (connected) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }
        } catch (Exception e) {
            disconnect();
        }
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case Constants.MSG_PRIVATE:
                System.out.println("🔍 CLIENT - Message PRIVÉ REÇU de " + message.getSender() + ": " + message.getContent());

                // ✅ Créer ou récupérer la fenêtre de chat
                PrivateChatHandler chat = privateChats.get(message.getSender());
                if (chat == null) {
                    chat = new PrivateChatHandler(this, message.getSender());
                    privateChats.put(message.getSender(), chat);
                }

                // ✅ Afficher le message reçu
                chat.receiveMessage(message);
                break;
            case Constants.MSG_PUBLIC:
                gui.appendMessage("[" + message.getSender() + "]: " + message.getContent());
                break;

            case Constants.MSG_SERVER:
                gui.appendMessage("🔵 [Serveur]: " + message.getContent());
                break;

            case Constants.MSG_SERVER_PRIVATE:
                // ✅ Message privé REÇU du serveur
                PrivateChatHandler serverChat = privateChats.get("Serveur");
                if (serverChat == null) {
                    serverChat = new PrivateChatHandler(this, "Serveur");
                    privateChats.put("Serveur", serverChat);
                }
                serverChat.receiveMessage(message);
                break;

            case Constants.MSG_LIST_USERS:
                updateUserList(message.getContent());
                break;
        }
    }

    private void updateUserList(String userListStr) {
        userList.clear();
        if (userListStr != null && !userListStr.isEmpty()) {
            userList.addAll(Arrays.asList(userListStr.split(",")));
        }
        gui.updateUserList(userList);
    }

    public void sendPublicMessage(String content) {
        Message message = new Message(Constants.MSG_PUBLIC, username, "ALL", content);
        sendMessage(message);
        gui.appendMessage("[Moi]: " + content);
    }

    public void sendPrivateMessage(String recipient, String content) {
        // ✅ 1. Récupérer ou créer la fenêtre
        PrivateChatHandler chat = privateChats.get(recipient);
        if (chat == null) {
            chat = new PrivateChatHandler(this, recipient);
            privateChats.put(recipient, chat);
        }

        // ✅ 2. ENVOYER le message (NE PAS AFFICHER ICI - c'est sendMessage qui affiche)
        Message message = new Message(Constants.MSG_PRIVATE, username, recipient, content);
        sendMessage(message);

        // ✅ 3. PLUS D'APPEL À displaySentMessage - c'est PrivateChatHandler.sendMessage() qui affiche
        // chat.displaySentMessage(content); // ← SUPPRIMEZ CETTE LIGNE

        System.out.println("🔍 Message privé envoyé à " + recipient);
    }
    public void sendMessageToServer(String content) {
        // ✅ 1. OUVRIRE/CREER la fenêtre de chat avec le serveur
        PrivateChatHandler serverChat = privateChats.get("Serveur");
        if (serverChat == null) {
            serverChat = new PrivateChatHandler(this, "Serveur");
            privateChats.put("Serveur", serverChat);
        }

        // ✅ 2. AFFICHER IMMÉDIATEMENT le message dans la fenêtre
        serverChat.displaySentMessage(content);

        // ✅ 3. ENVOYER le message
        Message message = new Message(Constants.MSG_SERVER, username, "Serveur", content);
        sendMessage(message);
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                sendMessage(new Message(Constants.MSG_LOGOUT, username, "Serveur", "Logout"));
                socket.close();
            }
        } catch (IOException e) {}

        for (PrivateChatHandler chat : privateChats.values()) {
            chat.dispose();
        }
        privateChats.clear();

        gui.connectionLost();
    }

    public String getUsername() { return username; }
    public boolean isConnected() { return connected; }
    public List<String> getUserList() { return new ArrayList<>(userList); }

    public void openPrivateChat(String recipient) {
        if (privateChats.containsKey(recipient)) {
            privateChats.get(recipient).show();
        } else {
            PrivateChatHandler chat = new PrivateChatHandler(this, recipient);
            privateChats.put(recipient, chat);
        }
    }
}