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
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        String sel;

        // Inizializzazione centralizzata e caricamento database su disco
        GestoreUtenti gestoreUtenti = new GestoreUtenti();
        gestoreUtenti.caricaDaFile();

        GestoreProiezioni gestoreProiezioni = new GestoreProiezioni();
        gestoreProiezioni.caricaDaFile(); // Rimuovi il commento quando avrai il metodo pronto

        GestorePrenotazioni gestorePrenotazioni = new GestorePrenotazioni();
        gestorePrenotazioni.caricaDaFile(gestoreUtenti, gestoreProiezioni);
        boolean chiudi = false;

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
                                MenuCliente.menu((Cliente) utente, gestorePrenotazioni, gestoreProiezioni);
                                break;
                            case BIGLIETTAIO:
                                MenuBigliettaio.menu(gestorePrenotazioni);
                                break;
                            case PROIEZIONISTA:
                                MenuProiezionista.menu();
                                break;
                            default:
                                System.out.println("Ruolo di sistema non riconosciuto.");
                        }
                    } else {
                        System.out.println("Errore: Credenziali non valide. Se non sei registrato, seleziona l'opzione [3].");
                    }
                    break;
                case "2":
                    chiudi = MenuGuest.menu(gestoreUtenti, gestoreProiezioni);
                    break;
                case "3":
                    MenuGuest.registraGuest(gestoreUtenti);
                    MenuCliente.menu();
                    break;
                case "X":
                    // Il salvataggio dei file avviene esclusivamente qui all'atto di chiusura
                    // massimizzando le prestazioni del software.
                    System.out.println("\nSalvataggio dei database in corso...");
                    gestoreUtenti.salvaSuFile();
                    gestorePrenotazioni.salvaSuFile();
                    // gestoreProiezioni.salvaSuFile();
                    System.out.println("Tutti i dati sono al sicuro. Arrivederci!");
                    break;

                default:
                    System.err.println("Comando non valido. Riprova");
            }

        }while (!sel.equals("X") && !chiudi);

        sc.close();
    }
}
