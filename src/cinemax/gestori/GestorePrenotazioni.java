package cinemax.gestori;

import cinemax.modelli.Genere;
import cinemax.modelli.Prenotazione;
import cinemax.modelli.Proiezione;
import cinemax.modelli.Cliente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorePrenotazioni {

    private List<Prenotazione> prenotazioni;
    private static final int CAPIENZA_SALA = 200;
    private final String FILE_PATH = "data/prenotazioni.csv";

    public GestorePrenotazioni() {
        this.prenotazioni = new ArrayList<>();
    }

    // --- METODI DI SUPPORTO ---
    public int calcolaPostiLiberi(Proiezione proiezione) {
        return 0;
    }

    private String generaCodiceUnivoco() {
        return null;
    }

    // --- METODI PER IL CLIENTE ---
    public Prenotazione creaPrenotazione(Cliente cliente, Proiezione proiezione, int numeroPosti) {
        return null;
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
    public void caricaDaFile() {}

    public void salvaSuFile() {}
}