# Datenschutzerklärung VPlanPlus und vpp.ID Stand 28.02.2024
> [!NOTE]
> Die App teilt sich in die Dienste VPlanPlus und vpp.ID auf.


## Generelles
VPlanPlus umfasst lediglich die App. vpp.ID ist ein optinales Angebot, welches in die App integriert ist, jedoch bis zur Aktivierung durch den Nutzer untätig bleibt.

### Abkürzungen
- Datenschutzgrundverordnung: DSGVO
- stundenplan24.de: Indiware
- beste.schule: Schulverwalter
- stundenplan24.de und beste.schule: externe Anbieter
- VPlanPlus und vpp.ID: Services
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

Das Token wird dabei lokal verwendet, um Noten aus beste.schule abzufragen. Die Server von vpp.ID haben zu keiner Zeit Kenntnis von den Noten.

## Anmeldung bei vpp.ID
Die Registrierung erfolgt via SSO bei beste.schule. Anschließend setzt der Benutzer ein Passwort, mit welchem er sich künftig anmeldet.

Ist die Schule nicht bei vpp.ID registriert, so werden alle Nutzerdaten sowie das Token direkt gelöscht.

### Rücksetzung/Änderung des Passwortes
> [!CAUTION]
> Dies ist aktuell nicht möglich, wir arbeiten jedoch bereits an dieser Funktion

