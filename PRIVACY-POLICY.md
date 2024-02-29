# Datenschutzerklärung VPlanPlus und vpp.ID Stand 29.02.2024
> [!NOTE]
> Die App teilt sich in die Dienste VPlanPlus und vpp.ID auf.


## Generelles
VPlanPlus umfasst lediglich die App. vpp.ID ist ein optinales Angebot, welches in die App integriert ist, jedoch bis zur Aktivierung durch den Nutzer keine personenbezogenen Daten erzeugt.

### Abkürzungen
- Datenschutzgrundverordnung: DSGVO
- stundenplan24.de: Indiware
- beste.schule: Schulverwalter
- stundenplan24.de und beste.schule: externe Anbieter
- VPlanPlus und vpp.ID: Services
- App: VPlanPlus

### Definition personenbezogenes Datum
Jegliche Daten, welche auf eine reale Person zugeordnet sind oder mittels weiterer Informationen Rückschlüsse auf eine reale Person ermöglichen, werden nach DSGVO als personenbezogen bezeichnet.

### Verantwortlicher
Julius Vincent Babies<br />
E-Mail: julvin.babies@gmail.com

### Hintergrund der Verarbeitung
Alle Daten werden nur für die Funktionen der Services verarbeitet. Eine Weitergabe oder anderweitige Verwendung findet nicht statt.

## Erhebung von Daten
### Generell
Jede Netzwerkanfrage an die Services, Indiware oder Schulverwalter umfasst Metadaten. Diese beinhalten unteranderem eine IP-Adresse des Gerätes, beziehungsweise seines Zugangspunktes zum Internet. Mit dieser IP-Adresse sind theoretisch extrem ungenau auf einen Ort zurückführbar. Auch der Internetanbieter kann aus ihnen erkannt werden. Auch werden Datum und Uhrzeit bei einer Anfrage erfasst. Desweiteren wird der sogenannte User-Agent mitgesendet, dieser beinhaltet immer den String "VPlanPlus".

### Services
Alle der im vorherigen Abschnitt genannten Daten werden für maximal 7 Tage in den sogenannten Log-Dateien der Server gespeichert und anschließend automatisisert gelöscht.

#### Verwendung ohne vpp.ID
Requests an die Server von VPlanPlus und vpp.ID, die ohne Nutzerangabe erfolgen, werden nicht zur Profilerstellung und automatisierten Auswertung verwendet.

VPlanPlus kann im Hintergrund den Benutzernamen einer vpp.ID erfragen, um beispielsweise den Ersteller eines Datensatzes in der App namentlich zu nennen. Dies ist beispielsweise bei Raumbuchungen der Fall.

#### Verwendung mit vpp.ID
Für die Verwendung der Dienste von vpp.ID wird ein Token benötigt. Dieses Token ist zusammen mit dem Nutzer sowohl auf dem Endgerät als auch auf den Servern von vpp.ID gespeichert.

### Externe Anbieter
Hier finden Sie die Datenschutzerklärung für
- [Indiware](https://indiware.de/index.php?page=datenschutz)
  - [Stundenplan24.de](https://stundenplan24.de/datenschutzapp.html#datenschutzsp24)
- [Schulverwalter](https://schulverwalter.de/privacy/)
  - [beste.schule](https://beste.schule/privacy)

### Schulverwalter
Die Registrierung erfolgt mittels eines Single-Sign-On [Bundesamt für Sicherheit in der Informationstechnik]([SSO](https://www.bsi.bund.de/DE/Themen/Verbraucherinnen-und-Verbraucher/Informationen-und-Empfehlungen/Cyber-Sicherheitsempfehlungen/Accountschutz/Single-Sign-On/single-sign-on_node.html)https://www.bsi.bund.de/DE/Themen/Verbraucherinnen-und-Verbraucher/Informationen-und-Empfehlungen/Cyber-Sicherheitsempfehlungen/Accountschutz/Single-Sign-On/single-sign-on_node.html) von beste.schule. Dabei wird ein Token an die Server von vpp.ID übermittelt, womit die Bestätigung der Angehörigkeit einer Klasse und einer Schule durchgeführt wird. Auch die E-Mail-Adresse des Nutzers wird zwecks Anmeldekennung und Kontaktmöglichkeit durch die Administration von vpp.ID erfasst. Das Token und die E-Mail-Adresse wird auf den Servern von beste.schule und vpp.ID gespeichert, und bei einer Anmeldung innerhalb von VPlanPlus an das Endgerät mitgesendet.

Das Token wird dabei lokal verwendet, um Noten aus beste.schule abzufragen. Die Server von vpp.ID haben zu keiner Zeit Kenntnis von den Noten, auch wenn dies technisch möglich ist.

### Google Firebase
Zur Echtzeitkommunikation über Raumbuchungen und Hausaufgabenanpassungen wird Google Firebase Cloud Messaging [Datenschutzerklärung](https://firebase.google.com/support/privacy?hl=de) verwendet. Dieses System ist die einzige zuverlässige Möglichkeit, nahezu Echtzeitkommunikation auf Android-Geräten durchführen zu hönnen. Hierfür wird von dem Gerät in regelmäßigen Abständen von mehreren Stunden ein Token generiert, welches in Kombination mit den Klassen und vpp.IDs auf einem Endgerät an die vpp.ID Server gesendet und gespeichert, selbst, wenn der Nutzer keine vpp.ID hat. Dies ist notwendig, um möglichst sparsam Klassenmitglieder oder alle Schulmitglieder über sie betreffende Änderungen zu Informieren. Diese Tokens verfallen automatisch nachdem ein neues Token erstellt wurde.

## Anmeldung bei vpp.ID
Die Registrierung erfolgt via SSO bei beste.schule. Anschließend setzt der Benutzer ein Passwort, mit welchem er sich künftig anmeldet.

Ist die Schule nicht bei vpp.ID registriert, so werden alle Nutzerdaten sowie das Token direkt gelöscht.

### Rücksetzung/Änderung des Passwortes
> [!CAUTION]
> Dies ist aktuell nicht möglich, wir arbeiten jedoch bereits an dieser Funktion.
> Falls du dich nicht mehr anmelden kannst, schreibe eine E-Mail an julvin.babies@gmail.com. Wichtig ist, dass du dieselbe E-Mail wie für dein beste.schule Account verwendest.

## Sichtbarkeit von direkt Benutzerbetzogenen Daten
### Noten
Die Noten eines vpp.ID Benutzers werden stehts über das eigene Endgerät direkt von beste.schule abgefragt und auch nur dort gespeichert. Keine andere Instanz kann diese Noten sehen.

### Raumbuchungen
Bei einer Raumbuchung wird der Klarname sowie die Klasse der buchenden Person an alle Schulmitglieder übermittelt. Diese Abfrage ist nur in Kombination mit den offiziellen Zugangsdaten für die Stundenplan24.de Instanz der Schule möglich. Administrationen können jederzeit diese Daten einsehen. Die Raumbuchungen werden zum aktuellen Zeitpunkt nicht automatisiert gelöscht, können jedoch nur für den aktuellen Tag von Endgeräten abgerufen werden.

### Hausaufgaben
Bei der Erstellung von freigegebenen Hausaufgaben wird der Klarname sowie die Klasse an alle Klassenmitglieder übermittelt. Die Zugangsdaten sind auf Schulebene definiert und somit für alle Klassen gleich. Administrationen können jederzeit diese Daten einsehen. Die Hausaufgaben werden zum aktuellen Zeitpunkt nicht automatisiert gelöscht.

## Weitergabe der Daten an Dritte
Eine Weitergabe an Dritte findet generell nicht statt.

## Datensicherheit
### Netzwerkanfragen
Alle Netzwerkanfragen im Produktivsystem sind mit HTTPS/TLS verschlüsselt.

### Authentifizierung
#### Web
Das Passwort für eine vpp.ID hat Kriterien. Über diese wird der Nutzer bei der Erstellung eines solchen Accounts aufgeklärt.

#### App
Für die App VPlanPlus wird bei dem Verbinden einer vpp.ID ein Token erzeugt, welches auf dem Gerät und dem Server gespeichert wird. Darüber werden alle personenbezogenen Anfragen an vpp.ID Dienste authentifiziert. Für Nutzer, die keine vpp.ID verwenden, werden die Stundenplan24.de Zugangsdaten der Schule verwendet.

## Support
Bei Fragen oder Anmerkungen können Sie sich gern per Mail an julvin.babies@gmail.com oder per GitHub-Issue an mich wenden.

## Ihre Rechte
Sie haben das Recht via Support
- Einen Auszug ihrer personenbezogenen Daten zu erhalten
- Die Löschung ihrer personenbezogenen Daten zu beantragen, wenn
  - Sie die Schule verlassen
