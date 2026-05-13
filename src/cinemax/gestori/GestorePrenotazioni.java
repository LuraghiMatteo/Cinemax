package cinemax.gestori;

import cinemax.Eccezioni.PostiEsauritiException;
import cinemax.modelli.Genere;
import cinemax.modelli.Prenotazione;
import cinemax.modelli.Proiezione;
import cinemax.modelli.Cliente;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Questa classe rappresenta il gestore di tutte le prenotazioni fatte dagli utenti.
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class GestorePrenotazioni {

    // Attributi
    private List<Prenotazione> prenotazioni;
    private static final int CAPIENZA_SALA = 200;
    private final String FILE_PATH = "data" + File.separator + "prenotazioni.csv";

    // Costruttore
    /**
     * Costruttore della classe: Inizializza un ArrayList di Prenotazioni vuota
     */
    public GestorePrenotazioni() {
        this.prenotazioni = new ArrayList<>();
    }

    // --- METODI DI SUPPORTO ---
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
        for(Prenotazione p : prenotazioni){
            if(p.getProiezione().equals(proiezione)){ // Verifico che la proiezione della prenotazione sia uguale a quella passata
                numPostiOccupati += p.getNumeroPosti(); // Calcolo posti occupati
            }
        }

        numPostiLiberi = CAPIENZA_SALA - numPostiOccupati; // Calcolo posti liberi

        return numPostiLiberi;
    }

    /**
     * Genera un codice alfanumerico univoco di 6 caratteri per una nuova prenotazione.
     * Il metodo garantisce l'univocità confrontando ogni nuovo codice generato
     * con quelli già presenti nella lista delle prenotazioni esistenti.
     *
     * @return Una stringa di 6 caratteri che rappresenta il codice univoco della prenotazione.
     */
    private String generaCodiceUnivoco() {
        String caratteri = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codiceTrovato = new StringBuilder(); // Codice da restituire
        Random rnd = new Random();
        boolean esisteGia;
        int lenCodice = 6; // Lunghezza del codice univoco

        // Fintanto che non trovo un codice univoco continuo a generarlo
        do {
            // Svuoto la stringa se il ciclo sta facendo un secondo tentativo
            codiceTrovato.setLength(0);

            // Creazione del codice
            int indiceCasuale;
            for (int i = 0; i < lenCodice; i++) {
                indiceCasuale = rnd.nextInt(caratteri.length()); // Genero l'indice
                codiceTrovato.append(caratteri.charAt(indiceCasuale)); // Prendo il carattere e lo attacco alla stringa
            }

            // Controllo se il codice esiste già nella lista delle prenotazioni lette/salvate
            esisteGia = false;
            for (Prenotazione p : prenotazioni) {
                if (p.getCodiceUnivoco().contentEquals(codiceTrovato)) {
                    esisteGia = true;
                    break;
                }
            }

        } while(esisteGia);

        return codiceTrovato.toString();
    }

    // --- METODI PER IL CLIENTE ---
    /**
     * Crea una nuova prenotazione se i posti sono disponibili.
     * @param cliente Il cliente che prenota
     * @param proiezione La proiezione scelta
     * @param numeroPosti Quanti posti vuole
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

        // Aggiornamento lista e file
        prenotazioni.add(nuova);
        salvaSuFile();

        return nuova;
    }

    /**
     * Recupera l'elenco di tutte le prenotazioni effettuate da un cliente specifico.
     * Questo metodo separa la logica di ricerca dalla visualizzazione, consentendo
     * alla classe CineMax (TUI) di gestire la formattazione dell'output.
     *
     * @param cliente L'oggetto Cliente di cui si vogliono recuperare le prenotazioni.
     * @return Una lista di oggetti Prenotazione associati al cliente; la lista sarà vuota se non ci sono match.
     */
    public List<Prenotazione> getPrenotazioniPerCliente(Cliente cliente) {
        List<Prenotazione> filtrate = new ArrayList<>();

        for (Prenotazione p : prenotazioni) {
            // Verifico se la prenotazione appartiene al cliente passato sfruttando il metodo equals della classe Cliente
            if (p.getCliente().equals(cliente)) {
                filtrate.add(p);
            }
        }

        return filtrate;
    }

    /**
     * Modifica la data di una prenotazione esistente sostituendo la proiezione associata.
     * Come da specifiche, l'operazione è consentita solo se la proiezione originale
     * e quella nuova sono entrambe successive alla data/ora odierna e se ci sono posti disponibili.
     *
     * @param codiceUnivoco Il codice della prenotazione da modificare.
     * @param nuovaProiezione Il nuovo spettacolo a cui il cliente vuole partecipare.
     * @return true se la modifica ha successo, false se fallisce (es. regole non rispettate).
     */
    public boolean modificaPrenotazione(String codiceUnivoco, Proiezione nuovaProiezione) {
        Prenotazione daModificare = null;

        // Cerco la prenotazione
        for (Prenotazione p : prenotazioni) {
            if (p.getCodiceUnivoco().equals(codiceUnivoco)) {
                daModificare = p;
                break;
            }
        }

        // Se non la trovo, mando un mssaggio di errore
        if (daModificare == null) {
            System.out.println("Errore: Prenotazione non trovata.");
            return false;
        }

        // Prendo l'ora attuale
        LocalDateTime oraAttuale = LocalDateTime.now();

        // Controllo regole temporali: posso modificarla a patto che sia la vecchia che la nuova data siano successive alla data odierna)
        // Se la proiezione originale è già passata, non consento l'eliminazione
        if (daModificare.getProiezione().getDataOra().isBefore(oraAttuale)) {
            System.out.println("Errore: La proiezione originale è già passata. Impossibile modificare.");
            return false;
        }

        // Se la nuova data scelta è già passata, non consento la modifica
        if (nuovaProiezione.getDataOra().isBefore(oraAttuale)) {
            System.out.println("Errore: La nuova data scelta è nel passato.");
            return false;
        }

        // Controllo che il cliente non cambi film, ma che mantenga lo stesso della prenotazione originale
        if (!daModificare.getProiezione().getFilm().getTitolo().equals(nuovaProiezione.getFilm().getTitolo())) {
            System.out.println("Errore: Stai cercando di cambiare film. Usa la cancellazione e fai una nuova prenotazione.");
            return false;
        }

        // Controllo disponibilità posti per la nuova proiezione
        int postiLiberiNuova = calcolaPostiLiberi(nuovaProiezione);
        if (daModificare.getNumeroPosti() > postiLiberiNuova) {
            System.out.println("Errore: Non ci sono abbastanza posti liberi nella nuova data.");
            return false;
        }

        // Se supera tutti i controlli applichiamo la modifica e salviamo su file
        daModificare.setProiezione(nuovaProiezione);
        salvaSuFile();

        System.out.println("Prenotazione aggiornata con successo alla nuova data: " + nuovaProiezione.getDataOra());
        return true;
    }

    /**
     * Elimina una prenotazione dal sistema ricercandola tramite il suo codice univoco.
     * La cancellazione va a buon fine solo se la prenotazione esiste e se la data
     * della proiezione non è ancora passata (vincolo temporale).
     *
     * @param codiceUnivoco La stringa di 6 caratteri che identifica la prenotazione.
     * @return true se l'eliminazione è avvenuta con successo, false altrimenti.
     */
    public boolean eliminaPrenotazione(String codiceUnivoco) {
        Prenotazione daRimuovere = null;

        // Scorro la lista solo per trovare l'oggetto
        for (Prenotazione p : prenotazioni) {
            if (p.getCodiceUnivoco().equals(codiceUnivoco)) {
                daRimuovere = p;
                break; // Fermiamo il ciclo per risparmiare tempo quando la troviamo
            }
        }

        // Se non trovo la prenotazione genero un errore
        if (daRimuovere == null) {
            System.out.println("Errore: Nessuna prenotazione trovata con il codice " + codiceUnivoco);
            return false;
        }

        // Impediamo di cancellare prenotazioni di spettacoli già iniziati o passati
        if (daRimuovere.getProiezione().getDataOra().isBefore(LocalDateTime.now())) {
            System.out.println("Errore: Impossibile cancellare la prenotazione. Lo spettacolo è già passato.");
            return false;
        }

        // Elimino la prenotazione e salvo gli aggiornamenti su file
        prenotazioni.remove(daRimuovere);
        salvaSuFile();

        return true;
    }

    // --- METODI PER IL BIGLIETTAIO ---
    /**
     * Recupera l'elenco di tutte le prenotazioni relative agli spettacoli previsti per la giornata odierna.
     * Ignora l'orario specifico e confronta solo l'anno, il mese e il giorno.
     *
     * @return Una lista (eventualmente vuota) contenente le prenotazioni valide per oggi.
     */
    public List<Prenotazione> visualizzaPrenotazioniOdierne() {
        List<Prenotazione> prenotazioniOdierne = new ArrayList<>();
        LocalDate oggi = LocalDate.now(); // Recupero la data odierna

        // Salvo solo le prenotazioni con data odierna
        for (Prenotazione p : prenotazioni) {
            if (p.getProiezione().getDataOra().toLocalDate().equals(oggi)) {
                prenotazioniOdierne.add(p);
            }
        }

        // Se non ci sono prenotazioni odierne
        if (prenotazioniOdierne.isEmpty()) {
            System.out.println("Nessuna prenotazione trovata per gli spettacoli di oggi.");
        }

        return prenotazioniOdierne;
    }

    /**
     * Ricerca una specifica prenotazione tramite il suo codice univoco.
     * Poiché i codici sono univoci, il metodo restituisce un singolo oggetto o null.
     *
     * @param codice Il codice alfanumerico di 6 caratteri da cercare.
     * @return La prenotazione trovata, oppure null se inesistente.
     */
    public Prenotazione cercaPrenotazionePerCodice(String codice) {
        for (Prenotazione p : prenotazioni) {
            // Uso equalsIgnoreCase per evitare problemi se il bigliettaio digita in minuscolo
            if (p.getCodiceUnivoco().equalsIgnoreCase(codice)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Ricerca tutte le prenotazioni effettuate da un cliente specifico.
     *
     * @param nome Il nome del cliente.
     * @param cognome Il cognome del cliente.
     * @return Una lista di prenotazioni associate a quel cliente.
     */
    public List<Prenotazione> cercaPrenotazionePerCliente(String nome, String cognome) {
        List<Prenotazione> trovate = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getCliente().getNome().equalsIgnoreCase(nome) && p.getCliente().getCognome().equalsIgnoreCase(cognome)) {
                trovate.add(p);
            }
        }
        return trovate;
    }

    /**
     * Ricerca tutte le prenotazioni per proiezioni il cui titolo contiene la stringa cercata (ricerca parziale).
     *
     * @param titoloParziale La parola o frase da cercare nel titolo del film.
     * @return Una lista di prenotazioni pertinenti.
     */
    public List<Prenotazione> cercaPrenotazionePerTitolo(String titoloParziale) {
        List<Prenotazione> trovate = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            // Trasformo entrambi i titoli in minuscolo e verifico se il titolo completo "contiene" la stringa cercata
            if (p.getProiezione().getFilm().getTitolo().toLowerCase().contains(titoloParziale.toLowerCase())) {
                trovate.add(p);
            }
        }
        return trovate;
    }

    /**
     * Ricerca le prenotazioni in cui la data della proiezione ricade in un determinato intervallo.
     * È possibile lasciare uno dei due limiti a "null" per fare ricerche aperte (es. "solo dopo una certa data").
     *
     * @param dataInizio Il limite inferiore (incluso). Passare null per ignorarlo.
     * @param dataFine Il limite superiore (incluso). Passare null per ignorarlo.
     * @return Una lista di prenotazioni che rispettano i criteri temporali.
     */
    public List<Prenotazione> cercaPrenotazionePerDate(LocalDate dataInizio, LocalDate dataFine) {
        List<Prenotazione> trovate = new ArrayList<>();
        boolean isDopoInizio, isPrimaFine;

        for (Prenotazione p : prenotazioni) {
            // Estraggo la data della proiezione associata alla prenotazione
            LocalDate dataProiezione = p.getProiezione().getDataOra().toLocalDate();

            // Controllo data inizio
            if (dataInizio == null) {
                isDopoInizio = true; // Nessun limite iniziale: va sempre bene
            } else if (dataProiezione.isBefore(dataInizio)) {
                isDopoInizio = false; // La data è troppo vecchia: scartiamo
            } else {
                isDopoInizio = true; // La data è uguale o successiva all'inizio: ok
            }

            // Controllo data fine
            if (dataFine == null) {
                isPrimaFine = true; // Nessun limite finale: va sempre bene
            } else if (dataProiezione.isAfter(dataFine)) {
                isPrimaFine = false; // La data è troppo in là nel futuro: scartiamo
            } else {
                isPrimaFine = true; // La data è uguale o precedente alla fine: ok
            }

            // Se rispetta entrambi i criteri, la aggiungiamo alla lista dei risultati
            if (isDopoInizio && isPrimaFine) {
                trovate.add(p);
            }
        }

        return trovate;
    }

    // --- GESTIONE FILE ---
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

        // Controllo di sicurezza: se il file non esiste (es. primo avvio assoluto), usciamo senza errori
        if (!f.exists()) {
            return;
        }

        try {
            FileReader r = new FileReader(f);
            BufferedReader fIn = new BufferedReader(r);

            String line = fIn.readLine();
            // Creo il data formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (line != null) {
                // Divido la riga usando la virgola
                String[] campi = line.split(",");

                // Controllo di sicurezza per evitare righe vuote o malformate
                if (campi.length == 5) {
                    String codice = campi[0];
                    String username = campi[1];

                    // Rimuovo le virgolette "" che avevo aggiunto nel salvataggio
                    String titoloFilm = campi[2].replace("\"", "");
                    String dataString = campi[3].replace("\"", "");

                    // Converto la data da String a LocalDateTime
                    LocalDateTime dataOra = LocalDateTime.parse(dataString, formatter);

                    // Converto il numero di posti da String a int
                    int posti = Integer.parseInt(campi[4]);

                    // Ricostruzione degli oggetti Cliente e Proiezione contenuti nella prenotazione
                    // Chiediamo ai rispettivi gestori di darci gli oggetti corrispondenti
                    Cliente cliente = (Cliente) gestoreUtenti.cercaUtentePerUsername(username);
                    Proiezione proiezione = gestoreProiezioni.cercaProiezioneEsatta(titoloFilm, dataOra);

                    // Se abbiamo trovato sia il cliente che la proiezione, ricreiamo la prenotazione
                    if (cliente != null && proiezione != null) {
                        Prenotazione p = new Prenotazione(codice, cliente, proiezione, posti);
                        prenotazioni.add(p);
                    } else {
                        System.out.println("Attenzione: impossibile ricostruire la prenotazione " + codice + " (Cliente o Proiezione mancanti).");
                    }
                }
                line = fIn.readLine(); // Leggo la riga successiva
            }
            fIn.close();

        } catch (FileNotFoundException e) {
            System.out.println("File prenotazioni non trovato: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Errore di lettura del file prenotazioni: " + e.getMessage());
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
            FileWriter w = new FileWriter(f);
            PrintWriter fOut = new PrintWriter(w);

            for (Prenotazione p : prenotazioni) {
                fOut.print(p.toCsv()); // Per ogni prenotazione salvo il formato csv
                fOut.print("\n");
            }
            fOut.close();

        } catch(FileNotFoundException e) {
            System.out.println("Errore: Impossibile trovare o creare il file delle prenotazioni.");
            System.out.println("Assicurati che la cartella 'data' esista nel percorso del progetto. Dettagli: " + e.getMessage());
        } catch (IOException e) {
            // Questo gestisce errori generici di scrittura (es. file bloccato da un altro programma)
            System.out.println("Errore di input/output durante la scrittura sul file delle prenotazioni: " + e.getMessage());
        }
    }
}