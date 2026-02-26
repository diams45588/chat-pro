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

```text
                ┌─────────────┐
                │   SERVEUR   │
                │  Port 12345 │
                └──────┬──────┘
                       │
       ┌───────────────┼───────────────┐
       ▼               ▼               ▼
 ┌─────────┐     ┌─────────┐     ┌─────────┐
 │ CLIENT 1│     │ CLIENT 2│     │ CLIENT 3│
 │  Alice  │     │   Bob   │     │ Charlie │
 └─────────┘     └─────────┘     └─────────┘
---
## Installation

### Prérequis

Avant de lancer le projet, assurez-vous d’avoir installé :

- **Java JDK 17 ou supérieur**  
- **Git** (optionnel)

---

### Cloner le projet

Pour récupérer le projet depuis GitHub :

1. Ouvrir un terminal  
2. Exécuter les commandes suivantes :

```bash
git clone https://github.com/diams45588/chat-pro.git
cd chat-pro
Compiler le projet

Dans le dossier du projet, créer le dossier de sortie et compiler les fichiers Java :

mkdir -p out
javac -encoding UTF-8 -d out src/common/*.java src/server/*.java src/client/*.java
Lancer l’application
Démarrer le serveur
java -cp out src.server.ServerGUI
Démarrer un client
java -cp out src.client.ClientGUI
Guide d’utilisation
Côté serveur

Lancer ServerGUI

Cliquer sur DÉMARRER

Visualiser les connexions dans la liste

Pour envoyer un message :

Public : choisir PUBLIC

Privé : sélectionner un client

Double-cliquer sur un client pour ouvrir une conversation privée.

Côté client

Lancer ClientGUI

Entrer un pseudo

Cliquer sur Connexion

Fonctionnement :

Message public : écrire dans le champ principal

Message privé : double-cliquer sur un utilisateur

Chat avec le serveur : bouton dédié

Protocole de communication
Types de messages
Type	Description
MSG_LOGIN	Connexion
MSG_LOGOUT	Déconnexion
MSG_PUBLIC	Message public
MSG_PRIVATE	Message privé
MSG_SERVER	Message pour serveur
MSG_SERVER_PRIVATE	Réponse serveur
MSG_LIST_USERS	Liste utilisateurs
Structure d’un message
class Message implements Serializable {
    String type;
    String sender;
    String recipient;
    String content;
    long timestamp;
}
Technologies utilisées

Java

Sockets TCP

Swing

Multi-threading

Sérialisation d’objets

Structure du projet
src/
├── common/
│   ├── Constants.java
│   └── Message.java
├── server/
│   ├── ChatServer.java
│   ├── ClientHandler.java
│   ├── ServerGUI.java
│   └── ServerPrivateChatWindow.java
└── client/
    ├── ChatClient.java
    ├── ClientGUI.java
    └── PrivateChatHandler.java
Améliorations possibles

Authentification des utilisateurs

Historique des messages

Version web

Partage de fichiers