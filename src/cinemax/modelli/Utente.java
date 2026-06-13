package cinemax.modelli;

import java.time.LocalDate;

/**
 * Classe astratta che definisce la struttura comune a tutti gli utenti del sistema CineMax.
 * Gestisce l'anagrafica di base, le credenziali di accesso crittografate ed il ruolo
 * aziendale all'interno dell'applicazione.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public abstract class Utente {

    private String nome;
    private String cognome;
    private String username;
    private String password; // Memorizzata sotto forma di Hash SHA-256
    private LocalDate dataNascita;
    private String luogoDomicilio;
    private Ruolo ruolo;

    /**
     * Costruttore completo per inizializzare un utente del sistema.
     *
     * @param nome           Il nome dell'utente.
     * @param cognome        Il cognome dell'utente.
     * @param username       L'identificativo univoco per il login.
     * @param password       La stringa della password (già cifrata o da cifrare).
     * @param dataNascita    La data di nascita (opzionale per alcuni ruoli).
     * @param ruolo          Il ruolo assegnato nel sistema (es. CLIENTE, BIGLIETTAIO).
     * @param luogoDomicilio La città o l'indirizzo di domicilio.
     */
    public Utente(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataNascita = dataNascita;
        this.ruolo = ruolo;
        this.luogoDomicilio = luogoDomicilio;
    }

    /**
     * Restituisce il ruolo dell'utente nel sistema.
     *
     * @return Il ruolo dell'utente.
     */
    public Ruolo getRuolo() { return ruolo; }

    /**
     * Restituisce il luogo di domicilio dell'utente.
     *
     * @return La stringa del domicilio.
     */
    public String getLuogoDomicilio() { return luogoDomicilio; }

    /**
     * Restituisce la data di nascita dell'utente.
     *
     * @return La data di nascita (può essere null).
     */
    public LocalDate getDataNascita() { return dataNascita; }

    /**
     * Restituisce la password crittografata dell'utente.
     *
     * @return La password hashata.
     */
    public String getPassword() { return password; }

    /**
     * Restituisce lo username univoco dell'utente.
     *
     * @return Lo username dell'utente.
     */
    public String getUsername() { return username; }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return Il cognome.
     */
    public String getCognome() { return cognome; }

    /**
     * Restituisce il nome dell'utente.
     *
     * @return Il nome.
     */
    public String getNome() { return nome; }

    /**
     * Verifica l'uguaglianza logica tra questo utente ed un altro oggetto Utente.
     * Il criterio di uguaglianza è basato sull'univocità dello username.
     *
     * @param u L'utente da confrontare con l'istanza corrente.
     * @return true se gli username coincidono (case-insensitive), false altrimenti.
     */
    public boolean equals(Utente u) {
        if (u == null) {
            return false;
        }
        return this.username.equalsIgnoreCase(u.getUsername());
    }

    /**
     * Sovrascrittura del metodo equals ereditato da Object per supportare il polimorfismo
     * e l'utilizzo corretto all'interno delle liste o collezioni di Java.
     *
     * @param o L'oggetto da confrontare.
     * @return true se l'oggetto è un Utente con lo stesso username, false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Utente) {
            return equals((Utente) o);
        } else {
            return false;
        }
    }

    /**
     * Rigenera una vista testuale formattata a blocchi dei dettagli dell'utente,
     * ideale per la visualizzazione allineata all'interno della TUI.
     *
     * @return La stringa formattata.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n======================================\n");
        sb.append("   DETTAGLI UTENTE - ").append(ruolo).append("\n");
        sb.append("======================================\n");

        sb.append(String.format("%-15s: %s\n", "Nome", nome));
        sb.append(String.format("%-15s: %s\n", "Cognome", cognome));
        sb.append(String.format("%-15s: %s\n", "Username", username));
        sb.append(String.format("%-15s: %s\n", "Domicilio", luogoDomicilio));

        String dataStr = (dataNascita != null) ? dataNascita.toString() : "N/D";
        sb.append(String.format("%-15s: %s\n", "Data Nascita", dataStr));

        sb.append("======================================\n");

        return sb.toString();
    }
}