========================================================================
GUIDA SEMPLICE ALL'INSTALLAZIONE E ALL'AVVIO DEL PROGRAMMA
========================================================================

Questa guida vi aiuterà a scaricare e avviare il programma sul vostro
computer in pochi semplici passi, anche se non siete esperti di informatica.

------------------------------------------------------------------------
1. REQUISITI FONDAMENTALI (COSA SERVE SUL COMPUTER)
------------------------------------------------------------------------
Prima di iniziare, sul vostro computer deve essere installato Java:

* Java (JDK): Versione 24 o superiore.
* Come verificare se lo avete già:
  1. Aprite il Terminale (su Mac o Linux) o il Prompt dei comandi (su Windows).
  2. Digitate il seguente comando e premete Invio:
     java -version
  3. Se compare un testo che indica una versione uguale o superiore a 24, potete procedere.
     Se compare un errore, dovete scaricare e installare gratuitamente Java da internet (cercando "Download Java JDK 24").

------------------------------------------------------------------------
2. COME SCARICARE IL PROGRAMMA DA GITHUB
------------------------------------------------------------------------
1. Visitate la pagina internet di GitHub dove si trova il progetto -> https://github.com/LuraghiMatteo/Cinemax.
2. Cercate in alto a destra un pulsante verde con la scritta "Code" e cliccatelo.
3. Nel menu che si apre, cliccate sulla voce "Download ZIP".
4. Una volta completato il download, estraete il contenuto del file .zip in una cartella a vostra scelta sul computer.

------------------------------------------------------------------------
3. STRUTTURA DELLE CARTELLE DEL PROGETTO
------------------------------------------------------------------------
Entrando nella cartella estratta troverete questa struttura:

📁 Nome_Progetto/
├── 📄 autori.txt       # Contiene i nomi dei membri del team
├── 📁 doc/              # Contiene i manuali d'uso (in formato PDF) e la javadoc
├── 📁 data/             # Contiene i file di dati utilizzati dal programma
├── 📁 bin/              # Contiene il file pronto da eseguire (il file .jar)
├── 📁 src/              # Contiene il codice sorgente (per programmatori)
├── 📁 lib/              # Contiene librerie esterne necessarie
└── 📄 README.txt        # Questa guida

------------------------------------------------------------------------
4. COME AVVIARE IL PROGRAMMA (IL FILE JAR GIÀ PRONTO)
------------------------------------------------------------------------
Non c'è bisogno di compilare il codice o fare procedure difficili. Nella cartella "bin" abbiamo già inserito il programma pronto per essere avviato.

Seguite questi passaggi per avviarlo:
1. Aprite il Terminale (Mac/Linux) o il Prompt dei comandi (Windows).
2. Spostatevi all'interno della cartella principale del progetto usando il comando "cd" seguito dal percorso della cartella.
3. Eseguite il programma digitando questo comando e premendo Invio:

   java -jar bin/nome_del_file.jar

*(Nota per l'utente: sostituite "nome_del_file.jar" con il nome reale del file che trovate dentro la cartella bin)*.

------------------------------------------------------------------------
5. SE QUALCOSA NON FUNZIONA (RISOLUZIONE DEI PROBLEMI)
------------------------------------------------------------------------
* Errore: "Comando non riconosciuto" o simile.
  -> Significa che Java non è installato sul computer oppure che il sistema non riesce a trovarlo. Assicuratevi di aver installato Java correttamente.

* Errore: "UnsupportedClassVersionError" (Versione non supportata).
  -> Significa che il vostro Java è troppo vecchio. Aggiornate Java sul vostro computer scaricando la versione più recente (JDK 24 o superiore).
========================================================================