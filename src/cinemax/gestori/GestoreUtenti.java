package cinemax.gestori;

import cinemax.eccezioni.UtenteUsernameException;
import cinemax.modelli.*;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe incaricata di gestire il ciclo di vita degli utenti (autenticazione,
 * registrazione e persistenza su file CSV).
 * * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class GestoreUtenti {

    private List<Utente> utentiRegistrati;
    private final String FILE_PATH = "data/utenti.csv";

    /**
     * Inizializza il gestore istanziando la lista vuota in memoria RAM.
     */
    public GestoreUtenti() {
        this.utentiRegistrati = new ArrayList<>();
    }

    // METODI DI ACCESSO E LOGICA DI BUSINESS

    /**
     * Esegue l'autenticazione di un utente confrontando le credenziali inserite.
     * La password inserita in chiaro viene cifrata confrontata con l'hash salvato.
     *
     * @param username       L'identificativo inserito dall'utente.
     * @param passwordChiaro La password testuale digitata a terminale.
     * @return L'oggetto Utente autenticato (Cliente, Bigliettaio, ecc.) oppure null se fallisce.
     */
    public Utente login(String username, String passwordChiaro) {
        String passwordCifrata = cifraPassword(passwordChiaro);

        if (passwordCifrata == null) return null;

        for (Utente u : utentiRegistrati) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(passwordCifrata)) {
                return u; // Trovato e autenticato
            }
        }
        return null; // Credenziali errate
    }

    /**
     * Registra un nuovo cliente nel sistema previa verifica di univocità dello username.
     * Prima di salvare l'oggetto, la password del cliente viene cifrata in sicurezza.
     *
     * @param nuovoCliente L'oggetto Cliente compilato dalla TUI.
     * @throws Exception Se lo username scelto è già occupato da un altro utente.
     */
    public void registraCliente(Cliente nuovoCliente) throws Exception {
        // Controllo se esiste già un utente con lo stesso username
        if (cercaUtentePerUsername(nuovoCliente.getUsername()) != null) {
            throw new UtenteUsernameException("Errore: Lo username '" + nuovoCliente.getUsername() + "' è già registrato nel sistema.");
        }

        // Cifratura della password prima del salvataggio definitivo
        String passCifrata = cifraPassword(nuovoCliente.getPassword());

        // Creo una copia corretta del cliente con la password cifrata
        Cliente clienteSicuro = new Cliente(
                nuovoCliente.getNome(),
                nuovoCliente.getCognome(),
                nuovoCliente.getUsername(),
                passCifrata,
                nuovoCliente.getDataNascita(),
                nuovoCliente.getRuolo(),
                nuovoCliente.getLuogoDomicilio()
        );

        utentiRegistrati.add(clienteSicuro);
    }

    /**
     * Ricerca un utente registrato partendo dal suo username identificativo.
     * Questo metodo è vitale per la ricostruzione dei legami nel GestorePrenotazioni.
     *
     * @param username Lo username da ricercare.
     * @return L'oggetto Utente corrispondente, oppure null se inesistente.
     */
    public Utente cercaUtentePerUsername(String username) {
        for (Utente u : utentiRegistrati) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    // METODI DI SUPPORTO SICUREZZA

    /**
     * Cifra una stringa in chiaro utilizzando l'algoritmo di hashing standard SHA-256.
     *
     * @param passwordInChiaro Il testo lineare della password inserito dall'utente.
     * @return La stringa esadecimale a 64 caratteri dell'hash calcolato,
     * oppure null in caso di fallimento del sistema crittografico.
     */
    private String cifraPassword(String passwordInChiaro) {
        try {
            // Inizializzo richiedendo l'algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Converto la stringa in un array di byte e calcolo l'hash
            // L'array 'hashBytes' conterrà una sequenza di 32 byte binari "grezzi".
            byte[] hashBytes = md.digest(passwordInChiaro.getBytes());

            StringBuilder sb = new StringBuilder();

            // Itero su ciascuno dei 32 byte estratti
            for (byte b : hashBytes) {

                // Formatto la stringa in esadecimale
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Gestione dell'eccezione controllata: scatta solo nell'improbabile caso in cui
            // l'ambiente Java di esecuzione non supporti l'algoritmo SHA-256.
            System.out.println("Errore interno di sicurezza: Algoritmo di cifratura non trovato.");
            return null;
        }
    }

    // GESTIONE FILE

    /**
     * Carica gli utenti memorizzati nel file utenti.csv ripristinando la lista in RAM.
     * Sfrutta il polimorfismo per istanziare la corretta sottoclasse (Cliente, Bigliettaio,
     * Proiezionista) in base al ruolo letto dal file di persistenza.
     * Crea automaticamente il file e le cartelle se non presenti all'avvio del software.
     */
    public void caricaDaFile() {
        File f = new File(FILE_PATH);
        this.utentiRegistrati = new ArrayList<>();

        // Controllo e creazione della struttura delle cartelle e del file se assenti
        if (!f.exists()) {
            try {
                File cartellaPadre = f.getParentFile();
                if (cartellaPadre != null && !cartellaPadre.exists()) {
                    cartellaPadre.mkdirs();
                }
                f.createNewFile();

                // Se il file nasce vuoto, creiamo un Bigliettaio di default in modo da consentire il primo login per gestire il sistema.
                // Altrimenti l'applicazione partirebbe senza utenti utilizzabili.
                String passCifrataAdmin = cifraPassword("admin123");
                Bigliettaio adminDefault = new Bigliettaio("Admin", "Cinema", "admin", passCifrataAdmin, null, Ruolo.BIGLIETTAIO, "Sede");
                this.utentiRegistrati.add(adminDefault);

                // Salviamo subito il primo utente di backup
                salvaSuFile();
                return;
            } catch (IOException e) {
                System.out.println("Errore critico nella creazione del database utenti: " + e.getMessage());
                return;
            }
        }

        try {
            FileReader r = new FileReader(f);
            BufferedReader fIn = new BufferedReader(r);

            String line = fIn.readLine();

            while (line != null) {

                String[] campi = line.split(",");

                // Verifico la presenza di tutti e 7 i campi previsti
                if (campi.length == 7) {
                    String nome = campi[0];
                    String cognome = campi[1];
                    String username = campi[2];
                    String passwordHash = campi[3];
                    LocalDate dataNascita = campi[4].equals("N/D") ? null : LocalDate.parse(campi[4]);
                    Ruolo ruolo = Ruolo.valueOf(campi[5]);
                    String domicilio = campi[6];

                    // Istanziamo l'oggetto corretto
                    if (ruolo == Ruolo.CLIENTE) {
                        Cliente c = new Cliente(nome, cognome, username, passwordHash, dataNascita, ruolo, domicilio);
                        utentiRegistrati.add(c);
                    } else if (ruolo == Ruolo.BIGLIETTAIO) {
                        Bigliettaio b = new Bigliettaio(nome, cognome, username, passwordHash, dataNascita, ruolo, domicilio);
                        utentiRegistrati.add(b);
                    } else if (ruolo == Ruolo.PROIEZIONISTA) {
                        Proiezionista p = new Proiezionista(nome, cognome, username, passwordHash, dataNascita, ruolo, domicilio);
                        utentiRegistrati.add(p);
                    }
                }
                line = fIn.readLine();
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Errore durante la lettura o il parsing del file utenti: " + e.getMessage());
        }
    }

    /**
     * Scrive l'intero parco utenti contenuti nella lista RAM all'interno del file CSV,
     * sovrascrivendolo per aggiornare lo stato di persistenza.
     */
    public void salvaSuFile() {
        try {
            File f = new File(FILE_PATH);
            FileWriter w = new FileWriter(f, false);
            PrintWriter fOut = new PrintWriter(w);

            for (Utente u : utentiRegistrati) {
                // Gestione del valore nullo della data di nascita per evitare crash
                String dataStr = (u.getDataNascita() != null) ? u.getDataNascita().toString() : "N/D";

                // Formato CSV
                fOut.println(String.format("%s,%s,%s,%s,%s,%s,%s",
                        u.getNome(),
                        u.getCognome(),
                        u.getUsername(),
                        u.getPassword(),
                        dataStr,
                        u.getRuolo().name(),
                        u.getLuogoDomicilio()
                ));
            }
        } catch (IOException e) {
            System.out.println("Errore durante il salvataggio del file utenti: " + e.getMessage());
        }
    }
}