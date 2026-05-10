package cinemax.modelli;

import java.time.LocalDate;

public class Proiezionista extends Utente {

    public Proiezionista(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, Ruolo.PROIEZIONISTA, luogoDomicilio);
    }
}
