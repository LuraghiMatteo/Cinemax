package cinemax.gestori;

import cinemax.eccezioni.DataNonValidaException;
import cinemax.eccezioni.NumeroCampiErratoException;
import cinemax.modelli.Film;
import cinemax.modelli.Proiezione;
import cinemax.modelli.Genere;
import cinemax.eccezioni.CostoNonValidoException;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestisce la logica di business per le proiezioni cinematografiche.
 * Permette il caricamento e il salvataggio del palinsesto su file CSV, l'inserimento,
 * la modifica dell'orario e la cancellazione di proiezioni programmate.
 * Offre inoltre diversi metodi di ricerca filtrata per titolo, genere, date e prezzi.
 * Il palinsesto è mantenuto costantemente all'interno di una HashMap per ottimizzare le ricerche.
 *
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class GestoreProiezioni {

    private Map<LocalDateTime, Proiezione> palinsesto;
    private final String FILE_PATH = "data" + File.separator + "proiezioni.csv";

    /**
     * Costruttore della classe: Inizializza un nuovo gestore delle proiezioni con una mappa vuota.
     */
    public GestoreProiezioni() {
        this.palinsesto = new HashMap<>();
    }

    /**
     * Aggiunge una nuova proiezione al palinsesto se non è già presente un'altra proiezione nella stessa data e ora.
     *
     * @param proiezione la proiezione da aggiungere al palinsesto
     * @return true se l'inserimento ha avuto successo, false se esiste già una proiezione con la stessa data e ora
     * @throws NullPointerException se la proiezione passata è null
     */
    public boolean aggiungiProiezione(Proiezione proiezione) {
        if (proiezione == null) {
            throw new NullPointerException("La proiezione è null");
        }
        if (palinsesto.containsKey(proiezione.getDataOra())) {
            return false;
        }
        palinsesto.put(proiezione.getDataOra(), proiezione);
        return true;
    }

    /**
     * Modifica la data e l'ora di una proiezione esistente nel palinsesto, assicurando che non ci sia un'altra proiezione nella stessa data e ora.
     *
     * @param proiezione la proiezione da modificare
     * @param dataOra    la nuova data e ora da assegnare alla proiezione
     * @return true se la proiezione è stata trovata e modificata, false se c'è un conflitto con un'altra proiezione allo stesso orario
     * @throws NullPointerException se la nuova data e ora passata è null
     */
    public boolean modificaProiezione(Proiezione proiezione, LocalDateTime dataOra) {
        if (dataOra == null) {
            throw new NullPointerException("la nuova data e ora non devono essere null");
        }
        if (palinsesto.containsKey(proiezione.getDataOra())) {
            // Se c'è già una proiezione al nuovo orario (diverso da quello attuale), rifiutiamo
            if (!proiezione.getDataOra().equals(dataOra) && palinsesto.containsKey(dataOra)) {
                return false;
            }
            palinsesto.remove(proiezione.getDataOra());
            proiezione.setDataOra(dataOra);
            palinsesto.put(dataOra, proiezione);
            return true;
        }
        return false;
    }

    /**
     * Rimuove una proiezione programmata dal palinsesto.
     *
     * @param proiezione la proiezione da eliminare
     * @return true se la proiezione è stata trovata e rimossa, false altrimenti
     */
    public boolean eliminaProiezione(Proiezione proiezione) {
        return palinsesto.remove(proiezione.getDataOra()) != null;
    }

    // --- METODI DI RICERCA (Guest, Cliente, Bigliettaio) ---

    /**
     * Cerca tutte le proiezioni il cui titolo del film contiene la stringa specificata.
     * La ricerca non fa distinzione tra lettere maiuscole e minuscole (case-insensitive).
     *
     * @param titoloFilm il titolo (o parte di esso) del film da cercare
     * @return una lista contenente tutte le proiezioni corrispondenti trovate
     * @throws NullPointerException se il titolo del film è null
     */
    public List<Proiezione> cercaProiezione(String titoloFilm) {
        if (titoloFilm == null) {
            throw new NullPointerException("Il titolo del film non può essere null");
        }
        if (titoloFilm.isEmpty()) {
            return new ArrayList<>();
        }
        List<Proiezione> proiezioni = new ArrayList<>();
        for (Proiezione p : palinsesto.values()) {
            if (p.getFilm().getTitolo().toLowerCase().contains(titoloFilm.toLowerCase())) {
                proiezioni.add(p);
            }
        }
        proiezioni.sort(null);
        return proiezioni;
    }

    /**
     * Cerca tutte le proiezioni associate a un determinato genere cinematografico.
     *
     * @param genere il genere del film da cercare
     * @return una lista contenente tutte le proiezioni del genere specificato
     * @throws NullPointerException se il genere è null
     */
    public List<Proiezione> cercaProiezione(Genere genere) {
        if (genere == null) {
            throw new NullPointerException("Il genere è null");
        }
        List<Proiezione> proiezioni = new ArrayList<>();
        for (Proiezione p : palinsesto.values()) {
            if (p.getFilm().getGenere().equals(genere)) {
                proiezioni.add(p);
            }
        }
        proiezioni.sort(null);
        return proiezioni;
    }

    /**
     * Cerca tutte le proiezioni programmate in un intervallo di date.
     * Se la data di inizio e di fine coincidono, cerca le proiezioni in quel giorno specifico.
     *
     * @param dataInizio la data di inizio del periodo di ricerca (inclusa)
     * @param dataFine   la data di fine del periodo di ricerca (inclusa)
     * @return una lista delle proiezioni programmate nell'intervallo temporale
     * @throws NullPointerException  se la data di inizio o di fine è null
     * @throws DataNonValidaException se la data di inizio è successiva alla data di fine
     */
    public List<Proiezione> cercaProiezione(LocalDate dataInizio, LocalDate dataFine) {
        List<Proiezione> proiezioni = new ArrayList<>();
        if (dataInizio == null || dataFine == null) {
            throw new NullPointerException("dataInizio e dataFine non devono essere null!");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new DataNonValidaException("La data d'inizio è maggiore della data di fine: dataIn = " + dataInizio + ", dataFine = " + dataFine);
        }
        for (Proiezione p : palinsesto.values()) {
            LocalDate dataP = p.getDataOra().toLocalDate();
            if (!dataP.isBefore(dataInizio) && !dataP.isAfter(dataFine)) {
                proiezioni.add(p);
            }
        }
        proiezioni.sort(null);
        return proiezioni;
    }

    /**
     * Cerca tutte le proiezioni il cui costo del biglietto rientra nel range di prezzi specificato.
     * Se il prezzo minimo e massimo coincidono, cerca le proiezioni con quel costo esatto.
     *
     * @param prezzoMin il costo minimo del biglietto da filtrare
     * @param prezzoMax il costo massimo del biglietto da filtrare
     * @return una lista delle proiezioni che soddisfano il criterio di prezzo
     * @throws CostoNonValidoException se il prezzo minimo o massimo è minore di zero,
     *                                oppure se il prezzo minimo supera il prezzo massimo
     */
    public List<Proiezione> cercaProiezione(double prezzoMin,  double prezzoMax) {
        List<Proiezione> proiezioni = new ArrayList<>();
        if (prezzoMin < 0 || prezzoMax < 0) {
            throw new CostoNonValidoException("Il prezzo minimo e massimo devono essere >= 0: prezzoMin = " + prezzoMin + ", prezzoMax = " + prezzoMax);
        }
        if (prezzoMin > prezzoMax) {
            throw new CostoNonValidoException("Il prezzo minimo deve essere minore o uguale del prezzo massimo: prezzoMin = " + prezzoMin + ", prezzoMax = " + prezzoMax);
        }

        for (Proiezione p : palinsesto.values()) {
            if (p.getCostoBiglietto() >= prezzoMin && p.getCostoBiglietto() <= prezzoMax) {
                proiezioni.add(p);
            }
        }
        proiezioni.sort(null);
        return proiezioni;
    }

    /**
     * Cerca le proiezioni applicando contemporaneamente più filtri facoltativi.
     * I parametri null o i prezzi negativi vengono ignorati ai fini della ricerca.
     *
     * @param titolo     il titolo o sotto-stringa del film da cercare (null se ignorato)
     * @param genere     il genere cinematografico da cercare (null se ignorato)
     * @param dataInizio la data iniziale dell'intervallo temporale (null se ignorata)
     * @param dataFine   la data finale dell'intervallo temporale (null se ignorata)
     * @param prezzoMin  il prezzo minimo del biglietto da filtrare (negativo se ignorato)
     * @param prezzoMax  il prezzo massimo del biglietto da filtrare (negativo se ignorato)
     * @return una lista delle proiezioni che soddisfano tutti i criteri specificati
     * @throws DataNonValidaException  se le date sono fornite e la data di inizio è successiva a quella di fine
     * @throws CostoNonValidoException se i prezzi sono forniti e il prezzo minimo è maggiore del prezzo massimo
     */
    public List<Proiezione> cercaProiezione(String titolo, Genere genere, LocalDate dataInizio, LocalDate dataFine, double prezzoMin, double prezzoMax) {

        // Controlli di validità sui parametri
        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
            throw new DataNonValidaException("La data d'inizio è maggiore della data di fine: dataIn = " + dataInizio + ", dataFine = " + dataFine);
        }
        if (prezzoMin >= 0 && prezzoMax >= 0 && prezzoMin > prezzoMax) {
            throw new CostoNonValidoException("Il prezzo minimo deve essere minore o uguale del prezzo massimo: prezzoMin = " + prezzoMin + ", prezzoMax = " + prezzoMax);
        }

        List<Proiezione> proiezioni = new ArrayList<>();

        // Iterazione sul palinsesto
        for (Proiezione p : palinsesto.values()) {

            // Filtro Titolo
            if (titolo != null && !titolo.trim().isEmpty()) {
                if (!p.getFilm().getTitolo().toLowerCase().contains(titolo.trim().toLowerCase())) {
                    continue; // Scarta se il titolo non contiene la stringa cercata
                }
            }

            // Filtro Genere
            if (genere != null) {
                if (!p.getFilm().getGenere().equals(genere)) {
                    continue; // Scarta se il genere è diverso
                }
            }

            // Filtro Date
            if (dataInizio != null && dataFine != null) {
                LocalDate dataP = p.getDataOra().toLocalDate();
                // Scarta se la data è strettamente precedente all'inizio o strettamente successiva alla fine
                if (dataP.isBefore(dataInizio) || dataP.isAfter(dataFine)) {
                    continue;
                }
            }

            // Filtro Prezzo
            if (prezzoMin >= 0 && prezzoMax >= 0) {
                if (p.getCostoBiglietto() < prezzoMin || p.getCostoBiglietto() > prezzoMax) {
                    continue; // Scarta se il costo è fuori dal range stabilito
                }
            }

            proiezioni.add(p);
        }

        proiezioni.sort(null);
        return proiezioni;
    }

    /**
     * Trova la proiezione esatta associata a un film specifico in una determinata data e ora.
     *
     * @param titoloFilm il titolo esatto del film cercato
     * @param dataOra    la data e l'ora precise della proiezione
     * @return la proiezione corrispondente se presente nel palinsesto, null altrimenti
     * @throws NullPointerException se il titolo del film o la data e ora sono null
     */
    public Proiezione cercaProiezioneEsatta(String titoloFilm, LocalDateTime dataOra){
        if (titoloFilm == null) {
            throw new NullPointerException("Il titolo non può essere null");
        }
        if (dataOra == null) {
            throw new NullPointerException("La data e ora non devono essere null");
        }
        Proiezione p = palinsesto.get(dataOra);
        if (p != null && p.getFilm().getTitolo().equalsIgnoreCase(titoloFilm)) {
            return p;
        }
        return null;
    }

    /**
     * Mostra a schermo un blocco di proiezioni (massimo 25) a partire dall'indice fornito.
     * Utile per scopi di paginazione nell'interfaccia utente testuale.
     *
     * @param proiezioni la lista complessiva delle proiezioni da impaginare
     * @param index      l'indice iniziale da cui far partire la visualizzazione
     */
    public static void visualizzaProiezioni(List<Proiezione> proiezioni, int index) {
        visualizzaProiezioni(proiezioni, index, null);
    }

    /**
     * Mostra a schermo un blocco di proiezioni (massimo 25) a partire dall'indice fornito,
     * includendo anche i posti disponibili se viene passato il gestore delle prenotazioni.
     *
     * @param proiezioni          la lista complessiva delle proiezioni da impaginare
     * @param index               l'indice iniziale da cui far partire la visualizzazione
     * @param gestorePrenotazioni il gestore delle prenotazioni per calcolare i posti liberi (opzionale)
     */
    public static void visualizzaProiezioni(List<Proiezione> proiezioni, int index, GestorePrenotazioni gestorePrenotazioni) {
        for (int i = index; i < proiezioni.size() && i < index+25; i++) {
            Proiezione p = proiezioni.get(i);
            if (gestorePrenotazioni != null) {
                System.out.println("[" + i  + "]" + p + " | Posti disponibili: " + gestorePrenotazioni.calcolaPostiLiberi(p));
            } else {
                System.out.println("[" + i  + "]" + p);
            }
        }
    }


    // --- GESTIONE FILE ---

    /**
     * Carica i dati delle proiezioni dal file CSV se questo esiste nel percorso prestabilito.
     * Ciascuna riga letta viene validata e convertita in un oggetto Proiezione.
     * Al termine del caricamento, il palinsesto viene memorizzato nella HashMap in RAM.
     *
     * @throws NumeroCampiErratoException se una riga nel file CSV non contiene esattamente 8 campi
     * @throws RuntimeException          se si verifica un errore durante la lettura del file
     */
    public void caricaDaFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line = reader.readLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (line != null) {

                String[] dati = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for(int i = 0; i < dati.length; i++){
                    dati[i] = dati[i].replace("\"", "");
                }

                if (dati.length != 8) {
                    throw new NumeroCampiErratoException("Campi richiesti 8, ricevuti: " + dati.length);
                }

                Proiezione nuovaProiezione = new Proiezione(
                        LocalDateTime.parse(dati[0], formatter),        //data e ora proiezione
                        new Film(
                                dati[1],                                //titolo
                                Genere.valueOf(dati[2].toUpperCase().replace('-', '_' )),  //genere
                                dati[3],                                //regista
                                Integer.parseInt(dati[4]),              //anno
                                Integer.parseInt(dati[5]),              //durata
                                Integer.parseInt(dati[6])               //eta minima
                                ),
                        Double.parseDouble(dati[7])                     //prezzo biglietto
                );
                palinsesto.put(nuovaProiezione.getDataOra(), nuovaProiezione);

                line = reader.readLine();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Salva l'intero palinsesto corrente all'interno del file CSV.
     * Se il file o la cartella di destinazione non esistono, vengono creati automaticamente.
     *
     * @throws RuntimeException se si verifica un errore durante la scrittura o la creazione del file
     */
    public void salvaSuFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();

            try (PrintStream ps = new PrintStream(new FileOutputStream(file, false))) {
                ps.println("data_ora_proiezione,titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto");

                List<Proiezione> proiezioni = new ArrayList<>(this.palinsesto.values());
                proiezioni.sort(null);
                for (Proiezione p : proiezioni) {
                    ps.println(p.toCsv());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
