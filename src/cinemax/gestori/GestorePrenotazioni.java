package cinemax.gestori;

import cinemax.eccezioni.NumeroCampiErratoException;
import cinemax.eccezioni.PostiEsauritiException;
import cinemax.eccezioni.PrenotazioneException;
import cinemax.modelli.Prenotazione;
import cinemax.modelli.Proiezione;
import cinemax.modelli.Cliente;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Questa classe rappresenta il gestore di tutte le prenotazioni fatte dagli utenti.
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class GestorePrenotazioni {

    // Attributi
    /**
     * Struttura dati per l'indicizzazione e l'archiviazione in RAM delle prenotazioni attive.
     * Mappa ogni codice univoco alfanumerico (Key in formato String) al rispettivo oggetto modello (Value di tipo Prenotazione).
     */
    private Map<String, Prenotazione> prenotazioni;
    private int contatoreCodici = 1; // Contatore necessario per generare il codice univoco
    private static final int CAPIENZA_SALA = 200;
    private final String FILE_PATH = "data" + File.separator + "prenotazioni.csv";

    // Costruttore
    /**
     * Costruttore della classe: Inizializza una HashMap di Prenotazioni vuota
     */
    public GestorePrenotazioni() {
        this.prenotazioni = new HashMap<>();
    }

    // METODI DI SUPPORTO
    /**
     * Calcola il numero di posti ancora disponibili per una specifica proiezione.
     * Il calcolo viene effettuato sottraendo il numero totale di posti già prenotati
     * dalla capacità massima della sala (200 posti).
     *
     * @param proiezione La proiezione di cui si vuole conoscere la disponibilità.
     * @return Il numero di posti liberi rimasti.
     */
    public int calcolaPostiLiberi(Proiezione proiezione) {
        int numPostiOccupati = 0, numPostiLiberi = 0;

        // Scorro tutte le prenotazioni
        for(Prenotazione p : prenotazioni.values()){
            if(p.getProiezione().equals(proiezione)){ // Verifico che la proiezione della prenotazione sia uguale a quella passata
                numPostiOccupati += p.getNumeroPosti(); // Calcolo posti occupati
            }
        }

        numPostiLiberi = CAPIENZA_SALA - numPostiOccupati; // Calcolo posti liberi

        return numPostiLiberi;
    }

    /**
     * Genera un codice univoco sequenziale alfanumerico con prefisso (es. PR000001, PR000002...).
     * Il metodo garantisce l'univocità in tempo costante O(1) senza la necessità di cicli di verifica
     * Supporta un limite massimo di 999.999 prenotazioni uniche mantenendo una lunghezza fissa di 8 caratteri totali.
     *
     * @return Una stringa di 8 caratteri (2 alfabetici di prefisso e 6 numerici) che rappresenta
     * il codice univoco della prenotazione.
     */
    private String generaCodiceUnivoco() {
        // Il pattern "PR%06d" concatena il prefisso statico aziendale "PR" alla formattazione del contatore numerico su 6 cifre totali.
        String codice = String.format("PR%06d", this.contatoreCodici);

        // Incremento il contatore di stato interno per preparare la sequenza alla successiva richiesta di prenotazione
        this.contatoreCodici++;

        return codice;
    }

    /**
     * Gestisce la visualizzazione formattata ed impaginata a blocchi delle prenotazioni sulla console.
     * Mostra un massimo di 25 elementi a partire dall'indice specificato, stampando l'indice reale di lista
     * affiancato alla vista testuale della prenotazione, evitando il sovraccarico visivo del terminale.
     *
     * @param prenotazioni La lista completa di oggetti {@link Prenotazione} da scorrere e mostrare a video.
     * @param index        L'indice numerico di partenza (offset) da cui avviare il rendering del blocco corrente.
     */
    public static void visualizzaPrenotazioni(List<Prenotazione> prenotazioni, int index) {
        for (int i = index; i < prenotazioni.size() && i < index + 25; i++) {
            System.out.println("[" + i  + "]" + prenotazioni.get(i));
        }
    }

    // METODI PER IL CLIENTE
    /**
     * Crea una nuova prenotazione se i posti sono disponibili.
     * @param cliente Il cliente che prenota
     * @param proiezione La proiezione scelta
     * @param numeroPosti Quanti posti vuole prenotare il cliente
     * @return La prenotazione creata oppure null se fallisce
     */
    public Prenotazione creaPrenotazione(Cliente cliente, Proiezione proiezione, int numeroPosti) {
        // Controllo disponibilità posti
        int postiDisponibili = calcolaPostiLiberi(proiezione);

        // Se non ci sono posti lancio eccezione
        if (numeroPosti > postiDisponibili) {
            throw new PostiEsauritiException("Errore: posti non sufficienti per la proiezione. Disponibili: " + postiDisponibili);
        }

        // Generazione codice univoco e creazione oggetto
        String codice = generaCodiceUnivoco();
        Prenotazione nuova = new Prenotazione(codice, cliente, proiezione, numeroPosti);

        // Aggiornamento mappa
        prenotazioni.put(codice, nuova);

        return nuova;
    }

    /**
     * Recupera l'elenco di tutte le prenotazioni effettuate da un cliente specifico se esistono.
     *
     * @param cliente L'oggetto Cliente di cui si vogliono recuperare le prenotazioni.
     * @return Una lista di oggetti Prenotazione associati al cliente; la lista sarà vuota se non ci sono match.
     */
    public List<Prenotazione> getPrenotazioniPerCliente(Cliente cliente) {
        List<Prenotazione> filtrate = new ArrayList<>();

        for (Prenotazione p : prenotazioni.values()) {
            // Verifico se la prenotazione appartiene al cliente passato sfruttando il metodo equals della classe Utente
            if (p.getCliente().equals(cliente)) {
                filtrate.add(p);
            }
        }

        return filtrate;
    }

    /**
     * Modifica la data di una prenotazione esistente sostituendo la proiezione associata.
     * L'operazione è consentita solo se la proiezione originale e quella nuova
     * sono entrambe successive alla data/ora odierna e se ci sono posti disponibili.
     *
     * @param codiceUnivoco Il codice della prenotazione da modificare.
     * @param nuovaProiezione Il nuovo spettacolo a cui il cliente vuole partecipare.
     * @throws PrenotazioneException Se uno dei vincoli aziendali o temporali viene violato.
     */
    public void modificaPrenotazione(String codiceUnivoco, Proiezione nuovaProiezione) throws PrenotazioneException {
        // Cerco la prenotazione
        Prenotazione daModificare = prenotazioni.get(codiceUnivoco.toUpperCase());

        // Se non la trovo, lancio l'eccezione
        if (daModificare == null) {
            throw new PrenotazioneException("La prenotazione con codice " + codiceUnivoco + " non esiste nel sistema.");
        }

        LocalDateTime oraAttuale = LocalDateTime.now();

        // Controllo regole temporali descritte nelle specifiche
        if (daModificare.getProiezione().getDataOra().isBefore(oraAttuale)) {
            throw new PrenotazioneException("Impossibile modificare: lo spettacolo originale è già passato.");
        }

        if (nuovaProiezione.getDataOra().isBefore(oraAttuale)) {
            throw new PrenotazioneException("Impossibile modificare: la nuova data scelta è nel passato.");
        }

        // Controllo che il cliente non cambi film
        if (!daModificare.getProiezione().getFilm().getTitolo().equals(nuovaProiezione.getFilm().getTitolo())) {
            throw new PrenotazioneException("Non puoi cambiare film durante la modifica. Cancella la prenotazione e creane una nuova.");
        }

        // Controllo disponibilità posti per la nuova proiezione
        int postiLiberiNuova = calcolaPostiLiberi(nuovaProiezione);
        if (daModificare.getNumeroPosti() > postiLiberiNuova) {
            throw new PrenotazioneException("Posti insufficienti nella nuova proiezione selezionata. Disponibili: " + postiLiberiNuova);
        }

        // Se supera tutti i controlli, applichiamo la modifica
        daModificare.setProiezione(nuovaProiezione);
    }

    /**
     * Elimina una prenotazione dal sistema ricercandola tramite il suo codice univoco.
     * La cancellazione va a buon fine solo se la prenotazione esiste e se la data
     * della proiezione non è ancora passata (vincolo temporale).
     *
     * @param codiceUnivoco La stringa di 6 caratteri che identifica la prenotazione.
     * @throws PrenotazioneException Se la prenotazione non esiste o se lo spettacolo è già passato.
     */
    public void eliminaPrenotazione(String codiceUnivoco) throws PrenotazioneException {
        // Cerco la prenotazione
        Prenotazione daRimuovere = prenotazioni.get(codiceUnivoco.toUpperCase());

        // Se non trovo la prenotazione, lancio l'eccezione
        if (daRimuovere == null) {
            throw new PrenotazioneException("Nessuna prenotazione trovata con il codice " + codiceUnivoco);
        }

        // Impediamo di cancellare prenotazioni di spettacoli già iniziati o passati
        if (daRimuovere.getProiezione().getDataOra().isBefore(LocalDateTime.now())) {
            throw new PrenotazioneException("Impossibile cancellare la prenotazione: lo spettacolo è già passato.");
        }

        // Se passa i controlli, elimino la prenotazione dalla mappa
        prenotazioni.remove(codiceUnivoco.toUpperCase());
    }

    // METODI PER IL BIGLIETTAIO
    /**
     * Recupera l'elenco di tutte le prenotazioni relative agli spettacoli previsti per la giornata odierna.
     * Ignora l'orario specifico e confronta solo l'anno, il mese e il giorno.
     *
     * @return Una lista (eventualmente vuota) contenente le prenotazioni valide per oggi.
     */
    public List<Prenotazione> visualizzaPrenotazioniOdierne() {
        List<Prenotazione> prenotazioniOdierne = new ArrayList<>();
        LocalDate oggi = LocalDate.now();

        for (Prenotazione p : prenotazioni.values()) {
            // Controllo che la data corrisponda con oggi
            if (p.getProiezione().getDataOra().toLocalDate().equals(oggi)) {
                prenotazioniOdierne.add(p);
            }
        }

        return prenotazioniOdierne; // Restituisce la lista, la gestione del null va nel Main
    }

    /**
     * Ricerca una specifica prenotazione tramite il suo codice univoco.
     * Poiché i codici sono univoci, il metodo restituisce un singolo oggetto o null.
     *
     * @param codice Il codice alfanumerico di 6 caratteri da cercare.
     * @return La prenotazione trovata, oppure null se inesistente.
     */
    public Prenotazione cercaPrenotazionePerCodice(String codice) {
        return prenotazioni.get(codice.toUpperCase());
    }

    /**
     * Ricerca tutte le prenotazioni effettuate da un cliente specifico tramite nome e cognome.
     *
     * @param nome Il nome del cliente.
     * @param cognome Il cognome del cliente.
     * @return Una lista di prenotazioni associate a quel cliente (vuota se nessun match).
     */
    public List<Prenotazione> cercaPrenotazionePerCliente(String nome, String cognome) {
        List<Prenotazione> trovate = new ArrayList<>();

        for (Prenotazione p : prenotazioni.values()) {
            // Controllo che il cliente corrisponda
            if (p.getCliente().getNome().equalsIgnoreCase(nome) && p.getCliente().getCognome().equalsIgnoreCase(cognome)) {
                trovate.add(p);
            }
        }

        return trovate;
    }

    /**
     * Ricerca tutte le prenotazioni per proiezioni il cui titolo contiene la stringa cercata.
     * Realizza una ricerca parziale e non accetta distinzioni tra maiuscole e minuscole.
     *
     * @param titoloParziale La parola o frase da cercare nel titolo del film.
     * @return Una lista di prenotazioni pertinenti.
     */
    public List<Prenotazione> cercaPrenotazionePerTitolo(String titoloParziale) {
        List<Prenotazione> trovate = new ArrayList<>();

        for (Prenotazione p : prenotazioni.values()) {
            // Controllo se la sotto-stringa compare nel titolo di un film
            if (p.getProiezione().getFilm().getTitolo().toLowerCase().contains(titoloParziale.toLowerCase())) {
                trovate.add(p);
            }
        }
        return trovate;
    }

    /**
     * Ricerca le prenotazioni in cui la data della proiezione ricade in un determinato intervallo.
     * Permette ricerche aperte passando "null" come valore di uno dei due limiti.
     *
     * @param dataInizio Il limite inferiore (incluso). Passare null per ignorarlo.
     * @param dataFine Il limite superiore (incluso). Passare null per ignorarlo.
     * @return Una lista di prenotazioni che rispettano i criteri temporali.
     */
    public List<Prenotazione> cercaPrenotazionePerDate(LocalDate dataInizio, LocalDate dataFine) {
        List<Prenotazione> trovate = new ArrayList<>();
        boolean isDopoInizio, isPrimaFine;

        for (Prenotazione p : prenotazioni.values()) {

            LocalDate dataProiezione = p.getProiezione().getDataOra().toLocalDate();

            // Verifica dataInizio
            if (dataInizio == null) {
                // L'utente non ha espresso un limite iniziale (ricerca aperta).
                isDopoInizio = true;
            } else if (dataProiezione.isBefore(dataInizio)) {
                // La data dello spettacolo è strettamente precedente alla data di inizio richiesta.
                isDopoInizio = false;
            } else {
                // La data dello spettacolo è uguale o successiva alla data di inizio.
                isDopoInizio = true;
            }

            // Verifica dataFine
            if (dataFine == null) {
                // L'utente non ha espresso un limite finale (ricerca aperta verso il futuro).
                isPrimaFine = true;
            } else if (dataProiezione.isAfter(dataFine)) {
                // La data dello spettacolo è strettamente successiva alla data di fine richiesta.
                isPrimaFine = false;
            } else {
                // La data dello spettacolo è uguale o precedente alla data di fine.
                isPrimaFine = true;
            }

            // Se rispetta i vincoli aggiungo
            if (isDopoInizio && isPrimaFine) {
                trovate.add(p);
            }
        }

        return trovate;
    }

    // GESTIONE FILE
    /**
     * Legge le prenotazioni dal file CSV e le carica nella memoria dell'applicazione.
     * Per ogni riga del file, il metodo estrae le informazioni testuali e utilizza
     * i gestori passati come parametro per recuperare i riferimenti agli oggetti
     * reali di tipo Cliente e Proiezione, garantendo l'integrità dei dati.
     *
     * @param gestoreUtenti Il gestore necessario per ricercare l'oggetto Cliente tramite username.
     * @param gestoreProiezioni Il gestore necessario per ricercare l'oggetto Proiezione tramite titolo e data.
     */
    public void caricaDaFile(GestoreUtenti gestoreUtenti, GestoreProiezioni gestoreProiezioni) {
        File f = new File(FILE_PATH);

        // Inizializzo la HashMap per evitare che rimanga null
        this.prenotazioni = new HashMap<>();

        // Controllo di sicurezza: se il file non esiste, lo creiamo automaticamente
        if (!f.exists()) {
            try {
                // Controllo anche se esiste la cartella padre "data" altrimenti la creo
                File cartellaPadre = f.getParentFile();
                if (cartellaPadre != null && !cartellaPadre.exists()) { // Lazy Evaluation
                    cartellaPadre.mkdirs();
                }
                f.createNewFile();
                return;

            } catch (IOException e) {
                System.out.println("\u001B[31mErrore critico: impossibile creare il file delle prenotazioni: " + e.getMessage() + "\u001B[0m");
                return;
            }
        }

        try {
            FileReader r = new FileReader(f);
            BufferedReader fIn = new BufferedReader(r);

            String line = fIn.readLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int posti, codiceNumerico;

            while (line != null) {
                String[] campi = line.split(",");

                if (campi.length == 5) {
                    String codice = campi[0];
                    String username = campi[1];
                    String titoloFilm = campi[2];
                    String dataString = campi[3];

                    try {
                        LocalDateTime dataOra = LocalDateTime.parse(dataString, formatter);
                        posti = Integer.parseInt(campi[4]);

                        // Rimuovo il prefisso "PR" e convertiamo la parte rimanente in un intero.
                        codiceNumerico = Integer.parseInt(codice.replace("PR", ""));

                        // Se il codice letto nel file è maggiore o uguale al contatore attuale,
                        // sposto in avanti il contatore per garantire che le prossime nuove prenotazioni
                        // abbiano un ID successivo e totalmente inedito.
                        if (codiceNumerico >= this.contatoreCodici) {
                            this.contatoreCodici = codiceNumerico + 1;
                        }

                        Cliente cliente = (Cliente) gestoreUtenti.cercaUtentePerUsername(username);
                        Proiezione proiezione = gestoreProiezioni.cercaProiezioneEsatta(titoloFilm, dataOra);

                        // Validazione dati: se qualcosa manca, scatta l'eccezione personalizzata
                        if (cliente == null) {
                            throw new PrenotazioneException("Il cliente con username '" + username + "' non è presente nel database utenti.");
                        }
                        if (proiezione == null) {
                            throw new PrenotazioneException("La proiezione per il film '" + titoloFilm + "' in data " + dataString + " non esiste a palinsesto.");
                        }

                        // Se i controlli passano, l'oggetto viene costruito in sicurezza
                        Prenotazione p = new Prenotazione(codice, cliente, proiezione, posti);
                        this.prenotazioni.put(codice, p);

                    } catch (PrenotazioneException e) {
                        // Catturo l'eccezione e segnaliamo il problema specifico,
                        // ma NON interrompiamo il ciclo. La riga successiva verrà letta normalmente.
                        System.out.println("\u001B[31mErrore di integrità alla riga [" + codice + "]: " + e.getMessage() + "\u001B[0m");
                    } catch (Exception e) {
                        // Protezione da date malformate nel CSV
                        System.out.println("\u001B[31mErrore di formato dati alla riga [" + codice + "]: campi corrotti nel file.\u001B[0m");
                    }
                }else {
                    throw new NumeroCampiErratoException("I campi ricevuti risultano diversi o insufficienti");
                }
                line = fIn.readLine();
            }
            fIn.close();
            r.close();

        } catch (IOException e) {
            System.out.println("\u001B[31mErrore di lettura del file prenotazioni: " + e.getMessage() + "\u001B[0m");
        }
    }

    /**
     * Salva l'elenco corrente delle prenotazioni nel file CSV specificato.
     * Il metodo apre il file in modalità scrittura (sovrascrivendo il contenuto precedente)
     * e registra ogni singola prenotazione presente in memoria utilizzando il formato
     * definito dal metodo toCsv() della classe Prenotazione.
     */
    public void salvaSuFile() {
        try {
            File f = new File(FILE_PATH);
            FileWriter w = new FileWriter(f, false);
            PrintWriter fOut = new PrintWriter(w);

            for (Prenotazione p : prenotazioni.values()) {
                fOut.print(p.toCsv()); // Per ogni prenotazione salvo il formato csv
                fOut.print("\n");
            }
            fOut.close();
            w.close();

        } catch(FileNotFoundException e) {
            System.out.println("\u001B[31mErrore: Impossibile trovare o creare il file delle prenotazioni.\u001B[0m");
            System.out.println("\u001B[31mAssicurati che la cartella 'data' esista nel percorso del progetto. Dettagli: " + e.getMessage() + "\u001B[0m");
        } catch (IOException e) {
            // Questo gestisce errori generici di scrittura (es. file bloccato da un altro programma)
            System.out.println("\u001B[31mErrore di input/output durante la scrittura sul file delle prenotazioni: " + e.getMessage() + "\u001B[0m");
        }
    }
}