package cinemax.modelli;

import java.time.LocalDate;

/**
 * Rappresenta un utente con ruolo Proiezionista all'interno del sistema CineMax.
 * Il proiezionista si occupa della programmazione e gestione del palinsesto (film e proiezioni).
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 * */
public class Proiezionista extends Utente {

    /**
     * Costruttore completo per creare un nuovo oggetto Proiezionista.
     * Istruisce la superclasse {@link Utente} impostando forzatamente il ruolo PROIEZIONISTA.
     *
     * @param nome           Il nome del proiezionista.
     * @param cognome        Il cognome del proiezionista.
     * @param username       L'identificativo univoco del proiezionista.
     * @param password       La password (in chiaro o hash).
     * @param dataNascita    La data di nascita.
     * @param ruolo          Il ruolo (ignorato in favore di Ruolo.PROIEZIONISTA).
     * @param luogoDomicilio La città o l'indirizzo di domicilio.
     */
    public Proiezionista(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, Ruolo.PROIEZIONISTA, luogoDomicilio);
    }
}
