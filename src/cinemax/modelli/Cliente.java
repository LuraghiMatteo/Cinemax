package cinemax.modelli;

import java.time.LocalDate;

public class Cliente extends Utente{

    public Cliente(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, Ruolo.CLIENTE, luogoDomicilio);
    }
}
