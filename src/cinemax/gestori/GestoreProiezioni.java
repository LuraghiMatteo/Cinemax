package cinemax.gestori;

import cinemax.modelli.Proiezione;
import cinemax.modelli.Genere; // Assumendo che esista l'enum

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestoreProiezioni {

    private List<Proiezione> palinsesto;
    private final String FILE_PATH = "data/proiezioni.csv";

    public GestoreProiezioni() {
        this.palinsesto = new ArrayList<>();
    }

    // --- METODI PER IL PROIEZIONISTA ---
    public void aggiungiProiezione(Proiezione proiezione) {}

    public void modificaProiezione(Proiezione proiezioneVecchia, Proiezione proiezioneNuova) {}

    public void eliminaProiezione(Proiezione proiezione) {}

    // --- METODI DI RICERCA (Guest, Cliente, Bigliettaio) ---

    // Ricerca combinata. //riguardare slide per le specifiche della ricerca combinata
    // Metodi separati e metodo unico
    public Proiezione cercaProiezione(String titolo) {
        return null;
    }

    public Proiezione cercaProiezione(Genere genere) {
        return null;
    }

    public Proiezione cercaProiezione(LocalDate dataInizio, LocalDate dataFine) { //ci sono tante varianti
        return null;
    }

    public Proiezione cercaProiezione(double costoBiglietto) { //ci sono tante varianti
        return null;
    }

    public List<Proiezione> cercaProiezioni(String titolo, Genere genere, LocalDate dataInizio, LocalDate dataFine, Double prezzoMin, Double prezzoMax) {
        return null;
    }

    //visualizzazione Proiezione con toString

    // --- GESTIONE FILE ---
    public void caricaDaFile() {}

    public void salvaSuFile() {}
}