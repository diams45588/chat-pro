package src.server;

import src.common.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private Map<String, ClientHandler> clientMap;
    private ExecutorService pool;
    private ServerGUI gui;

    public ChatServer() {
        clients = new CopyOnWriteArrayList<>();
        clientMap = new ConcurrentHashMap<>();
        pool = Executors.newCachedThreadPool();
    }

    public void setGUI(ServerGUI gui) {
        this.gui = gui;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            log("✅ Serveur démarré sur le port " + port);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                pool.execute(clientHandler);
                log("🔵 Nouvelle connexion: " + clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                log("❌ Erreur: " + e.getMessage());
            }
        }
    }

    public void stop() {
        try {
            log("🛑 Arrêt du serveur...");

            for (ClientHandler client : clients) {
                client.close();
            }
            clients.clear();
            clientMap.clear();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            pool.shutdownNow();
            log("✅ Serveur arrêté");
        } catch (IOException e) {
            log("❌ Erreur arrêt: " + e.getMessage());
        }
    }

    public void registerClient(String username, ClientHandler handler) {
        clientMap.put(username, handler);
        log("✅ Client connecté: " + username);

        // Message de bienvenue
        handler.sendMessage(new Message(
                Constants.MSG_SERVER,
                "Serveur",
                username,
                "👋 Bienvenue " + username + "!"
        ));

        broadcastUserList();
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
        if (handler.getUsername() != null) {
            clientMap.remove(handler.getUsername());
            log("❌ Client déconnecté: " + handler.getUsername());
            broadcastUserList();
        }
    }

    public void broadcastUserList() {
        List<String> userList = new ArrayList<>(clientMap.keySet());
        String userListStr = String.join(",", userList);

        Message listMessage = new Message(
                Constants.MSG_LIST_USERS,
                "Serveur",
                "ALL",
                userListStr
        );

        for (ClientHandler client : clients) {
            client.sendMessage(listMessage);
        }

        if (gui != null) {
            gui.updateUserList(userList);
        }
    }

    public void broadcastPublicMessage(Message message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender && client.isConnected()) {
                client.sendMessage(message);
            }
        }
    }

    public void sendPrivateMessage(Message message) {
        ClientHandler recipient = clientMap.get(message.getRecipient());
        ClientHandler sender = clientMap.get(message.getSender());

        System.out.println("🔍 SERVEUR - Tentative envoi privé: " + message.getSender() + " → " + message.getRecipient());
        System.out.println("🔍 SERVEUR - Destinataire trouvé? " + (recipient != null));

        if (recipient != null && recipient.isConnected()) {
            // ✅ Envoyer UNIQUEMENT au destinataire
            recipient.sendMessage(message);
            log("🔒 Message privé: " + message.getSender() + " → " + message.getRecipient());
            System.out.println("🔍 SERVEUR - ✓ Message envoyé à " + message.getRecipient());
        } else {
            System.out.println("🔍 SERVEUR - ✗ Destinataire " + message.getRecipient() + " non trouvé");
            if (sender != null) {
                sender.sendMessage(new Message(
                        Constants.MSG_SERVER,
                        "Serveur",
                        sender.getUsername(),
                        "❌ " + message.getRecipient() + " n'est pas connecté"
                ));
            }
        }
    }
    public void sendServerPublicMessage(String content) {
        Message message = new Message(
                Constants.MSG_SERVER,
                "Serveur",
                "ALL",
                content
        );

        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
        log("📢 Message public: " + content);
    }

    public void log(String message) {
        System.out.println("[" + new java.util.Date() + "] " + message);
        if (gui != null) {
            gui.appendLog(message);
        }
    }

    public List<String> getUserList() {
        return new ArrayList<>(clientMap.keySet());
    }

    public static void main(String[] args) {
        new ChatServer().start(Constants.PORT);
    }

    public ServerGUI getGUI() {
        return this.gui;  // ✅ Retourne l'interface graphique du serveur
    }
    public List<ClientHandler> getAllClients() {
        return clients;
    }

    public void sendServerPrivateMessage(String recipient, String content) {
        ClientHandler client = clientMap.get(recipient);
        if (client != null && client.isConnected()) {
            client.sendMessage(new Message(
                    Constants.MSG_SERVER_PRIVATE,
                    "Serveur",
                    recipient,
                    content
            ));
            log("📩 Message serveur → " + recipient + ": " + content);
        } else {
            log("❌ Impossible d'envoyer à " + recipient + " (non connecté)");
        }
    }
}