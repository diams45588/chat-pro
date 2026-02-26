@echo off
title Chat Client/Serveur - Projet Java Socket
color 0A

echo ========================================
echo    CHAT CLIENT/SERVEUR - PROJET JAVA
echo ========================================

:menu
echo.
echo CHOIX D'EXECUTION:
echo 1. Compiler tous les fichiers
echo 2. Lancer le serveur
echo 3. Lancer le client (Interface Graphique)
echo 4. Lancer le client (Console)
echo 5. Lancer serveur + 2 clients (Test complet)
echo 6. Quitter
echo.
set /p choix="Votre choix [1-6]: "

if "%choix%"=="1" goto compile
if "%choix%"=="2" goto server
if "%choix%"=="3" goto client_gui
if "%choix%"=="4" goto client_console
if "%choix%"=="5" goto test_all
if "%choix%"=="6" goto end

echo Choix invalide!
goto menu

:compile
echo.
echo Compilation en cours...
javac -d bin -cp . src/common/*.java src/server/*.java src/client/*.java MainLauncher.java
if errorlevel 1 (
    echo Erreur de compilation!
    pause
) else (
    echo ✅ Compilation reussie!
)
goto menu

:server
echo.
echo Lancement du serveur...
java -cp bin MainLauncher 1
goto menu

:client_gui
echo.
echo Lancement du client graphique...
java -cp bin MainLauncher 3
goto menu

:client_console
echo.
echo Lancement du client console...
java -cp bin MainLauncher 4
goto menu

:test_all
echo.
echo Test complet...
start cmd /k "title Serveur & java -cp bin MainLauncher 1"
timeout /t 3
start cmd /k "title Client 1 & java -cp bin MainLauncher 3"
start cmd /k "title Client 2 & java -cp bin MainLauncher 4"
goto menu

:end
echo.
echo Au revoir!
pause