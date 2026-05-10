package cinemax.gestori;

import cinemax.modelli.Utente;
import cinemax.modelli.Cliente;

import java.util.ArrayList;
import java.util.List;

public class GestoreUtenti {

    private List<Utente> utentiRegistrati;
    private final String FILE_PATH = "data/utenti.csv";

    public GestoreUtenti() {
        this.utentiRegistrati = new ArrayList<>();
    }

    // --- METODI DI ACCESSO ---

    // Ritorna l'Utente (che può essere Cliente, Proiezionista, Bigliettaio) o null se fallisce
    public Utente login(String username, String passwordChiaro) {
        return null;
    }

    public void registraCliente(Cliente nuovoCliente) {}

    // --- METODI DI SUPPORTO SICUREZZA ---

    // Metodo che cifra semplicemente la password
    private String cifraPassword(String passwordInChiaro) {
        return null;
    }

    // --- GESTIONE FILE ---
    public void caricaDaFile() {}

    public void salvaSuFile() {}
}