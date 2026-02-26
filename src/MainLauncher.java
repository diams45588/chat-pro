package src;

import src.server.ServerGUI;
import src.client.ClientGUI;

import javax.swing.*;

public class MainLauncher {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "server":
                startServer();
                break;
            case "client":
                startClient();
                break;
            default:
                printUsage();
        }
    }

    private static void startServer() {
        System.out.println("🚀 Démarrage du serveur...");
        SwingUtilities.invokeLater(() -> {
            new ServerGUI().setVisible(true);
        });
    }

    private static void startClient() {
        System.out.println("🚀 Démarrage du client...");
        SwingUtilities.invokeLater(() -> {
            new ClientGUI().setVisible(true);
        });
    }

    private static void printUsage() {
        System.out.println("=== Chat Application ===");
        System.out.println("Usage:");
        System.out.println("  java MainLauncher server    - Démarrer le serveur");
        System.out.println("  java MainLauncher client    - Démarrer le client");
        System.out.println();
        System.out.println("Exemples:");
        System.out.println("  java MainLauncher server");
        System.out.println("  java MainLauncher client");
    }
}