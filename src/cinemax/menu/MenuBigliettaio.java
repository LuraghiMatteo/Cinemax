package cinemax.menu;

import cinemax.gestori.GestorePrenotazioni;
import cinemax.modelli.Prenotazione;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Gestisce l'interfaccia a riga di comando (TUI) per le funzionalità dedicate al Bigliettaio.
 * Consente di visionare il riepilogo giornaliero delle prenotazioni e di effettuare ricerche mirate.
 * * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class MenuBigliettaio {

    /**
     * Menu principale interattivo per l'utente loggato come Bigliettaio.
     *
     * @param gestorePrenotazioni Il gestore contenente la logica e i dati delle prenotazioni.
     */
    public static boolean menu(GestorePrenotazioni gestorePrenotazioni) {
        Scanner sc = new Scanner(System.in);
        String sel;

        do {
            System.out.println("\n=== SPORTELLO BIGLIETTERIA ===");
            System.out.println("[1] Visualizza le prenotazioni di OGGI");
            System.out.println("[2] Apri il modulo di ricerca prenotazioni");
            System.out.println("[X] Logout");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine().trim().toUpperCase();

            switch (sel) {
                case "1":
                    mostraPrenotazioniOdierne(gestorePrenotazioni);
                    break;
                case "2":
                    sottoMenuRicerca(gestorePrenotazioni);
                    break;
                case "X":
                    System.out.println("Chiusura sessione biglietteria effettuata. Arrivederci!");
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        } while (!sel.equals("X"));
        return true;
    }

    /**
     * Recupera e stampa a video tutte le prenotazioni relative agli spettacoli della giornata corrente.
     */
    private static void mostraPrenotazioniOdierne(GestorePrenotazioni gPrenotazioni) {
        System.out.println("\n--- PRENOTAZIONI NELLA DATA ODIERNA ---");

        // Uso Metodo del gestorePrenotazioni per ottenere quelle in data odierna
        List<Prenotazione> odierne = gPrenotazioni.visualizzaPrenotazioniOdierne();

        // Se la lista è vuota, notifichiamo la situazione senza bloccare il programma
        if (odierne.isEmpty()) {
            System.out.println("Nessuna prenotazione registrata per gli spettacoli di oggi.");
            return;
        }

        // visualizzazione dettagliata dei biglietti venduti per oggi
        stampalista(odierne);
    }

    /**
     * Sotto-menu interattivo che aggrega tutti i criteri di ricerca messi a disposizione per il personale staff.
     */
    private static void sottoMenuRicerca(GestorePrenotazioni gPrenotazioni) {
        Scanner sc = new Scanner(System.in);
        String scelta;

        do {
            System.out.println("\n--- MODULO DI RICERCA PRENOTAZIONI ---");
            System.out.println("[1] Cerca per Codice Univoco (es. PR000001)");
            System.out.println("[2] Cerca per Anagrafica Cliente (Nome e Cognome)");
            System.out.println("[3] Cerca per Titolo del Film (Ricerca parziale)");
            System.out.println("[4] Cerca per Intervallo di Date");
            System.out.println("[X] Torna al menu precedente");
            System.out.print("Scegli il criterio di ricerca: ");
            scelta = sc.nextLine().trim().toUpperCase();

            switch (scelta) {
                case "1":
                    System.out.print("Inserisci il codice esatto (8 caratteri): ");
                    String cod = sc.nextLine().trim();
                    Prenotazione trovata = gPrenotazioni.cercaPrenotazionePerCodice(cod);
                    if (trovata != null) {
                        System.out.println("\nPrenotazione Rintracciata:");
                        System.out.println(trovata);
                    } else {
                        System.out.println("Nessun record corrispondente al codice inserito.");
                    }
                    break;

                case "2":
                    System.out.print("Inserisci il nome del cliente: ");
                    String nome = sc.nextLine().trim();
                    System.out.print("Inserisci il cognome del cliente: ");
                    String cognome = sc.nextLine().trim();
                    // Uso il metodo di supporto insieme a quello del gestore delle prenotazioni
                    stampalista(gPrenotazioni.cercaPrenotazionePerCliente(nome, cognome));
                    break;

                case "3":
                    System.out.print("Inserisci il titolo del film (anche parziale): ");
                    String titolo = sc.nextLine().trim();
                    // Delego la ricerca parziale per stringa contenuto
                    stampalista(gPrenotazioni.cercaPrenotazionePerTitolo(titolo));
                    break;

                case "4":
                    try {
                        System.out.print("Inserisci data inizio intervallo (AAAA-MM-DD) o premi INVIO per omettere: ");
                        String inizioInput = sc.nextLine().trim();
                        LocalDate inizio = inizioInput.isEmpty() ? null : LocalDate.parse(inizioInput);

                        System.out.print("Inserisci data fine intervallo (AAAA-MM-DD) o premi INVIO per omettere: ");
                        String fineInput = sc.nextLine().trim();
                        LocalDate fine = fineInput.isEmpty() ? null : LocalDate.parse(fineInput);

                        // Delego al gestore
                        stampalista(gPrenotazioni.cercaPrenotazionePerDate(inizio, fine));
                    } catch (DateTimeParseException e) {
                        System.out.println("Errore: Formato data non coerente. Usa la struttura AAAA-MM-DD.");
                    }
                    break;

                case "X":
                    break;

                default:
                    System.out.println("Criterio non riconosciuto.");
            }
        } while (!scelta.equals("X"));
    }

    /**
     * Metodo di utilità interno per la stampa delle liste risultanti
     * dalle ricerche, evitando ridondanze di codice
     */
    private static void stampalista(List<Prenotazione> risultati) {
        if (risultati.isEmpty()) {
            System.out.println("La ricerca non ha prodotto alcun risultato.");
            return;
        }
        System.out.println("\nCorrispondenze rintracciate (" + risultati.size() + "):");

        Scanner sc = new Scanner(System.in);
        int index = 0;
        do {
            GestorePrenotazioni.visualizzaPrenotazioni(risultati, index);
            index += 25;
            if (index < risultati.toArray().length) {
                System.out.print("Proiezioni " + index + " su " + risultati.toArray().length + ". ");
                System.out.print("Premere invio per continuare");
                sc.nextLine();
            }
        } while (index < risultati.toArray().length);
    }
}