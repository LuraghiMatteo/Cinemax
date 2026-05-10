package cinemax.modelli;

import java.time.LocalDate;

public class Bigliettaio extends Utente{

    public Bigliettaio(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, Ruolo.BIGLIETTAIO, luogoDomicilio);
    }
}
