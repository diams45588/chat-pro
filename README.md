# CHAT PRO — Application de Messagerie Client/Serveur

## Auteur

Diaminatou Sanogo  
Projet académique — Programmation Réseau (Java Sockets TCP)

---

## Description

CHAT PRO est une application de messagerie temps réel développée en Java utilisant les sockets TCP.

Elle permet à plusieurs clients de communiquer via un serveur central avec :

- des messages publics
- des messages privés
- une discussion avec le serveur (administrateur)
- une gestion multi-clients simultanés

---

## Fonctionnalités

- Chat public avec diffusion à tous les clients connectés
- Messages privés entre utilisateurs
- Conversation privée avec le serveur
- Interface graphique réalisée avec Swing
- Gestion multi-threads
- Liste dynamique des utilisateurs connectés
- Fenêtres dédiées pour les conversations privées

---

## Architecture

            ┌─────────────┐
            │   SERVEUR   │
            │  Port 12345 │
            └──────┬──────┘
                   │
   ┌───────────────┼───────────────┐
   ▼               ▼               ▼

┌─────────┐ ┌─────────┐ ┌─────────┐
│ CLIENT 1│ │ CLIENT 2│ │ CLIENT 3│
│ Alice │ │ Bob │ │ Charlie │
└─────────┘ └─────────┘ └─────────┘


---

## Installation

### Prérequis

- Java JDK 17 ou supérieur
- Git 

### Cloner le projet

```bash
- git clone https://github.com/diams45588/chat-pro.git
- cd chat-pro
- Compiler le projet
- mkdir -p out
- javac -encoding UTF-8 -d out src/common/*.java src/server/*.java src/client/*.java
- Lancer l’application
-Démarrer le serveur
- java -cp out src.server.ServerGUI
Démarrer un client
- java -cp out src.client.ClientGUI
## Guide d’utilisation

### Côté serveur

1. Lancer **ServerGUI**
2. Cliquer sur **DÉMARRER**
3. Visualiser les connexions dans la liste

Pour envoyer un message :

- Public : choisir **PUBLIC**
- Privé : sélectionner un client

Double-cliquer sur un client pour ouvrir un chat privé.

---

### Côté client

1. Lancer **ClientGUI**
2. Entrer un pseudo
3. Cliquer sur **Connexion**

Fonctionnement :

- Message public : écrire dans le champ principal
- Message privé : double-cliquer sur un utilisateur
- Chat avec le serveur : double-cliquer sur le nom serveur

---

## Protocole de communication

### Types de messages

| Type | Description |
|------|------------|
| MSG_LOGIN | Connexion |
| MSG_LOGOUT | Déconnexion |
| MSG_PUBLIC | Message public |
| MSG_PRIVATE | Message privé |
| MSG_SERVER | Message pour serveur |
| MSG_SERVER_PRIVATE | Réponse serveur |
| MSG_LIST_USERS | Liste utilisateurs |

---

## Structure d’un message

```java
class Message implements Serializable {
    String type;
    String sender;
    String recipient;
    String content;
    long timestamp;
}