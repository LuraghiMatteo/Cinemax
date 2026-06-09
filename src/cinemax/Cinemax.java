package cinemax;

import cinemax.gestori.GestoreProiezioni;
import cinemax.gestori.GestoreUtenti;
import cinemax.gestori.GestorePrenotazioni;
import cinemax.menu.MenuBigliettaio;
import cinemax.menu.MenuCliente;
import cinemax.menu.MenuGuest;
import cinemax.menu.MenuProiezionista;
import cinemax.modelli.Cliente;
import cinemax.modelli.Utente;

import java.util.Scanner;

public class Cinemax {
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
                                chiudi = new MenuProiezionista(gestoreProiezioni).esegui();
                                break;
                            default:
                                System.out.println("Ruolo di sistema non riconosciuto.");
                        }
                    } else {
                        System.out.println("Errore: Credenziali non valide. Se non sei registrato, seleziona l'opzione [3].");
                    }
                    break;
                case "2":
                    chiudi = new MenuGuest(gestoreUtenti, gestoreProiezioni).esegui();
                    break;
                case "3":
                    new MenuGuest(gestoreUtenti, gestoreProiezioni).registraUtente();
                    break;
                case "X":
                    System.out.println("Uscita dal programma in corso...");
                    break;

                default:
                    System.err.println("Comando non valido. Riprova");
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