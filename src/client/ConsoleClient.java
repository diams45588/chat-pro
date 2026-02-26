package src.client;

// import common.Protocol;

// If Protocol.java is in the same directory as ConsoleClient.java, use:
// import scr.Client.Protocol;

// If Protocol.java is in another package, adjust the import to the correct package name, e.g.:
import src.common.Protocol;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ConsoleClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        CHAT CLIENT - CONSOLE          ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Configuration
        System.out.print("Adresse du serveur [" + Protocol.DEFAULT_HOST + "]: ");
        String host = scanner.nextLine();
        if (host.isEmpty()) host = Protocol.DEFAULT_HOST;
        
        System.out.print("Port [" + Protocol.DEFAULT_PORT + "]: ");
        String portStr = scanner.nextLine();
        int port = portStr.isEmpty() ? Protocol.DEFAULT_PORT : Integer.parseInt(portStr);
        
        System.out.print("Votre nom d'utilisateur: ");
        String username = scanner.nextLine();
        
        try {
            // Connexion
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Envoyer le username
            out.println(username);
            
            // Lire la réponse initiale
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
                if (response.contains("Connecté") || response.contains("Commandes")) {
                    break;
                }
            }
            
            // Thread pour recevoir les messages
            Thread receiver = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("\n⚠️  Déconnecté du serveur");
                }
            });
            receiver.start();
            
            // Boucle d'envoi
            System.out.println("\nTapez vos messages (/" + "help pour l'aide):");
            System.out.print("> ");
            
            while (true) {
                String message = scanner.nextLine();
                
                if (message.equalsIgnoreCase(Protocol.CMD_QUIT)) {
                    out.println(Protocol.CMD_QUIT);
                    break;
                }
                
                out.println(message);
                System.out.print("> ");
            }
            
            // Nettoyage
            receiver.interrupt();
            socket.close();
            System.out.println("Au revoir!");
            
        } catch (IOException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}