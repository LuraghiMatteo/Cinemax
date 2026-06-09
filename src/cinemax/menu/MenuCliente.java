package cinemax.menu;

import cinemax.gestori.GestorePrenotazioni;
import cinemax.gestori.GestoreProiezioni;
import cinemax.modelli.Cliente;
import cinemax.modelli.Prenotazione;
import cinemax.modelli.Proiezione;
import cinemax.eccezioni.PrenotazioneException;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Gestisce l'interfaccia utente testuale (TUI) per le funzionalità dedicate al Cliente autenticato.
 * Classe convertita in programmazione a oggetti (OOP): incapsula i gestori di business logic
 * e il profilo del cliente in sessione come attributi d'istanza.
 * Consente l'inserimento, la visualizzazione, la modifica e la cancellazione delle proprie prenotazioni.
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class MenuCliente {

    // Dipendenze d'istanza (Dependency Injection tramite costruttore)
    private final GestorePrenotazioni gestorePrenotazioni;
    private final GestoreProiezioni gestoreProiezioni;
    private final Cliente cliente;

    /**
     * Costruttore completo per istanziare il modulo del menù per un cliente specifico.
     * Inietta le dipendenze dei manager di sistema necessarie al funzionamento della TUI.
     *
     * @param gestoreProiezioni   Il gestore del palinsesto dei film e delle sale proiezioni.
     * @param gestorePrenotazioni Il gestore globale per la manipolazione dei record delle prenotazioni.
     * @param cliente             L'oggetto Cliente che ha effettuato l'autenticazione nel sistema.
     */
    public MenuCliente(GestoreProiezioni gestoreProiezioni, GestorePrenotazioni gestorePrenotazioni, Cliente cliente) {
        this.gestoreProiezioni = gestoreProiezioni;
        this.gestorePrenotazioni = gestorePrenotazioni;
        this.cliente = cliente;
    }

    /**
     * Avvia il ciclo interattivo del menù principale per l'area personale del cliente.
     * Mostra le opzioni disponibili e smista i comandi inseriti dall'utente fino alla richiesta di disconnessione.
     *
     * @return true ad esecuzione terminata, segnalando l'avvenuto logout al pannello principale.
     */
    public boolean esegui() {
        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;

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
                    inserisciPrenotazione();
                    break;
                case "2":
                    mostraPrenotazioni();
                    break;
                case "3":
                    modificaPrenotazione();
                    break;
                case "4":
                    cancellaPrenotazione();
                    break;
                case "X":
                    chiudi = true;
                    System.out.println("Disconnessione effettuata con successo. Arrivederci!");
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        } while (!chiudi);

        return true;
    }

    /**
     * Gestisce il flusso interattivo a terminale per l'inserimento guidato di una nuova prenotazione.
     * Sfrutta il metodo multi-filtro del gestore proiezioni impostando un intervallo temporale dinamico di un anno
     * a partire dalla data odierna per mostrare il palinsesto futuro.
     */
    private void inserisciPrenotazione() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- NUOVA PRENOTAZIONE ---");

        LocalDate oggi = LocalDate.now();
        LocalDate fine = oggi.plusMonths(1);

        // Interrogazione del gestore per ottenere solo le proiezioni future
        List<Proiezione> proiezioni = gestoreProiezioni.cercaProiezione(null, null, oggi, fine, -1, -1);
        if (proiezioni.isEmpty()) {
            System.out.println("Al momento non ci sono proiezioni disponibili nel cinema.");
            return;
        }

        // Sfrutta il metodo di utilità per stampare e impaginare a blocchi le proiezioni disponibili
        stampaListaProiezioni(proiezioni);

        System.out.print("Seleziona il numero della proiezione desiderata: ");
        try {
            // Controllo preventivo dell'indice immesso
            int indice = Integer.parseInt(sc.nextLine());
            if (indice < 0 || indice >= proiezioni.size()) {
                System.out.println("Selezione non valida.");
                return;
            }

            Proiezione scelta = proiezioni.get(indice);
            System.out.print("Quanti posti desideri prenotare? ");
            int posti = Integer.parseInt(sc.nextLine());

            if (posti <= 0) {
                System.out.println("Errore: Il numero di posti deve essere maggiore di zero.");
                return;
            }

            // Inoltro richiesta alla business logic passandoci il riferimento 'this.cliente'
            Prenotazione p = gestorePrenotazioni.creaPrenotazione(this.cliente, scelta, posti);
            System.out.println("\n[Successo] Prenotazione effettuata!");
            System.out.println(p);

        } catch (NumberFormatException e) {
            System.out.println("Errore: Inserisci un valore numerico valido.");
        } catch (Exception e) {
            System.out.println("Impossibile prenotare: " + e.getMessage());
        }
    }

    /**
     * Interroga il gestore delle prenotazioni per filtrare ed esporre a schermo tutte le ricevute
     * di titolarità del cliente correntemente loggato nel modulo di sessione.
     */
    private void mostraPrenotazioni() {
        System.out.println("\n--- LE TUE PRENOTAZIONI ---");

        // Richiesta di estrazione dei record filtrando per l'attributo d'istanza 'this.cliente'
        List<Prenotazione> mie = gestorePrenotazioni.getPrenotazioniPerCliente(this.cliente);

        if (mie.isEmpty()) {
            System.out.println("Non hai ancora effettuato nessuna prenotazione.");
            return;
        }

        // Sfrutta il metodo di utilità generico per stampare ed impaginare le prenotazioni del cliente
        stampaListaPrenotazioni(mie);
    }

    /**
     * Esegue la procedura guidata per lo slittamento temporale di una prenotazione attiva.
     * Identifica il record originario tramite codice alfanumerico univoco, ne isola il titolo del film
     * e permette il re-indirizzamento guidato esclusivamente verso proiezioni future dello stesso identico titolo.
     */
    private void modificaPrenotazione() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- MODIFICA DATA SPETTACOLO ---");

        System.out.print("Inserisci il codice univoco della prenotazione da modificare (es. PR000001): ");
        String codice = sc.nextLine().trim();

        // Controllo di esistenza del codice inserito
        Prenotazione vecchiaPrenotazione = gestorePrenotazioni.cercaPrenotazionePerCodice(codice);
        if (vecchiaPrenotazione == null) {
            System.out.println("Errore: Nessuna prenotazione trouvata con il codice " + codice);
            return;
        }

        // Estrazione selettiva del titolo del film per vincolare la ricerca successiva
        String titoloFilm = vecchiaPrenotazione.getProiezione().getFilm().getTitolo();

        // Generazione elenco date alternative filtrando per il titolo del film
        LocalDate oggi = LocalDate.now();
        LocalDate fine = oggi.plusMonths(1);
        List<Proiezione> proiezioni = gestoreProiezioni.cercaProiezione(titoloFilm, null, oggi, fine, -1, -1);

        if (proiezioni.isEmpty()) {
            System.out.println("Al momento non ci sono altre date future disponibili per il film: " + titoloFilm);
            return;
        }

        // Sfrutta il metodo di utilità riutilizzabile per stampare le opzioni di cambio data disponibili
        System.out.println("Seleziona la nuova data/ora per il film '" + titoloFilm + "':");
        stampaListaProiezioni(proiezioni);

        System.out.print("Scegli la nuova proiezione: ");

        try {
            int indice = Integer.parseInt(sc.nextLine());
            if (indice < 0 || indice >= proiezioni.size()) {
                System.out.println("Selezione errata.");
                return;
            }

            Proiezione nuovaProiezione = proiezioni.get(indice);

            // Richiesta di allineamento nello stato del gestore
            gestorePrenotazioni.modificaPrenotazione(codice, nuovaProiezione);
            System.out.println("[Successo] Prenotazione aggiornata con successo.");

        } catch (NumberFormatException e) {
            System.out.println("Errore: Input numerico non valido.");
        } catch (PrenotazioneException e) {
            System.out.println("Modifica fallita: " + e.getMessage());
        }
    }

    /**
     * Provvede all'intercettazione del codice identificativo immesso da tastiera e inoltra la richiesta
     * di cancellazione definitiva del biglietto al rispettivo gestore delle prenotazioni.
     */
    private void cancellaPrenotazione() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- CANCELLAZIONE PRENOTAZIONE ---");
        System.out.print("Inserisci il codice univoco di 8 caratteri da rimuovere: ");
        String codice = sc.nextLine().trim();

        try {
            gestorePrenotazioni.eliminaPrenotazione(codice);
            System.out.println("[Successo] La prenotazione è stata cancellata correttamente.");

        } catch (PrenotazioneException e) {
            System.out.println("Cancellazione fallita: " + e.getMessage());
        }
    }

    /**
     * Metodo di utilità specifico per l'impaginazione controllata delle PRENOTAZIONI.
     * Richiama internamente il visualizzatore statico del GestorePrenotazioni.
     *
     * @param risultati La lista di prenotazioni da mostrare a video a blocchi da 25.
     */
    private void stampaListaPrenotazioni(List<Prenotazione> risultati) {
        if (risultati.isEmpty()) {
            System.out.println("Nessun elemento da mostrare.");
            return;
        }
        System.out.println("\nPrenotazioni rintracciate (" + risultati.size() + "):");

        Scanner sc = new Scanner(System.in);
        int index = 0;
        do {
            // Chiamata al visualizzatore del gestore prenotazioni
            GestorePrenotazioni.visualizzaPrenotazioni(risultati, index);
            index += 25;

            if (index < risultati.size()) {
                System.out.print("Record da " + index + " su " + risultati.size() + ". ");
                System.out.print("Premere INVIO per continuare la lettura...");
                sc.nextLine();
            }
        } while (index < risultati.size());
    }

    /**
     * Metodo di utilità specifico per l'impaginazione controllata delle PROIEZIONI.
     * Risolve il problema del menù inserimento stampando l'indice corretto affiancato all'oggetto Proiezione,
     * garantendo l'impaginazione a blocchi da 25 senza saturare la console.
     *
     * @param risultati La lista di proiezioni da mostrare a video a blocchi da 25.
     */
    private void stampaListaProiezioni(List<Proiezione> risultati) {
        if (risultati.isEmpty()) {
            System.out.println("Nessun elemento da mostrare.");
            return;
        }
        System.out.println("\nProiezioni disponibili (" + risultati.size() + "):");

        Scanner sc = new Scanner(System.in);
        int index = 0;
        do {
            // Calcoliamo la fine del blocco attuale (massimo 25 elementi alla volta o la fine della lista)
            int fineBlocco = Math.min(index + 25, risultati.size());

            // Ciclo locale per stampare il blocco corrente mantenendo intatto l'indice [i] per la selezione
            for (int i = index; i < fineBlocco; i++) {
                System.out.println("[" + i + "] " + risultati.get(i));
            }

            index += 25;

            if (index < risultati.size()) {
                System.out.print("Proiezioni " + index + " su " + risultati.size() + ". ");
                System.out.print("Premere INVIO per continuare la lettura...");
                sc.nextLine();
            }
        } while (index < risultati.size());
    }
}