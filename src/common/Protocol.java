package src.common;

public class Protocol {
    // Commandes spéciales
    public static final String CMD_PREFIX = "/";
    public static final String CMD_QUIT = "/quit";
    public static final String CMD_USERS = "/users";
    public static final String CMD_HELP = "/help";
    public static final String CMD_PRIVATE = "@";
    public static final String CMD_BROADCAST = "/broadcast";
    
    // Codes de statut
    public static final int STATUS_OK = 200;
    public static final int STATUS_ERROR = 500;
    public static final int STATUS_CONNECTED = 201;
    public static final int STATUS_DISCONNECTED = 202;
    
    // Configuration
    public static final int DEFAULT_PORT = 12345;
    public static final String DEFAULT_HOST = "localhost";
    public static final int MAX_CLIENTS = 100;
}