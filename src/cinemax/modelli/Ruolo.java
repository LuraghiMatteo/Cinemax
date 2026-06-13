package cinemax.modelli;

/**
 * Rappresenta i ruoli aziendali previsti per gli utenti del sistema CineMax.
 * Definisce i privilegi e l'accesso ai diversi menu dell'applicazione.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public enum Ruolo {
    /** Ruolo per gli utenti che desiderano prenotare spettacoli. */
    CLIENTE,
    /** Ruolo per gli operatori addetti alla gestione del palinsesto delle proiezioni. */
    PROIEZIONISTA,
    /** Ruolo per gli operatori di cassa addetti alla gestione e visualizzazione delle prenotazioni. */
    BIGLIETTAIO;
}
