package cinemax.modelli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Proiezione {
    private LocalDateTime dataOra;
    private Film film;
    private double costoBiglietto;

    public Proiezione(LocalDateTime dataOra, Film film, double costoBiglietto) {
        this.dataOra = dataOra;
        this.film = film;
        this.costoBiglietto = costoBiglietto;
    }

    // Getters e Setters
    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; } // Utile per la modifica proiezionista
    public Film getFilm() { return film; }
    public double getCostoBiglietto() { return costoBiglietto; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%s] %s - Prezzo: %.2f€", dataOra.format(formatter), film.getTitolo(), costoBiglietto);
    }
}