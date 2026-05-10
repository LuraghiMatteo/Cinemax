package cinemax.modelli;

import java.time.LocalDate;

public abstract class Utente {

    private String nome;
    private String cognome;
    private String username;
    private String password; //con hash
    private LocalDate dataNascita;
    private String luogoDomicilio;
    private Ruolo ruolo;

    public Utente(String nome, String cognome, String username, String password, LocalDate dataNascita, Ruolo ruolo, String luogoDomicilio) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataNascita = dataNascita;
        this.ruolo = ruolo;
        this.luogoDomicilio = luogoDomicilio;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public String getLuogoDomicilio() {
        return luogoDomicilio;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getCognome() {
        return cognome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n======================================\n");
        sb.append("   DETTAGLI UTENTE - ").append(ruolo).append("\n");
        sb.append("======================================\n");

        // String.format("%-15s") serve ad allineare il testo a sinistra con uno spazio di 15 caratteri
        sb.append(String.format("%-15s: %s\n", "Nome", nome));
        sb.append(String.format("%-15s: %s\n", "Cognome", cognome));
        sb.append(String.format("%-15s: %s\n", "Username", username));
        sb.append(String.format("%-15s: %s\n", "Domicilio", luogoDomicilio));

        // Gestione della data opzionale
        String dataStr = (dataNascita != null) ? dataNascita.toString() : "N/D";
        sb.append(String.format("%-15s: %s\n", "Data Nascita", dataStr));

        sb.append("======================================\n");

        return sb.toString();
    }
}
