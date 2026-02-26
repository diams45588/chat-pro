package src.server;

import src.common.*;
import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private boolean connected;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        this.connected = true;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            close();
        }
    }

    @Override
    public void run() {
        try {
            while (connected) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            close();
        }
    }

    private void handleMessage(Message message) {
        System.out.println("🔍 SERVEUR - Message reçu: " + message.getType() + " de " + message.getSender());

        switch (message.getType()) {
            case Constants.MSG_LOGIN:
                this.username = message.getSender();
                server.registerClient(username, this);
                break;

            case Constants.MSG_PUBLIC:
                System.out.println("🔍 SERVEUR - Message PUBLIC de " + message.getSender() + ": " + message.getContent());

                // ✅ AFFICHER dans l'interface du serveur
                if (server.getGUI() != null) {
                    server.getGUI().appendLog("📢 [" + message.getSender() + "]: " + message.getContent());
                }

                server.broadcastPublicMessage(message, this);
                break;

            case Constants.MSG_PRIVATE:
                System.out.println("🔍 SERVEUR - Message PRIVÉ de " + message.getSender() + " à " + message.getRecipient() + ": " + message.getContent());
                // ✅ Le serveur transmet UNIQUEMENT au destinataire
                server.sendPrivateMessage(message);
                break;

            case Constants.MSG_SERVER:
                server.log("📨 Message de " + username + " pour serveur: " + message.getContent());

                // ✅ Créer un Message pour la réponse
                Message response = new Message(
                        Constants.MSG_SERVER_PRIVATE,
                        "Serveur",
                        username,
                        "✅ Message reçu: \"" + message.getContent() + "\""
                );

                // ✅ Envoyer via sendServerPrivateMessage (pas sendPrivateMessage)
                server.sendServerPrivateMessage(username, "✅ Message reçu: \"" + message.getContent() + "\"");

                // Notifier l'interface serveur
                if (server.getGUI() != null) {
                    server.getGUI().receivePrivateMessageFromClient(username, message.getContent());
                }
                break;

            case Constants.MSG_LOGOUT:
                close();
                break;
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            close();
        }
    }

    public void close() {
        try {
            connected = false;
            if (socket != null && !socket.isClosed()) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {}
        server.removeClient(this);
    }

    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return connected;
    }
}