package cinemax.modelli;

import java.util.Objects;

/**
 * Questa classe rappresenta un film all'interno del sistema CineMax.
 * Memorizza le informazioni essenziali quali il titolo, il genere, il regista, l'anno,
 * la durata e l'età minima consigliata per la visione.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 *
 */
public class Film {

    private String titolo;
    private Genere genere;
    private String regista;
    private int anno;
    private int durata; // in minuti
    private int etaMinima;

    /**
     * Costruttore completo della classe Film.
     * Inizializza tutti i campi relativi alla scheda del film.
     *
     * @param titolo    Il titolo del film.
     * @param genere    Il genere cinematografico (istanza di {@link Genere}).
     * @param regista   Il nome del regista.
     * @param anno      L'anno di pubblicazione o rilascio.
     * @param durata    La durata complessiva del film espressa in minuti.
     * @param etaMinima L'età minima consigliata per la visione (0 se adatto a tutti).
     */
    public Film(String titolo, Genere genere, String regista, int anno, int durata, int etaMinima) {
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata = durata;
        this.etaMinima = etaMinima;
    }

    /**
     * Restituisce il titolo del film.
     *
     * @return Il titolo del film.
     */
    public String getTitolo() { return titolo; }

    /**
     * Restituisce il genere del film.
     *
     * @return Il genere cinematografico.
     */
    public Genere getGenere() { return genere; }

    /**
     * Restituisce il nome del regista del film.
     *
     * @return Il regista del film.
     */
    public String getRegista() { return regista; }

    /**
     * Restituisce l'anno di rilascio del film.
     *
     * @return L'anno del film.
     */
    public int getAnno() { return anno; }

    /**
     * Restituisce la durata del film in minuti.
     *
     * @return La durata in minuti.
     */
    public int getDurata() { return durata; }

    /**
     * Restituisce l'età minima consigliata per la visione del film.
     *
     * @return L'età minima (0 se per tutti).
     */
    public int getEtaMinima() { return etaMinima; }

    /**
     * Converte gli attributi della classe film in un formato CSV standard,
     * racchiudendo le stringhe tra virgolette doppie per evitare problemi con le virgole.
     *
     * @return Una stringa formattata CSV contenente le informazioni del film.
     */
    public String toCSV() {
        return "\"" + titolo + "\"," +
                genere + "," +
                "\"" + regista + "\"," +
                anno + "," +
                durata + "," +
                etaMinima;
    }

    /**
     * Confronta questo film con un altro oggetto per verificarne l'uguaglianza.
     * Due film sono ritenuti uguali se e solo se coincidono tutti i loro attributi principali.
     *
     * @param o L'oggetto da confrontare.
     * @return true se gli oggetti sono logicamente equivalenti, false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Film film)) return false;
        return anno == film.anno && durata == film.durata && etaMinima == film.etaMinima && Objects.equals(titolo, film.titolo) && genere == film.genere && Objects.equals(regista, film.regista);
    }

    /**
     * Genera l'hash code per l'istanza corrente di Film basandosi su tutti i suoi campi.
     *
     * @return Il valore intero dell'hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(titolo, genere, regista, anno, durata, etaMinima);
    }

    /**
     * Restituisce una rappresentazione testuale leggibile ed incolonnata dei dettagli del film,
     * ideale per l'output su terminale.
     *
     * @return Una stringa formattata contenente la scheda del film.
     */
    @Override
    public String toString() {
        // Gestione per l'età minima
        String etaFormattata = (etaMinima > 0) ? etaMinima + "+" : "Per tutti";

        // String.format per incolonnare bene i dati
        return String.format("Titolo: %-25s | Anno: %4d | Regista: %-20s | Genere: %-12s | Durata: %3d min | Età: %s",
                titolo, anno, regista, genere, durata, etaFormattata);
    }
}