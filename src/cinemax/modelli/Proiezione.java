package cinemax.modelli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Questa classe rappresenta un singolo spettacolo o proiezione pianificata.
 * Associa un film specifico a una data, a un orario e a un prezzo di ingresso.
 * Implementa l'interfaccia {@link Comparable} per consentire l'ordinamento naturale cronologico.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class Proiezione implements Comparable<Proiezione> {
    private LocalDateTime dataOra;
    private Film film;
    private double costoBiglietto;

    /**
     * Costruttore completo della classe Proiezione.
     *
     * @param dataOra        La data e l'ora stabilite per lo spettacolo.
     * @param film           L'oggetto {@link Film} proiettato.
     * @param costoBiglietto Il prezzo del singolo biglietto di ingresso.
     */
    public Proiezione(LocalDateTime dataOra, Film film, double costoBiglietto) {
        this.dataOra = dataOra;
        this.film = film;
        this.costoBiglietto = costoBiglietto;
    }

    /**
     * Restituisce la data e l'ora della proiezione.
     *
     * @return La data e l'ora dello spettacolo.
     */
    public LocalDateTime getDataOra() { return dataOra; }

    /**
     * Imposta o modifica la data e l'ora dello spettacolo.
     *
     * @param dataOra La nuova data e ora.
     */
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }

    /**
     * Restituisce il film associato alla proiezione.
     *
     * @return L'oggetto {@link Film}.
     */
    public Film getFilm() { return film; }

    /**
     * Restituisce il costo del biglietto per questa proiezione.
     *
     * @return Il prezzo del biglietto.
     */
    public double getCostoBiglietto() { return costoBiglietto; }

    /**
     * Converte le informazioni della proiezione in una stringa formattata CSV.
     * La data viene formattata nel pattern standard "yyyy-MM-dd HH:mm:ss".
     *
     * @return Una riga CSV contenente data, dettagli film e costo del biglietto.
     */
    public String toCsv() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String dataFormattata = this.dataOra.format(formatter);

        return "\"" + dataFormattata + "\"," +
                film.toCSV() + "," +
                costoBiglietto;
    }

    /**
     * Confronta l'istanza corrente con un altro oggetto per verificarne l'uguaglianza logica.
     * Due proiezioni sono uguali se si tengono alla stessa ora, per lo stesso film e allo stesso costo.
     *
     * @param o L'oggetto da confrontare.
     * @return true se uguali, false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Proiezione that)) return false;
        return Double.compare(costoBiglietto, that.costoBiglietto) == 0 && Objects.equals(dataOra, that.dataOra) && Objects.equals(film, that.film);
    }

    /**
     * Calcola l'hash code per l'oggetto Proiezione basandosi su tutti i suoi campi.
     *
     * @return Il valore intero calcolato.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dataOra, film, costoBiglietto);
    }

    /**
     * Restituisce una rappresentazione testuale sintetica della proiezione,
     * contenente data formattata, titolo del film e costo in euro.
     *
     * @return La stringa formattata.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%s] %s - Prezzo: %.2f€", dataOra.format(formatter), film.getTitolo(), costoBiglietto);
    }

    /**
     * Esegue il confronto cronologico tra questa proiezione ed un'altra.
     * Implementa il criterio di ordinamento naturale.
     *
     * @param o La proiezione da confrontare.
     * @return Un intero negativo, zero o positivo se questa proiezione è cronologicamente antecedente,
     *         coincidente o successiva rispetto a quella fornita.
     */
    @Override
    public int compareTo(Proiezione o) {
        return this.dataOra.compareTo(o.dataOra);
    }
}