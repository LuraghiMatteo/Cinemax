package cinemax.menu;

import cinemax.gestori.GestoreProiezioni;
import cinemax.modelli.Film;
import cinemax.modelli.Genere;
import cinemax.modelli.Proiezione;

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

    /**
     * Visualizza il menu principale per l'utente Proiezionista e gestisce la navigazione interattiva
     * tra le varie operazioni disponibili (inserimento, modifica ed eliminazione).
     * Rimane attivo in un ciclo finché l'utente non seleziona l'opzione di logout (X).
     *
     * @param gestoreProiezioni il gestore della business logic per i film e le proiezioni
     * @return true al termine dell'esecuzione del menu (logout effettuato)
     */
    public static boolean menu(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;
        
        do {
            System.out.println("\nBenvenuto nel menu Proiezionista");
            System.out.println("[1] Inserire un film e le sue proiezioni");
            System.out.println("[2] Modificare la data di una proiezione");
            System.out.println("[3] Eliminare una proiezione");
            System.out.println("[X] Logout");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine();

            switch (sel.toUpperCase()) {
                case "1":
                    inserisciFilmEProiezioni(gestoreProiezioni);
                    break;
                case "2":
                    modificaProiezione(gestoreProiezioni);
                    break;
                case "3":
                    eliminaProiezione(gestoreProiezioni);
                    break;
                case "X":
                    chiudi = true;
                    break;
                default:
                    System.err.println("Opzione non valida. Riprova");
            }
        } while (!chiudi);

        return true;
    }

    /**
     * Gestisce l'inserimento guidato passo-passo di un nuovo film e della prima proiezione associata.
     * Richiede l'input di:
     * - Titolo (non vuoto)
     * - Genere (selezionato tra i valori dell'enum {@link Genere})
     * - Regista (non vuoto)
     * - Anno di uscita (compreso tra il 1800 e il 2100)
     * - Durata in minuti (maggiore di 0)
     * - Età minima per la visione (maggiore o uguale a 0)
     * Successivamente richiede la data e l'ora per la proiezione iniziale e il relativo costo del biglietto,
     * effettuando la validazione per ciascun campo.
     *
     * @param gestoreProiezioni il gestore da utilizzare per registrare la nuova proiezione e il film
     */
    private static void inserisciFilmEProiezioni(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Inserimento nuovo Film ---");
        
        String titolo;
        do {
            System.out.print("Titolo: ");
            titolo = sc.nextLine();
            if (titolo.isBlank()) {
                System.err.println("Il titolo non può essere vuoto o composto solo da spazi. Riprova.");
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
                System.err.println("Genere non valido. Riprova.");
            }
        } while (genere == null);

        String regista;
        do {
            System.out.print("Regista: ");
            regista = sc.nextLine();
            if (regista.isBlank()) {
                System.err.println("Il nome del regista non può essere vuoto. Riprova.");
            }
        } while (regista.isBlank());

        int anno = -1;
        do {
            System.out.print("Anno: ");
            try {
                anno = Integer.parseInt(sc.nextLine().trim());
                if (anno < 1800 || anno > 2100) {
                    System.err.println("Anno non realistico. Riprova.");
                    anno = -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Formato anno non valido. Inserisci un numero intero.");
            }
        } while (anno == -1);

        int durata = -1;
        do {
            System.out.print("Durata (in minuti): ");
            try {
                durata = Integer.parseInt(sc.nextLine().trim());
                if (durata <= 0) {
                    System.err.println("La durata deve essere maggiore di 0. Riprova.");
                    durata = -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Formato durata non valido. Inserisci un numero intero.");
            }
        } while (durata == -1);

        int etaMinima = -1;
        do {
            System.out.print("Età minima: ");
            try {
                etaMinima = Integer.parseInt(sc.nextLine().trim());
                if (etaMinima < 0) {
                    System.err.println("L'età minima non può essere negativa. Riprova.");
                    etaMinima = -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Formato età non valido. Inserisci un numero intero.");
            }
        } while (etaMinima == -1);

        Film film = new Film(titolo, genere, regista, anno, durata, etaMinima);

        System.out.println("\n--- Inserimento Proiezione per " + titolo + " ---");
        
        String dataStr = "";
        boolean dataValida = false;
        do {
            System.out.print("Data (formato yyyy-MM-dd): ");
            dataStr = sc.nextLine().trim();
            try {
                LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dataValida = true;
            } catch (DateTimeParseException e) {
                System.err.println("Formato data non valido. Riprova.");
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
                System.err.println("Formato ora non valido. Riprova.");
            }
        } while (dataOra == null);

        double costo = -1;
        do {
            System.out.print("Costo del biglietto: ");
            try {
                costo = Double.parseDouble(sc.nextLine().replace(',', '.').trim());
                if (costo < 0) {
                    System.err.println("Il costo non può essere negativo. Riprova.");
                    costo = -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Formato costo non valido. Inserisci un numero.");
            }
        } while (costo == -1);

        Proiezione proiezione = new Proiezione(dataOra, film, costo);
        if (gestoreProiezioni.aggiungiProiezione(proiezione)) {
            System.out.println("Proiezione aggiunta con successo.");
        } else {
            System.err.println("Errore: la proiezione potrebbe essere già presente nel palinsesto.");
        }
    }

    /**
     * Metodo di supporto per la ricerca e la selezione di una proiezione specifica presente nel sistema.
     * Permette la ricerca per titolo del film, mostra l'elenco delle proiezioni corrispondenti
     * con impaginazione (25 elementi alla volta) e richiede all'utente di selezionare
     * l'indice della proiezione desiderata.
     *
     * @param gestoreProiezioni il gestore delle proiezioni per effettuare la ricerca
     * @return la proiezione selezionata dall'utente
     */
    private static Proiezione selezionaProiezione(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String titolo;
        List<Proiezione> valide;
        
        do {
            System.out.print("Inserisci il titolo del film della proiezione da cercare: ");
            titolo = sc.nextLine().trim();
            
            valide = gestoreProiezioni.cercaProiezione(titolo);
            if (valide.isEmpty()) {
                System.err.println("Nessuna proiezione trovata con questo titolo. Riprova.");
            }
        } while (valide.isEmpty());
        
        int pageIndex = 0;
        do {
            GestoreProiezioni.visualizzaProiezioni(valide, pageIndex);
            pageIndex += 25;
            if (pageIndex < valide.size()) {
                System.out.print("Proiezioni " + pageIndex + " su " + valide.size() + ". ");
                System.out.print("Premere invio per continuare");
                sc.nextLine();
            }
        } while (pageIndex < valide.size());
        
        int index = -1;
        do {
            System.out.print("Seleziona l'indice della proiezione: ");
            try {
                index = Integer.parseInt(sc.nextLine().trim());
                if (index < 0 || index >= valide.size()) {
                    System.err.println("Indice fuori range. Inserisci un numero tra 0 e " + (valide.size() - 1) + ".");
                    index = -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Formato indice non valido. Inserisci un numero intero.");
            }
        } while (index == -1);
        
        return valide.get(index);
    }

    /**
     * Consente al proiezionista di selezionare una proiezione e modificarne la data e l'ora di programmazione.
     * La nuova data e ora vengono acquisite separatamente e validate nel formato 'yyyy-MM-dd HH:mm:ss'.
     *
     * @param gestoreProiezioni il gestore delle proiezioni incaricato di eseguire l'aggiornamento
     */
    private static void modificaProiezione(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Modifica Data Proiezione ---");
        Proiezione p = selezionaProiezione(gestoreProiezioni);
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
                    System.err.println("Formato data non valido. Riprova.");
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
                    System.err.println("Formato ora non valido. Riprova.");
                }
            } while (nuovaDataOra == null);

            if (gestoreProiezioni.modificaProiezione(p, nuovaDataOra)) {
                System.out.println("Proiezione modificata con successo.");
            } else {
                System.err.println("Errore durante la modifica.");
            }
        }
    }

    /**
     * Gestisce l'eliminazione controllata di una proiezione esistente dal palinsesto.
     * Richiede la selezione della proiezione tramite {@link #selezionaProiezione}
     * e richiede una conferma esplicita ('s' o 'n') all'utente prima di procedere con la rimozione.
     *
     * @param gestoreProiezioni il gestore delle proiezioni incaricato di rimuovere la proiezione
     */
    private static void eliminaProiezione(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Elimina Proiezione ---");
        Proiezione p = selezionaProiezione(gestoreProiezioni);
        if (p != null) {
            System.out.print("Sei sicuro di voler eliminare questa proiezione? (s/n): ");
            String conferm = sc.nextLine().trim();
            if (conferm.equalsIgnoreCase("s")) {
                if (gestoreProiezioni.eliminaProiezione(p)) {
                    System.out.println("Proiezione eliminata con successo.");
                } else {
                    System.err.println("Errore durante l'eliminazione.");
                }
            } else {
                System.out.println("Operazione annullata.");
            }
        }
    }
}
