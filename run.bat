@echo off
setlocal EnableExtensions EnableDelayedExpansion
chcp 65001 > nul
title Chat Client/Serveur - Projet Java Socket
color 0A

:menu
cls
echo ========================================
echo    CHAT CLIENT / SERVEUR - JAVA SOCKET
echo ========================================
echo.
echo 1. Compiler tous les fichiers
echo 2. Lancer le serveur
echo 3. Lancer le client (Interface Graphique)
echo 4. Lancer le client (Console)
echo 5. Lancer serveur + 2 clients (Test complet)
echo 6. Quitter
echo.
set choix=
set /p choix=Votre choix [1-6] : 

if "%choix%"=="1" goto compile
if "%choix%"=="2" goto server
if "%choix%"=="3" goto client_gui
if "%choix%"=="4" goto client_console
if "%choix%"=="5" goto test_all
if "%choix%"=="6" goto end

echo.
echo ❌ Choix invalide !
pause
goto menu

:compile
cls
echo 🔧 Compilation en cours...
if not exist bin mkdir bin
javac -d bin -cp . src/common/*.java src/server/*.java src/client/*.java MainLauncher.java
if errorlevel 1 (
    echo ❌ Erreur de compilation !
    pause
) else (
    echo ✅ Compilation réussie !
    pause
)
goto menu

:server
cls
echo 🚀 Lancement du serveur...
java -cp bin MainLauncher 1
pause
goto menu

:client_gui
cls
echo 🖥️ Lancement du client graphique...
java -cp bin MainLauncher 3
pause
goto menu

:client_console
cls
echo 💬 Lancement du client console...
java -cp bin MainLauncher 4
pause
goto menu

:test_all
cls
echo 🧪 Test complet...
start cmd /k "title Serveur & java -cp bin MainLauncher 1"
timeout /t 3 > nul
start cmd /k "title Client 1 & java -cp bin MainLauncher 3"
start cmd /k "title Client 2 & java -cp bin MainLauncher 4"
pause
goto menu

:end
echo.
echo 👋 Au revoir !
pause
exit
