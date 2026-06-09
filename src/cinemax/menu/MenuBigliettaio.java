package cinemax.menu;

import cinemax.gestori.GestorePrenotazioni;
import cinemax.modelli.Prenotazione;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Gestisce l'interfaccia utente testuale (TUI) dedicata al personale di sportello con ruolo Bigliettaio.
 * Classe strutturata secondo i canoni della programmazione a oggetti (OOP): incapsula il rispettivo
 * gestore delle prenotazioni come dipendenza d'istanza per manipolare i record del database.
 * Consente il monitoraggio in tempo reale del flusso di cassa giornaliero e l'esecuzione di ricerche storiche multicriterio.
 * * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 */
public class MenuBigliettaio {

    // Dipendenza di business logic incapsulata a livello d'istanza
    private final GestorePrenotazioni gestorePrenotazioni;

    /**
     * Costruttore completo per l'inizializzazione del modulo menù della biglietteria.
     * Configura l'accesso controllato ai servizi core iniettando il manager delle prenotazioni.
     *
     * @param gestorePrenotazioni Il gestore di sistema centralizzato contenente l'archivio delle prenotazioni.
     */
    public MenuBigliettaio(GestorePrenotazioni gestorePrenotazioni) {
        this.gestorePrenotazioni = gestorePrenotazioni;
    }

    /**
     * Avvia e governa il ciclo interattivo del menù principale per l'interfaccia dello sportello.
     * Intercetta i comandi operativi digitati dal bigliettaio, smistandoli verso le funzionalità dedicate.
     *
     * @return true ad esecuzione completata, notificando l'avvenuto logout del dipendente al pannello principale.
     */
    public boolean esegui() {
        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;

        do {
            System.out.println("\n=========================================");
            System.out.println("         SPORTELLO BIGLIETTERIA          ");
            System.out.println("=========================================");
            System.out.println("[1] Visualizza le prenotazioni di OGGI");
            System.out.println("[2] Apri il modulo di ricerca prenotazioni");
            System.out.println("[X] Esci (Logout)");
            System.out.println("=========================================");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine().trim().toUpperCase();

            switch (sel) {
                case "1":
                    // Invocazione della procedura di rendering odierna
                    mostraPrenotazioniOdierne();
                    break;
                case "2":
                    // Deviazione del flusso verso il sotto-pannello di ricerca storico
                    sottoMenuRicerca();
                    break;
                case "X":
                    chiudi = true;
                    System.out.println("\n[OK] Chiusura sessione biglietteria effettuata. Arrivederci!");
                    break;
                default:
                    System.out.println("\n[ERRORE] Opzione non valida. Riprova.");
            }
        } while (!chiudi);
        return true;
    }

    /**
     * Interroga la business logic per isolare ed esporre a terminale l'elenco completo
     * di tutti i biglietti staccati aventi come oggetto spettacoli previsti nella giornata odierna.
     *
     * @see GestorePrenotazioni#visualizzaPrenotazioniOdierne()
     */
    private void mostraPrenotazioniOdierne() {
        System.out.println("\n-----------------------------------------");
        System.out.println("     PRENOTAZIONI NELLA DATA ODIERNA     ");
        System.out.println("-----------------------------------------");

        // Estrazione record sfruttando l'attributo d'istanza 'this.gestorePrenotazioni'
        List<Prenotazione> odierne = this.gestorePrenotazioni.visualizzaPrenotazioniOdierne();

        if (odierne.isEmpty()) {
            System.out.println("[INFO] Nessuna prenotazione registrata per gli spettacoli di oggi.");
            return;
        }

        // Forward dell'elenco estratto al motore interno di impaginazione e rendering grafico
        stampalista(odierne);
    }

    /**
     * Sotto-menu interattivo secondario che organizza e modula i differenti filtri di ricerca
     * messi a disposizione dello staff per rintracciare una o più ricevute d'acquisto.
     * Valida la coerenza formale delle stringhe temporali immesse tramite costrutti di cattura d'eccezione.
     */
    private void sottoMenuRicerca() {
        Scanner sc = new Scanner(System.in);
        String scelta;

        do {
            System.out.println("\n=========================================");
            System.out.println("     MODULO DI RICERCA PRENOTAZIONI      ");
            System.out.println("=========================================");
            System.out.println("[1] Cerca per Codice Univoco (es. PR000001)");
            System.out.println("[2] Cerca per Anagrafica Cliente (Nome e Cognome)");
            System.out.println("[3] Cerca per Titolo del Film (Ricerca parziale)");
            System.out.println("[4] Cerca per Intervallo di Date");
            System.out.println("[X] Torna al menu precedente");
            System.out.println("=========================================");
            System.out.print("Scegli il criterio di ricerca: ");
            scelta = sc.nextLine().trim().toUpperCase();

            switch (scelta) {
                case "1":
                    System.out.print("\nInserisci il codice esatto (8 caratteri): ");
                    String cod = sc.nextLine().trim();

                    // Ricerca puntuale per chiave primaria
                    Prenotazione trovata = gestorePrenotazioni.cercaPrenotazionePerCodice(cod);
                    if (trovata != null) {
                        System.out.println("\n[OK] Prenotazione rintracciata:");
                        System.out.println(trovata);
                    } else {
                        System.out.println("[INFO] Nessun record corrispondente al codice inserito.");
                    }
                    break;

                case "2":
                    System.out.print("\nInserisci il nome del cliente: ");
                    String nome = sc.nextLine().trim();
                    System.out.print("Inserisci il cognome del cliente: ");
                    String cognome = sc.nextLine().trim();

                    // Filtro per stringa anagrafica combinata
                    stampalista(gestorePrenotazioni.cercaPrenotazionePerCliente(nome, cognome));
                    break;

                case "3":
                    System.out.print("\nInserisci il titolo del film (anche parziale): ");
                    String titolo = sc.nextLine().trim();

                    // Estrazione tramite algoritmo di ricerca parziale (contains/indexOf)
                    stampalista(gestorePrenotazioni.cercaPrenotazionePerTitolo(titolo));
                    break;

                case "4":
                    try {
                        System.out.print("\nInserisci data inizio intervallo (AAAA-MM-DD) o premi INVIO per omettere: ");
                        String inizioInput = sc.nextLine().trim();
                        LocalDate inizio = inizioInput.isEmpty() ? null : LocalDate.parse(inizioInput);

                        System.out.print("Inserisci data fine intervallo (AAAA-MM-DD) o premi INVIO per omettere: ");
                        String fineInput = sc.nextLine().trim();
                        LocalDate fine = fineInput.isEmpty() ? null : LocalDate.parse(fineInput);

                        // Estrazione cronologica basata su range temporale inclusivo
                        stampalista(gestorePrenotazioni.cercaPrenotazionePerDate(inizio, fine));
                    } catch (DateTimeParseException e) {
                        // Protezione da crash indotti da inserimenti di stringhe non conformi allo standard ISO-8601
                        System.out.println("[ERRORE] Formato data non coerente. Usa la struttura AAAA-MM-DD.");
                    }
                    break;

                case "X":
                    break;

                default:
                    System.out.println("[ERRORE] Criterio non riconosciuto.");
            }
        } while (!scelta.equals("X"));
    }

    /**
     * Componente di utilità interna strutturato per gestire l'impaginazione controllata a blocchi della TUI.
     * Evita la saturazione visiva del terminale visualizzando le prenotazioni in gruppi sequenziali
     * da 25 elementi ciascuno e congelando lo scroll tramite interruzioni di input.
     *
     * @param risultati La lista contenente l'insieme delle prenotazioni estratte dalle query dei gestori.
     */
    private void stampalista(List<Prenotazione> risultati) {
        if (risultati.isEmpty()) {
            System.out.println("[INFO] La ricerca non ha prodotto alcun risultato.");
            return;
        }
        System.out.println("\nPrenotazioni trovate (" + risultati.size() + "):");

        Scanner sc = new Scanner(System.in);
        int index = 0;
        do {
            // Invocazione del metodo delegato a gestire la logica visiva della pagina corrente
            GestorePrenotazioni.visualizzaPrenotazioni(risultati, index);
            index += 25;

            // Verifica di esistenza di record residui oltre la pagina renderizzata
            if (index < risultati.size()) {
                System.out.print("\n--> Mostrate " + index + " di " + risultati.size() + " prenotazioni. Premere INVIO per continuare...");
                sc.nextLine(); // Blocco temporaneo dello scanner per scopi di lettura antropica
            }
        } while (index < risultati.size());
    }
}