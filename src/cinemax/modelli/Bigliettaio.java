package cinemax.modelli;

import java.time.LocalDate;

/**
 * Rappresenta un utente con ruolo Bigliettaio all'interno del sistema CineMax.
 * Il bigliettaio gestisce le prenotazioni dei clienti ed ha accesso a visualizzazioni e ricerche avanzate.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class Bigliettaio extends Utente {

    /**
     * Costruttore completo per creare un nuovo oggetto Bigliettaio.
     * Istruisce la superclasse {@link Utente} impostando forzatamente il ruolo BIGLIETTAIO.
     *
     * @param nome           Il nome del bigliettaio.
     * @param cognome        Il cognome del bigliettaio.
     * @param username       L'identificativo univoco del bigliettaio.
     * @param password       La password (in chiaro o hash).
     * @param dataNascita    La data di nascita.
     * @param ruolo          Il ruolo (ignorato in favore di Ruolo.BIGLIETTAIO).
     * @param luogoDomicilio La città o l'indirizzo di domicilio.
     */
    public Bigliettaio(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, Ruolo.BIGLIETTAIO, luogoDomicilio);
    }
}
