package cinemax;

import cinemax.gestori.GestoreProiezioni;
import cinemax.gestori.GestoreUtenti;
import cinemax.gestori.GestorePrenotazioni;
import cinemax.menu.MenuBigliettaio;
import cinemax.menu.MenuCliente;
import cinemax.menu.MenuGuest;
import cinemax.menu.MenuProiezionista;
import cinemax.modelli.Cliente;
import cinemax.modelli.Proiezionista;
import cinemax.modelli.Utente;

import java.util.Scanner;

/**
 * Classe principale di ingresso (Entry Point) per l'applicazione CineMax.
 * Inizializza i gestori di business logic caricando i dati dai rispettivi file di persistenza (CSV),
 * e avvia il ciclo interattivo del menu di front-end del cinema.
 * Al momento della chiusura, garantisce il salvataggio persistente dello stato di tutti i database.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class Cinemax {
    /**
     * Il punto di ingresso principale dell'applicazione.
     * Gestisce la navigazione e lo smistamento dei comandi di login, registrazione
     * e accesso alle aree dedicate (Cliente, Bigliettaio, Proiezionista).
     *
     * @param args Gli argomenti della riga di comando (non utilizzati).
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;

        GestoreUtenti gestoreUtenti = new GestoreUtenti();
        gestoreUtenti.caricaDaFile();

        GestoreProiezioni gestoreProiezioni = new GestoreProiezioni();
        gestoreProiezioni.caricaDaFile();

        GestorePrenotazioni gestorePrenotazioni = new GestorePrenotazioni();
        gestorePrenotazioni.caricaDaFile(gestoreUtenti, gestoreProiezioni);

        do {
            System.out.println("\n=== BENVENUTO IN CINEMAX !! ===");
            System.out.println("[1] Login (Accedi al sistema)");
            System.out.println("[2] Menu Guest (Esplora senza registrarti)");
            System.out.println("[3] Registrati come nuovo cliente");
            System.out.println("[X] Esci dal programma");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine().trim().toUpperCase(); // Usiamo nextLine() coerentemente per evitare problemi di buffer

            switch (sel) {
                case "1":
                    System.out.print("Inserisci lo username: ");
                    String user = sc.nextLine().trim();
                    System.out.print("Inserisci la password: ");
                    String pass = sc.nextLine().trim();

                    // Richiesta di autenticazione logica al gestore
                    Utente utente = gestoreUtenti.login(user, pass);

                    if (utente != null) {
                        switch (utente.getRuolo()) {
                            case CLIENTE:
                                chiudi = new MenuCliente(gestoreProiezioni, gestorePrenotazioni, (Cliente) utente).esegui();
                                break;
                            case BIGLIETTAIO:
                                chiudi = new MenuBigliettaio(gestorePrenotazioni).esegui();
                                break;
                            case PROIEZIONISTA:
                                chiudi = new MenuProiezionista(gestoreProiezioni, (Proiezionista) utente).esegui();
                                break;
                            default:
                                System.out.println("\u001B[31m[ERRORE] Ruolo di sistema non riconosciuto.\u001B[0m");
                        }
                    } else {
                        System.out.println("\u001B[31m[ERRORE] Credenziali non valide. Se non sei registrato, seleziona l'opzione [3].\u001B[0m");
                    }
                    break;
                case "2":
                    chiudi = new MenuGuest(gestoreUtenti, gestoreProiezioni, gestorePrenotazioni).esegui();
                    break;
                case "3":
                    new MenuGuest(gestoreUtenti, gestoreProiezioni, gestorePrenotazioni).registraUtente();
                    break;
                case "X":
                    System.out.println("Uscita dal programma in corso...");
                    break;

                default:
                    System.out.println("\u001B[31m[ERRORE] Comando non valido. Riprova.\u001B[0m");
            }

        } while (!sel.equals("X") && !chiudi);

        // Il salvataggio dei file avviene esclusivamente qui all'atto di chiusura
        // massimizzando le prestazioni del software in RAM.
        //FIXME salvare solo se sono state fatte modifiche
        System.out.println("\nSalvataggio dei database in corso...");
        gestoreUtenti.salvaSuFile();
        gestorePrenotazioni.salvaSuFile();
        gestoreProiezioni.salvaSuFile();
        System.out.println("Tutti i dati sono al sicuro. Arrivederci!");

        sc.close();
    }
}