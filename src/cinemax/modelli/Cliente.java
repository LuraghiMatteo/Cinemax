package cinemax.modelli;

import java.time.LocalDate;

/**
 * Rappresenta un utente con ruolo Cliente all'interno del sistema CineMax.
 * Un cliente può registrarsi, autenticarsi, consultare il palinsesto e gestire le proprie prenotazioni.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class Cliente extends Utente {

    /**
     * Costruttore completo per creare un nuovo oggetto Cliente.
     * Istruisce la superclasse {@link Utente} impostando forzatamente il ruolo CLIENTE.
     *
     * @param nome           Il nome del cliente.
     * @param cognome        Il cognome del cliente.
     * @param username       L'identificativo univoco del cliente.
     * @param password       La password (in chiaro o hash).
     * @param dataNascita    La data di nascita.
     * @param ruolo          Il ruolo (ignorato in favore di Ruolo.CLIENTE).
     * @param luogoDomicilio La città o l'indirizzo di domicilio.
     */
    public Cliente(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, Ruolo.CLIENTE, luogoDomicilio);
    }
}
