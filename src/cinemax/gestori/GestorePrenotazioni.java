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

    private List<Prenotazione> prenotazioni;
    private static final int CAPIENZA_SALA = 200;
    private final String FILE_PATH = "data" + File.separator + "prenotazioni.csv";

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
            for (int i = 0; i < lenCodice; i++) {
                int indiceCasuale = rnd.nextInt(caratteri.length()); // Genero l'indice
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
        // Controllo disponibilità
        int postiDisponibili = calcolaPostiLiberi(proiezione);

        // Controllo e nel caso lancio eccezione
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

    public void visualizzaPrenotazioniCliente(Cliente cliente) {
    }

    public void modificaPrenotazione(String codiceUnivoco, LocalDateTime nuovaData) {}

    public void eliminaPrenotazione(String codiceUnivoco) {}


    // --- METODI PER IL BIGLIETTAIO ---
    public List<Prenotazione> visualizzaPrenotazioniOdierne() {
        return null;
    }

    public List<Prenotazione> cercaPrenotazione(String codice, String nome, String cognome, String titoloFilm, LocalDate dataInizio, LocalDate dataFine) {
        return null;
    }

    public Prenotazione cercaPrenotazione(String titolo) {
        return null;
    }

    public Prenotazione cercaPrenotazione(Genere genere) {
        return null;
    }

    public Prenotazione cercaPrenotazione(LocalDate dataInizio, LocalDate dataFine) { //ci sono tante varianti
        return null;
    }

    public Prenotazione cercaPrenotazione(double costoBiglietto) { //ci sono tante varianti
        return null;
    }

    // Visualizzazione prenotazione generica (bigliettaio anche)


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