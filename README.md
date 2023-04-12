# Rat um Rat

### By Emanuele, Rahel, Rui und Jonah.

## Über das Spiel

Rat um Rad ist ein digitales Brettspiel für 3-5 Spieler. Die Spieler bauen Strecken auf einem Streckennetz der Stadt Basel. 
Ziel ist es möglichst lange Strecken zu bilden und als erster alle Spielsteine verbaut zu haben.

## Entwicklung

Das Spiel befindet sich aktuell in Entwicklung. Der aktuelle Entwicklungsstand und relevante Dokumente können
auf [Google Drive](https://drive.google.com/drive/folders/1Voso3Dcn3yKRfsRwU3yPKWJBVLTWuam_?usp=sharing). mitverfolgt
werden. Zudem wird zu jedem Meilenstein ein Ordner mit dem aktuellen Stand der relevanten Dokumente in diesem Repository
hinterlegt.

## Programm starten

### main()

Sowohl Client als auch Server können über das Terminal gestartet werden. Der Server muss laufen, damit der Client
gestartet werden kann.

### JAR & Javadoc

Um ein JAR-File und die Javacod zu erstellen, öffne das Repository im Terminal und gebe
`gradlew build-cs108` ein. Um die Jar-File auszuführen, gehe in den [build/libs](./build/libs) Order und führe die
JAR-File mit z.B. `java -jar rat-um-rad-0.0.1-ALPHA.jar server 9090` aus. Um die Javadoc anzuschauen, gehe in
den [build/docs/javadoc](.build/docs/javadoc) und öffne dort die [index.html](.build/docs/javadoc/index.html) Datei mit
einem Browser.

### Server Input Parameter

| Befehl          | Beschreibung                                                                                       |
|-----------------|----------------------------------------------------------------------------------------------------|
| server \<port\> | startet den Server und erstellt das Socket, welches auf dem gewählten Port auf Verbindungen wartet |
| server          | zu Testzwecken, startet den Server mit Default-Parameter Port: 8090                                |

### Client Input Parameter

| Befehl                 | Beschreibung                                                                                                                 |
|------------------------|------------------------------------------------------------------------------------------------------------------------------|
| client \<ip\> \<port\> | startet den Client und stellt eine socket-Verbindung zur gewählten IP und Port her |
| client                 | zu Testzwecken, startet den Client mit Default-Parametern Host: localhost und Port: 8090                                     |
