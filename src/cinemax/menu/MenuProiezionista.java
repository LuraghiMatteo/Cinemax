package cinemax.menu;

import cinemax.gestori.GestoreProiezioni;
import cinemax.modelli.Film;
import cinemax.modelli.Genere;
import cinemax.modelli.Proiezione;
import cinemax.modelli.Proiezionista;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Gestisce l'interfaccia utente testuale (TUI) per il Proiezionista.
 * Permette la gestione completa del palinsesto tramite l'inserimento di nuovi film
 * con le relative proiezioni, la modifica dell'orario di proiezioni esistenti
 * e la cancellazione di proiezioni programmate.
 *
 * Tutte le interazioni con l'utente sono validate tramite cicli di controllo per
 * garantire l'integrità dei dati inseriti.
 *
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class MenuProiezionista {

    private final GestoreProiezioni gestoreProiezioni;
    private final Proiezionista proiezionista;

    /**
     * Costruttore completo della classe MenuProiezionista.
     * Inietta la dipendenza del gestore proiezioni e il profilo del proiezionista.
     *
     * @param gestoreProiezioni Il gestore globale delle proiezioni di sistema.
     * @param proiezionista     L'utente con ruolo Proiezionista autenticato.
     */
    public MenuProiezionista(GestoreProiezioni gestoreProiezioni, Proiezionista proiezionista) {
        this.gestoreProiezioni = gestoreProiezioni;
        this.proiezionista = proiezionista;
    }

    /**
     * Visualizza il menu principale per l'utente Proiezionista e gestisce la navigazione interattiva
     * tra le varie operazioni disponibili (inserimento, modifica ed eliminazione).
     * Rimane attivo in un ciclo finché l'utente non seleziona l'opzione di logout (X).
     *
     * @return true al termine dell'esecuzione del menu (logout effettuato)
     */
    public boolean esegui() {
        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;
        
        do {
            System.out.println("\n=========================================");
            System.out.println("            AREA PROIEZIONISTA           ");
            System.out.println("=========================================");
            System.out.println("Benvenuto, " + proiezionista.getNome() + " " + proiezionista.getCognome() + "!");
            System.out.println("[1] Inserire un film e le sue proiezioni");
            System.out.println("[2] Modificare la data di una proiezione");
            System.out.println("[3] Eliminare una proiezione");
            System.out.println("[X] Esci (Logout)");
            System.out.println("=========================================");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine();
 
            switch (sel.toUpperCase()) {
                case "1":
                    inserisciFilmEProiezioni();
                    break;
                case "2":
                    modificaProiezione();
                    break;
                case "3":
                    eliminaProiezione();
                    break;
                case "X":
                    chiudi = true;
                    System.out.println("\n[OK] Chiusura sessione proiezionista effettuata. Arrivederci!");
                    break;
                default:
                    System.out.println("\n\u001B[31m[ERRORE] Opzione non valida. Riprova.\u001B[0m");
            }
        } while (!chiudi);

        return true;
    }

    /**
     * Gestisce l'inserimento guidato passo-passo di un nuovo film e della prima proiezione associata.
     * Richiede l'input di:
     * - Titolo (non vuoto)
     * - Genere (selezionato tra i valori dell'enum Genere)
     * - Regista (non vuoto)
     * - Anno di uscita (compreso tra il 1800 e il 2100)
     * - Durata in minuti (maggiore di 0)
     * - Età minima per la visione (maggiore o uguale a 0)
     * Successivamente richiede la data e l'ora per la proiezione iniziale e il relativo costo del biglietto,
     * effettuando la validazione per ciascun campo.
     */
    private void inserisciFilmEProiezioni() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----------------------------------------");
        System.out.println("         INSERIMENTO NUOVO FILM          ");
        System.out.println("-----------------------------------------");
        
        String titolo;
        do {
            System.out.print("Titolo: ");
            titolo = sc.nextLine();
            if (titolo.isBlank()) {
                System.out.println("\u001B[31m[ERRORE] Il titolo non può essere vuoto o composto solo da spazi. Riprova.\u001B[0m");
            }
        } while (titolo.isBlank());

        Genere genere = null;
        do {
            System.out.print("Genere (");
            for (Genere g : Genere.values()) {
                System.out.print(g + " ");
            }
            System.out.print("): ");
            String genereStr = sc.nextLine().trim();
            try {
                genere = Genere.valueOf(genereStr.toUpperCase().replace('-', '_'));
            } catch (IllegalArgumentException e) {
                System.out.println("\u001B[31m[ERRORE] Genere non valido. Riprova.\u001B[0m");
            }
        } while (genere == null);

        String regista;
        do {
            System.out.print("Regista: ");
            regista = sc.nextLine();
            if (regista.isBlank()) {
                System.out.println("\u001B[31m[ERRORE] Il nome del regista non può essere vuoto. Riprova.\u001B[0m");
            }
        } while (regista.isBlank());

        int anno = -1;
        do {
            System.out.print("Anno: ");
            try {
                anno = Integer.parseInt(sc.nextLine().trim());
                if (anno < 1800 || anno > 2100) {
                    System.out.println("\u001B[31m[ERRORE] Anno non realistico. Riprova.\u001B[0m");
                    anno = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31m[ERRORE] Formato anno non valido. Inserisci un numero intero.\u001B[0m");
            }
        } while (anno == -1);

        int durata = -1;
        do {
            System.out.print("Durata (in minuti): ");
            try {
                durata = Integer.parseInt(sc.nextLine().trim());
                if (durata <= 0) {
                    System.out.println("\u001B[31m[ERRORE] La durata deve essere maggiore di 0. Riprova.\u001B[0m");
                    durata = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31m[ERRORE] Formato durata non valido. Inserisci un numero intero.\u001B[0m");
            }
        } while (durata == -1);

        int etaMinima = -1;
        do {
            System.out.print("Età minima: ");
            try {
                etaMinima = Integer.parseInt(sc.nextLine().trim());
                if (etaMinima < 0) {
                    System.out.println("\u001B[31m[ERRORE] L'età minima non può essere negativa. Riprova.\u001B[0m");
                    etaMinima = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31m[ERRORE] Formato età non valido. Inserisci un numero intero.\u001B[0m");
            }
        } while (etaMinima == -1);

        Film film = new Film(titolo, genere, regista, anno, durata, etaMinima);

        System.out.println("\n-----------------------------------------");
        System.out.println("          INSERIMENTO PROIEZIONE         ");
        System.out.println("-----------------------------------------");
        System.out.println("[INFO] Associazione proiezione per il film: " + titolo);
        
        String dataStr = "";
        boolean dataValida = false;
        do {
            System.out.print("Data (formato yyyy-MM-dd): ");
            dataStr = sc.nextLine().trim();
            try {
                LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("\u001B[31m[ERRORE] Formato data non valido. Riprova.\u001B[0m");
            }
        } while (!dataValida);

        LocalDateTime dataOra = null;
        do {
            System.out.print("Ora (formato HH:mm:ss): ");
            String oraStr = sc.nextLine().trim();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dataOra = LocalDateTime.parse(dataStr + " " + oraStr, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("\u001B[31m[ERRORE] Formato ora non valido. Riprova.\u001B[0m");
            }
        } while (dataOra == null);

        double costo = -1;
        do {
            System.out.print("Costo del biglietto: ");
            try {
                costo = Double.parseDouble(sc.nextLine().replace(',', '.').trim());
                if (costo < 0) {
                    System.out.println("\u001B[31m[ERRORE] Il costo non può essere negativo. Riprova.\u001B[0m");
                    costo = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31m[ERRORE] Formato costo non valido. Inserisci un numero.\u001B[0m");
            }
        } while (costo == -1);

        Proiezione proiezione = new Proiezione(dataOra, film, costo);
        if (gestoreProiezioni.aggiungiProiezione(proiezione)) {
            System.out.println("[OK] Proiezione aggiunta con successo.");
        } else {
            System.out.println("\u001B[31m[ERRORE] La proiezione potrebbe essere già presente nel palinsesto.\u001B[0m");
        }
    }

    /**
     * Metodo di supporto per la ricerca e la selezione di una proiezione specifica presente nel sistema.
     * Permette la ricerca per titolo del film, mostra l'elenco delle proiezioni corrispondenti
     * con impaginazione (25 elementi alla volta) e richiede all'utente di selezionare
     * l'indice della proiezione desiderata.
     *
     * @return la proiezione selezionata dall'utente
     */
    private Proiezione selezionaProiezione() {
        Scanner sc = new Scanner(System.in);
        String titolo;
        List<Proiezione> valide;
        
        do {
            System.out.print("Inserisci il titolo del film della proiezione da cercare: ");
            titolo = sc.nextLine().trim();
            
            valide = gestoreProiezioni.cercaProiezione(titolo);
            if (valide.isEmpty()) {
                System.out.println("\u001B[38;5;208m[INFO] Nessuna proiezione trovata con questo titolo. Riprova.\u001B[0m");
            }
        } while (valide.isEmpty());
        
        int pageIndex = 0;
        do {
            GestoreProiezioni.visualizzaProiezioni(valide, pageIndex);
            pageIndex += 25;
            if (pageIndex < valide.size()) {
                System.out.print("\n--> Mostrate " + pageIndex + " di " + valide.size() + " proiezioni. Premere INVIO per continuare...");
                sc.nextLine();
            }
        } while (pageIndex < valide.size());
        
        int index = -1;
        do {
            System.out.print("\nSeleziona l'indice della proiezione desiderata: ");
            try {
                index = Integer.parseInt(sc.nextLine().trim());
                if (index < 0 || index >= valide.size()) {
                    System.out.println("\u001B[31m[ERRORE] Indice fuori range. Inserisci un numero tra 0 e " + (valide.size() - 1) + ".\u001B[0m");
                    index = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31m[ERRORE] Formato indice non valido. Inserisci un numero intero.\u001B[0m");
            }
        } while (index == -1);
        
        return valide.get(index);
    }

    /**
     * Consente al proiezionista di selezionare una proiezione e modificarne la data e l'ora di programmazione.
     * La nuova data e ora vengono acquisite separatamente e validate nel formato 'yyyy-MM-dd HH:mm:ss'.
     */
    private void modificaProiezione() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----------------------------------------");
        System.out.println("        MODIFICA DATA PROIEZIONE         ");
        System.out.println("-----------------------------------------");
        Proiezione p = selezionaProiezione();
        if (p != null) {
            String dataStr = "";
            boolean dataValida = false;
            do {
                System.out.print("Inserisci la nuova data (formato yyyy-MM-dd): ");
                dataStr = sc.nextLine().trim();
                try {
                    LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    dataValida = true;
                } catch (DateTimeParseException e) {
                    System.out.println("\u001B[31m[ERRORE] Formato data non valido. Riprova.\u001B[0m");
                }
            } while (!dataValida);

            LocalDateTime nuovaDataOra = null;
            do {
                System.out.print("Inserisci la nuova ora (formato HH:mm:ss): ");
                String oraStr = sc.nextLine().trim();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    nuovaDataOra = LocalDateTime.parse(dataStr + " " + oraStr, formatter);
                } catch (DateTimeParseException e) {
                    System.out.println("\u001B[31m[ERRORE] Formato ora non valido. Riprova.\u001B[0m");
                }
            } while (nuovaDataOra == null);

            if (gestoreProiezioni.modificaProiezione(p, nuovaDataOra)) {
                System.out.println("[OK] Proiezione modificata con successo.");
            } else {
                System.out.println("\u001B[31m[ERRORE] Errore durante la modifica.\u001B[0m");
            }
        }
    }

    /**
     * Gestisce l'eliminazione controllata di una proiezione esistente dal palinsesto.
     * Richiede la selezione della proiezione tramite il metodo selezionaProiezione
     * e richiede una conferma esplicita ('s' o 'n') all'utente prima di procedere con la rimozione.
     */
    private void eliminaProiezione() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----------------------------------------");
        System.out.println("           ELIMINA PROIEZIONE            ");
        System.out.println("-----------------------------------------");
        Proiezione p = selezionaProiezione();
        if (p != null) {
            System.out.print("Sei sicuro di voler eliminare questa proiezione? (s/n): ");
            String conferm = sc.nextLine().trim();
            if (conferm.equalsIgnoreCase("s")) {
                if (gestoreProiezioni.eliminaProiezione(p)) {
                    System.out.println("[OK] Proiezione eliminata con successo.");
                } else {
                    System.out.println("\u001B[31m[ERRORE] Errore durante l'eliminazione.\u001B[0m");
                }
            } else {
                System.out.println("[INFO] Operazione annullata.");
            }
        }
    }
}
