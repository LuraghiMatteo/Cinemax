package cinemax.modelli;

import java.time.format.DateTimeFormatter;

/**
 * Questa classe rappresenta l'oggetto prenotazione che può essere effettuata da un cliente.
 * Memorizza le informazioni essenziali quali il codice univoco identificativo, il cliente
 * che ha eseguito l'operazione, la proiezione di riferimento e il numero di posti riservati.
 * * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class Prenotazione {
    private String codiceUnivoco; // Richiesto univoco dalle specifiche
    private Cliente cliente;
    private Proiezione proiezione;
    private int numeroPosti;

    /**
     * Costruisce un nuovo oggetto Prenotazione inizializzando tutti i suoi campi di tessitura.
     *
     * @param codiceUnivoco Il codice alfanumerico univoco di 6 caratteri generato dal sistema.
     * @param cliente       L'oggetto Cliente registrato che effettua la prenotazione.
     * @param proiezione    La specifica proiezione cinematografica scelta dal cliente.
     * @param numeroPosti   Il quantitativo di posti a sedere richiesti per la visione dello spettacolo.
     */
    public Prenotazione(String codiceUnivoco, Cliente cliente, Proiezione proiezione, int numeroPosti) {
        this.codiceUnivoco = codiceUnivoco;
        this.cliente = cliente;
        this.proiezione = proiezione;
        this.numeroPosti = numeroPosti;
    }

    // Getters
    public String getCodiceUnivoco() { return codiceUnivoco; }
    public Cliente getCliente() { return cliente; }
    public Proiezione getProiezione() { return proiezione; }
    public int getNumeroPosti() { return numeroPosti; }

    // Setters
    /**
     * Consente di modificare o aggiornare la proiezione associata alla prenotazione.
     * Questo metodo si rivela fondamentale per supportare la logica di business del "cambio data"
     * richiesta dalle specifiche funzionali di modifica.
     *
     * @param nuovaProiezione La nuova istanza di Proiezione da associare alla prenotazione corrente.
     */
    public void setProiezione(Proiezione nuovaProiezione) {
        this.proiezione = nuovaProiezione;
    }

    /**
     * Calcola il costo totale della prenotazione moltiplicando il numero di posti
     * per il costo del singolo biglietto della proiezione associata.
     * * @return Il valore double corrispondente al costo complessivo in euro.
     */
    public double calcolaCostoTotale() {
        return this.numeroPosti * this.proiezione.getCostoBiglietto();
    }

    /**
     * Genera una rappresentazione testuale formattata ed esauriente della prenotazione.
     * È specificatamente strutturata per rispondere ai requisiti di visualizzazione TUI
     * sia per il cliente che per il bigliettaio, mostrando i dati di riepilogo finanziario ed anagrafico.
     *
     * @return Una stringa multiline pronta per la stampa a terminale.
     */
    @Override
    public String toString() {
        return String.format("Prenotazione #%s\nCliente: %s %s\nSpettacolo: %s\nPosti: %d | Totale: %.2f€",
                codiceUnivoco, cliente.getNome(), cliente.getCognome(),
                proiezione.getFilm().getTitolo(), numeroPosti, calcolaCostoTotale());
    }

    /**
     * Converte i dati salienti della prenotazione in una stringa formattata secondo
     * lo standard CSV classico (campi separati da virgola senza delimitatori testuali aggiuntivi).
     * Questo metodo estrae esclusivamente le chiavi esterne (username cliente, titolo film, data/ora)
     * per salvaguardare la consistenza del file ed evitare ridondanze nel file di persistenza.
     *
     * @return Una riga di testo in formato CSV pronta per essere memorizzata nel file di persistenza.
     */
    public String toCsv() {
        // Creo il formatter per la data seguendo lo standard del database testuale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Formatto la data temporale della proiezione
        String dataFormattata = this.proiezione.getDataOra().format(formatter);

        // Composizione della riga finale: Codice, Username, TitoloFilm, DataOra, Posti
        return String.format("%s,%s,%s,%s,%d",
                this.codiceUnivoco,
                this.cliente.getUsername(),
                this.proiezione.getFilm().getTitolo(),
                dataFormattata,
                this.numeroPosti);
    }
}