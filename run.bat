@echo off
REM Compilation
javac -cp ".;libs/json-simple-1.1.1.jar" src/src/fr/aryvoxx/projava/model/*.java src/src/fr/aryvoxx/projava/view/*.java src/src/fr/aryvoxx/projava/*.java

REM Exécution
java -cp ".;libs/json-simple-1.1.1.jar;src" fr.aryvoxx.projava.Main

pause