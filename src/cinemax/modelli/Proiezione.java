package cinemax.modelli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Questa classe rappresenta l'oggetto proiezione.
 * Memorizza le informazioni essenziali quali la data e ora, il film e il costo del biglietto
 * di una determinata proiezione
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class Proiezione implements Comparable<Proiezione> {
    private LocalDateTime dataOra;
    private Film film;
    private double costoBiglietto;

    /**
     * Costruttore della classe proiezione
     * @param dataOra
     * @param film
     * @param costoBiglietto
     */
    public Proiezione(LocalDateTime dataOra, Film film, double costoBiglietto) {
        this.dataOra = dataOra;
        this.film = film;
        this.costoBiglietto = costoBiglietto;
    }

    // Getters e Setters
    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }
    public Film getFilm() { return film; }
    public double getCostoBiglietto() { return costoBiglietto; }

    /**
     * Converte gli attributi nella classe proiezione in un formato csv
     * @return "dataOra",film.toCSV(),costoBiglietto
     */
    public String toCsv() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String dataFormattata = this.dataOra.format(formatter);

        return "\"" + dataFormattata + "\"," +
                film.toCSV() + "," +
                costoBiglietto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Proiezione that)) return false;
        return Double.compare(costoBiglietto, that.costoBiglietto) == 0 && Objects.equals(dataOra, that.dataOra) && Objects.equals(film, that.film);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataOra, film, costoBiglietto);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%s] %s - Prezzo: %.2f€", dataOra.format(formatter), film.getTitolo(), costoBiglietto);
    }

    @Override
    public int compareTo(Proiezione o) {
        return this.dataOra.compareTo(o.dataOra);
    }
}