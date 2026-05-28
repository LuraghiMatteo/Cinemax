package cinemax.menu;

import cinemax.gestori.GestorePrenotazioni;
import cinemax.gestori.GestoreProiezioni;
import cinemax.modelli.Cliente;
import cinemax.modelli.Prenotazione;
import cinemax.modelli.Proiezione;
import cinemax.eccezioni.PrenotazioneException; // Assicurati che il package sia corretto

import java.util.List;
import java.util.Scanner;

/**
 * Gestisce l'interfaccia a riga di comando (TUI) per le funzionalità dedicate al Cliente autenticato.
 * Consente l'inserimento, la visualizzazione, la modifica e la cancellazione delle proprie prenotazioni.
 * * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class MenuCliente {

    /**
     * Menu principale interattivo per il cliente loggato.
     *
     * @param cliente             Il cliente correntemente autenticato in sessione.
     * @param gestorePrenotazioni Il gestore delle prenotazioni.
     * @param gestoreProiezioni   Il gestore del palinsesto dei film.
     */
    public static void menu(Cliente cliente, GestorePrenotazioni gestorePrenotazioni, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel;

        do {
            System.out.println("\n=== AREA PERSONALE CLIENTE ===");
            System.out.println("Benvenuto, " + cliente.getNome() + " " + cliente.getCognome() + "!");
            System.out.println("[1] Inserisci una nuova prenotazione");
            System.out.println("[2] Visualizza le tue prenotazioni");
            System.out.println("[3] Modifica la data di una prenotazione");
            System.out.println("[4] Cancella una prenotazione");
            System.out.println("[X] Logout");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine().trim().toUpperCase();

            switch (sel) {
                case "1":
                    inserisciPrenotazione(cliente, gestorePrenotazioni, gestoreProiezioni);
                    break;
                case "2":
                    mostraPrenotazioni(cliente, gestorePrenotazioni);
                    break;
                case "3":
                    modificaPrenotazione(gestorePrenotazioni, gestoreProiezioni);
                    break;
                case "4":
                    cancellaPrenotazione(gestorePrenotazioni);
                    break;
                case "X":
                    System.out.println("Disconnessione effettuata con successo. Arrivederci!");
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        } while (!sel.equals("X"));
    }

    /**
     * Gestisce l'interazione da terminale per l'inserimento di una nuova prenotazione.
     * Recupera il palinsesto delle proiezioni disponibili, acquisisce l'input numerico dei posti
     * e delega l'operazione alla business logic catturando eventuali eccezioni di indisponibilità.
     *
     * @param cliente             L'oggetto Cliente in sessione che richiede la prenotazione.
     * @param gPrenotazioni       Il gestore incaricato della creazione e validazione delle prenotazioni.
     * @param gProiezioni         Il gestore incaricato del recupero delle proiezioni a palinsesto.
     */
    private static void inserisciPrenotazione(Cliente cliente, GestorePrenotazioni gPrenotazioni, GestoreProiezioni gProiezioni) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- NUOVA PRENOTAZIONE ---");

        // Recupero dell'elenco delle proiezioni attive e non scadute a sistema
        List<Proiezione> proiezioni = gProiezioni.getProiezioniDisponibili();
        if (proiezioni.isEmpty()) {
            System.out.println("Al momento non ci sono proiezioni disponibili nel cinema.");
            return;
        }

        // Stampa indicizzata del palinsesto per permettere una selezione numerica da menù
        for (int i = 0; i < proiezioni.size(); i++) {
            System.out.println("[" + i + "] " + proiezioni.get(i));
        }

        System.out.print("Seleziona il numero della proiezione desiderata: ");
        try {
            // Acquisizione controllata dell'indice della proiezione scelta
            int indice = Integer.parseInt(sc.nextLine());
            if (indice < 0 || indice >= proiezioni.size()) {
                System.out.println("Selezione non valida.");
                return;
            }

            Proiezione scelta = proiezioni.get(indice);
            System.out.print("Quanti posti desideri prenotare? ");
            int posti = Integer.parseInt(sc.nextLine());

            // Controllo di integrità locale sui limiti dell'input inserito
            if (posti <= 0) {
                System.out.println("Errore: Il numero di posti deve essere maggiore di zero.");
                return;
            }

            // Invocazione del metodo del gestorePrenotazioni. Se i posti in sala sono esauriti,
            // il metodo solleverà un'eccezione
            Prenotazione p = gPrenotazioni.creaPrenotazione(cliente, scelta, posti);
            System.out.println("\n[Successo] Prenotazione effettuata!");
            System.out.println(p); // Mostro prenotazione

        } catch (NumberFormatException e) {
            // Cattura i tentativi di inserimento di testo alfabetico dove sono richiesti numeri interi
            System.out.println("Errore: Inserisci un valore numerico valido.");
        } catch (Exception e) {
            // Intercetta PostiEsauritiException o altre eccezioni di business sollevate dal gestore
            System.out.println("Impossibile prenotare: " + e.getMessage());
        }
    }

    /**
     * Recupera e stampa a video l'elenco storico di tutte le prenotazioni effettuate
     * dal cliente attualmente loggato in sessione.
     *
     * @param cliente       L'oggetto Cliente di cui si vogliono esaminare i record.
     * @param gPrenotazioni Il gestore contenente la collezione globale delle prenotazioni in memoria RAM.
     */
    private static void mostraPrenotazioni(Cliente cliente, GestorePrenotazioni gPrenotazioni) {
        System.out.println("\n--- LE TUE PRENOTAZIONI ---");

        // Metodo gestore per filtrare le sole prenotazioni di titolarità del cliente
        List<Prenotazione> mie = gPrenotazioni.getPrenotazioniPerCliente(cliente);

        // Se la lista restituita è vuota, notifichiamo l'assenza di record all'utente
        if (mie.isEmpty()) {
            System.out.println("Non hai ancora effettuato nessuna prenotazione.");
            return;
        }

        // Stampa prenotazioni
        for (Prenotazione p : mie) {
            System.out.println(p);
            System.out.println("-----------------------------------");
        }
    }

    /**
     * Gestisce la modifica di una prenotazione esistente.
     * Acquisisce il codice identificativo univoco della prenotazione e permette di selezionare una nuova
     * proiezione alternativa, inoltrando la richiesta al gestore.
     *
     * @param gPrenotazioni Il gestore deputato all'elaborazione e aggiornamento della prenotazione.
     * @param gProiezioni   Il gestore deputato al recupero delle date alternative a palinsesto.
     */
    private static void modificaPrenotazione(GestorePrenotazioni gPrenotazioni, GestoreProiezioni gProiezioni) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- MODIFICA DATA SPETTACOLO ---");

        // Acquisizione del codice alfanumerico sequenziale di 8 caratteri (es. PR000042)
        System.out.print("Inserisci il codice univoco della prenotazione da modificare (es. PR000001): ");
        String codice = sc.nextLine().trim();

        // Verifica preliminare sulla disponibilità di spettacoli alternativi
        List<Proiezione> proiezioni = gProiezioni.getProiezioniDisponibili();
        if (proiezioni.isEmpty()) {
            System.out.println("Nessuna nuova data disponibile a sistema.");
            return;
        }

        // Visualizzazione delle possibili opzioni di cambio data/ora
        System.out.println("Seleziona la nuova data/ora per lo stesso film:");
        for (int i = 0; i < proiezioni.size(); i++) {
            System.out.println("[" + i + "] " + proiezioni.get(i));
        }
        System.out.print("Scegli la nuova proiezione: ");

        try {
            // Controllo che l'indice sia valido
            int indice = Integer.parseInt(sc.nextLine());
            if (indice < 0 || indice >= proiezioni.size()) {
                System.out.println("Selezione errata.");
                return;
            }

            Proiezione nuovaProiezione = proiezioni.get(indice);

            // Chiamo metodo del gestore (che gestisce già le eccezioni al meglio)
            gPrenotazioni.modificaPrenotazione(codice, nuovaProiezione);
            System.out.println("[Successo] Prenotazione aggiornata con successo.");

        } catch (NumberFormatException e) {
            System.out.println("Errore: Input numerico non valido.");
        } catch (PrenotazioneException e) {
            System.out.println("Modifica fallita: " + e.getMessage());
        }
    }

    /**
     * Gestisce la cancellazione definitiva di una prenotazione tramite l'interfaccia TUI.
     * Riceve il codice identificativo e interroga in modo protetto il gestore.
     *
     * @param gPrenotazioni Il gestore incaricato della rimozione del record dalla lista
     */
    private static void cancellaPrenotazione(GestorePrenotazioni gPrenotazioni) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- CANCELLAZIONE PRENOTAZIONE ---");
        System.out.print("Inserisci il codice univoco di 8 caratteri da rimuovere: ");
        String codice = sc.nextLine().trim();

        try {
            // Inoltro della richiesta di rimozione al gestore delle prenotazioni
            gPrenotazioni.eliminaPrenotazione(codice);
            System.out.println("[Successo] La prenotazione è stata cancellata correttamente.");

        } catch (PrenotazioneException e) {
            System.out.println("Cancellazione fallita: " + e.getMessage());
        }
    }
}