# Datenschutzerklärung VPlanPlus und vpp.ID Stand 21.02.2024
Die App teilt sich in die Dienste VPlanPlus und vpp.ID auf.

## Generelles
Für jede Netzwerkanfrage wird die eigene IP-Adresse übermittelt. Dies ist technisch bedingt. Jede Anfrage wird auch mit einem User-Agent ausgestattet. Dieser ist in jedem Falle "VPlanPlus".

### Löschen der Daten
Das Löschen von Daten bzw. Nutzerkonten ist nicht vorgesehen. Falls dennoch eine Löschung durchgeführt werden soll, kann eine E-Mail an julvin.babies@gmail.com geschrieben werden. Mögliche Gründe für das Löschen des Kontos können sein:
- Ich besuche die aktuelle Schule nicht mehr
- Ich habe versehentlich mehrere Konten erstellt

## VPlanPlus
Die App VPlanPlus und ihr Funktionsumfang lädt alle Daten direkt von stundenplan24.de (nachfolgend als Indiware ([Datenschutzerklärung](https://indiware.de/index.php?page=datenschutz)) bezeichnet). Dabei werden die Zugangsdaten für die Schule (Schulnummer, Benutzername/Passwort), welche für eine ganze Schule gelten übertragen.

App-Nachrichten über Updates und andere Informationen werden nicht personenbezogen von den VPlanPlus Servern bezogen. Lediglich die Schulnummer wird zur Filterung zwischen den Nachrichten übermittelt, ebenso wie die App-Version.

Für die Echtzeitkommunikation von beispielsweise Raumbuchungen und Hausaufgaben wird Google Firebase Messaging verwendet. Dabei werden nur Informationen über die Art der Neuigkeit, nicht aber die eigentlichen Daten übermittelt. Dafür wird ein Token erzeugt, welches zu einem Gerät zugeordnet wird. Mit diesem Token wird eine Schulklasse und ggf. eine vpp.ID assoziiert, um die Nachrichten noch genauer einzuschränken.

## vpp.ID
Die vpp.ID ist ein Konto, welches von Nutzern freiwillig angelegt werden kann. Die Authentifizierung findet hierbei über [beste.schule](https://beste.schule)/[Datenschutzerklärung](https://beste.schule/privacy) (nachfolgend als Schulverwalter bezeichnet) statt. Hierbei werden der Vor- und Nachname, die E-Mail-Adresse sowie die Schule und Klasse des Nutzers an die Server von vpp.ID übergeben. Dies ist zur Verifizierung notwendig, um Spam und Irreführung vorzubeugen. Der Vor- und Nachname kann von jedem VPlanPlus Nutzer eingesehen werden.
