package cinemax.menu;

import cinemax.eccezioni.CostoNonValidoException;
import cinemax.eccezioni.UtenteUsernameException;
import cinemax.gestori.GestoreProiezioni;
import cinemax.gestori.GestoreUtenti;
import cinemax.modelli.Cliente;
import cinemax.modelli.Genere;
import cinemax.modelli.Proiezione;
import cinemax.modelli.Ruolo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Gestisce l'interfaccia utente testuale (TUI) per gli utenti non autenticati
 * (Guest).
 * Consente la registrazione di nuovi clienti e la ricerca interattiva delle
 * proiezioni
 * cinematografiche filtrando per titolo, genere, prezzo, data o combinando più
 * filtri.
 *
 * Tutte le interazioni dell'utente sono validate per garantire un corretto
 * inserimento dei dati.
 *
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class MenuGuest {

    private final GestoreUtenti gestoreUtenti;
    private final GestoreProiezioni gestoreProiezioni;

    /**
     * Costruttore della classe MenuGuest.
     *
     * @param gestoreUtenti      Il gestore per le operazioni sugli utenti.
     * @param gestoreProiezioni  Il gestore per le ricerche sulle proiezioni.
     */
    public MenuGuest(GestoreUtenti gestoreUtenti, GestoreProiezioni gestoreProiezioni) {
        this.gestoreUtenti = gestoreUtenti;
        this.gestoreProiezioni = gestoreProiezioni;
    }

    /**
     * Esegue il menu principale per l'utente Guest e gestisce la navigazione interattiva.
     * Rimane attivo in un ciclo finché l'utente non seleziona l'opzione di uscita (X).
     *
     * @return true al termine dell'esecuzione (uscita effettuata)
     */
    public boolean esegui() {
        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;

        do {
            System.out.println("\n=========================================");
            System.out.println("               MENU GUEST                ");
            System.out.println("=========================================");
            System.out.println("[0] Registrati");
            System.out.println("[1] Ricerca proiezione per titolo");
            System.out.println("[2] Ricerca proiezione per genere");
            System.out.println("[3] Ricerca proiezione per prezzo");
            System.out.println("[4] Ricerca proiezione per data");
            System.out.println("[5] Ricerca proiezione mix");
            System.out.println("[X] Esci");
            System.out.println("=========================================");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine();

            switch (sel.toUpperCase()) {
                case "0":
                    registraUtente();
                    break;
                case "1":
                    ricercaPerTitolo();
                    break;
                case "2":
                    ricercaPerGenere();
                    break;
                case "3":
                    ricercaPerPrezzo();
                    break;
                case "4":
                    ricercaPerData();
                    break;
                case "5":
                    ricercaMixInterattiva();
                    break;
                case "X":
                    chiudi = true;
                    break;
                default:
                    System.out.println("\n\u001B[31m[ERRORE] Opzione non valida. Riprova.\u001B[0m");
            }
        } while (!chiudi);

        return true;
    }

    /**
     * Gestisce la procedura guidata di registrazione per un nuovo utente Guest
     * (Cliente).
     * Richiede i dati anagrafici e le credenziali, validando l'input dell'utente.
     */
    public void registraUtente() {
        Scanner sc = new Scanner(System.in);
        String nome, cognome, username, password, luogoDomicilio;
        LocalDate dataNascita = null;
        boolean datiValidi = false;

        System.out.println("\n-----------------------------------------");
        System.out.println("          REGISTRAZIONE CLIENTE          ");
        System.out.println("-----------------------------------------");
        do {
            System.out.print("Inserisci il nome: ");
            nome = sc.nextLine().trim();

            System.out.print("Inserisci il cognome: ");
            cognome = sc.nextLine().trim();

            System.out.print("Inserisci lo username: ");
            username = sc.nextLine().trim();

            System.out.print("Inserisci la password: ");
            password = sc.nextLine().trim();

            System.out.print("Inserisci il luogo domicilio: ");
            luogoDomicilio = sc.nextLine().trim();

            if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty()
                    || luogoDomicilio.isEmpty()) {
                System.out.println("\u001B[31m[ERRORE] Tutti i campi testuali sono obbligatori. Ricomincia la compilazione.\u001B[0m");
                continue;
            }

            System.out.println("\nInserisci la data di nascita (formato AAAA-MM-DD, es. 1995-05-24)");
            System.out.print("Campo facoltativo (premi INVIO per omettere): ");
            String dataString = sc.nextLine().trim();
            try {
                if (!dataString.isEmpty()) {
                    dataNascita = LocalDate.parse(dataString);

                    if (dataNascita.isAfter(LocalDate.now())) {
                        System.out.println("\u001B[31m[ERRORE] La data di nascita non può essere nel futuro.\u001B[0m");
                        continue;
                    }
                }
                datiValidi = true;

            } catch (DateTimeParseException e) {
                System.out.println("\u001B[31m[ERRORE] Formato data non valido. Utilizzare rigorosamente il pattern AAAA-MM-DD.\u001B[0m");
            }
        } while (!datiValidi);

        // Creo Cliente
        Cliente nuovoCliente = new Cliente(nome, cognome, username, password, dataNascita, Ruolo.CLIENTE,
                luogoDomicilio);

        try {
            gestoreUtenti.registraCliente(nuovoCliente);
            System.out.println(
                    "\n[OK] Registrazione completata! Ora puoi effettuare il login con lo username: " + username);

        } catch (UtenteUsernameException e) {
            System.out.println("\n\u001B[31m[ERRORE] Registrazione fallita: " + e.getMessage() + "\u001B[0m");

        } catch (Exception e) {
            // Gestione di sicurezza per errori imprevisti generici
            System.out.println("\n\u001B[31m[ERRORE] Impossibile completare l'operazione. Riprova più tardi.\u001B[0m");
        }
    }

    /**
     * Avvia l'interfaccia interattiva per la ricerca di proiezioni tramite titolo.
     * Richiede l'inserimento di una stringa e chiama il metodo di ricerca.
     */
    private void ricercaPerTitolo() {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            System.out.println("\n-----------------------------------------");
            System.out.println("           RICERCA PER TITOLO            ");
            System.out.println("-----------------------------------------");
            System.out.print("Inserisci il titolo del film: ");
            String titolo = sc.nextLine();
            if (titolo.isBlank()) {
                System.out.println("\u001B[31m[ERRORE] Il titolo non può essere vuoto.\u001B[0m");
            } else {
                cercaTitolo(titolo);
                sel = "esci";
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Avvia l'interfaccia interattiva per la ricerca di proiezioni tramite genere.
     * Mostra l'elenco dei generi disponibili e richiede una scelta.
     */
    private void ricercaPerGenere() {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            System.out.println("\n-----------------------------------------");
            System.out.println("           RICERCA PER GENERE            ");
            System.out.println("-----------------------------------------");
            System.out.print("Generi disponibili: ");
            for (Genere g : Genere.values()) {
                System.out.print(g + " ");
            }
            System.out.print("\nInserisci il genere del film: ");
            String genere = sc.nextLine().toUpperCase().trim();
            if (genere.isBlank()) {
                System.out.println("\u001B[31m[ERRORE] Il genere non può essere vuoto.\u001B[0m");
            } else {
                try {
                    Genere g = Genere.valueOf(genere);
                    cercaGenere(g);
                    sel = "esci";
                } catch (IllegalArgumentException e) {
                    System.out.println("\u001B[31m[ERRORE] Il genere inserito non esiste. Riprova.\u001B[0m");
                }
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Avvia l'interfaccia interattiva per la ricerca di proiezioni tramite prezzo.
     * Consente di cercare per prezzo esatto o per un intervallo di prezzi.
     */
    private void ricercaPerPrezzo() {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            System.out.println("\n-----------------------------------------");
            System.out.println("           RICERCA PER PREZZO            ");
            System.out.println("-----------------------------------------");
            System.out.println("[1] Cerca prezzo esatto");
            System.out.println("[2] Cerca fascia di prezzo");
            System.out.println("-----------------------------------------");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine();
            switch (sel) {
                case "1":
                    System.out.print("Inserisci il prezzo del biglietto: ");
                    try {
                        double prezzo = Double.parseDouble(sc.nextLine().replace(',', '.'));
                        if (prezzo <= 0) {
                            System.out.println("\u001B[31m[ERRORE] Il prezzo deve essere maggiore di 0.\u001B[0m");
                        } else {
                            cercaPrezzo(prezzo, prezzo);
                            sel = "esci";
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\u001B[31m[ERRORE] Formato non valido.\u001B[0m");
                    }
                    break;
                case "2":
                    try {
                        double min, max;
                        System.out.print("Inserisci il prezzo minimo: ");
                        min = Double.parseDouble(sc.nextLine().replace(',', '.'));
                        System.out.print("Inserisci il prezzo massimo: ");
                        max = Double.parseDouble(sc.nextLine().replace(',', '.'));
                        if (min < 0 || min > max) {
                            System.out.println("\u001B[31m[ERRORE] Il prezzo minimo deve essere >= 0 e minore o uguale al prezzo massimo.\u001B[0m");
                        } else {
                            cercaPrezzo(min, max);
                            sel = "esci";
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\u001B[31m[ERRORE] Formato non valido.\u001B[0m");
                    } catch (CostoNonValidoException e) {
                        System.out.println("\u001B[31m[ERRORE] " + e.getMessage() + "\u001B[0m");
                    }
                    break;
                default:
                    System.out.println("\u001B[31m[ERRORE] Opzione non valida. Riprova.\u001B[0m");
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Avvia l'interfaccia interattiva per la ricerca di proiezioni tramite data.
     * Consente di cercare per data esatta o per un intervallo di date.
     */
    private void ricercaPerData() {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            System.out.println("\n-----------------------------------------");
            System.out.println("            RICERCA PER DATA             ");
            System.out.println("-----------------------------------------");
            System.out.println("[1] Cerca data precisa");
            System.out.println("[2] Cerca intervallo di date");
            System.out.println("-----------------------------------------");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine();
            switch (sel) {
                case "1":
                    System.out.print("Inserisci la data (formato AAAA-MM-DD, es. 2026-05-24): ");
                    String dataString = sc.nextLine().trim();
                    try {
                        LocalDate data = LocalDate.parse(dataString);
                        cercaData(data, data);
                        sel = "esci";
                    } catch (DateTimeParseException e) {
                        System.out.println("\u001B[31m[ERRORE] Formato data non valido. Usa il pattern AAAA-MM-DD.\u001B[0m");
                    }
                    break;
                case "2":
                    System.out.print("Inserisci data inizio (formato AAAA-MM-DD, es. 2026-05-24): ");
                    String dataIn = sc.nextLine().trim();
                    System.out.print("Inserisci data fine (formato AAAA-MM-DD, es. 2026-05-24): ");
                    String dataFin = sc.nextLine().trim();
                    try {
                        LocalDate data1 = LocalDate.parse(dataIn);
                        LocalDate data2 = LocalDate.parse(dataFin);
                        if (!data1.isAfter(data2)) {
                            cercaData(data1, data2);
                            sel = "esci";
                        } else {
                            System.out.println("\u001B[31m[ERRORE] La data inizio deve essere minore o uguale della data di fine.\u001B[0m");
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("\u001B[31m[ERRORE] Formato data non valido. Usa il pattern AAAA-MM-DD.\u001B[0m");
                    }
                    break;
                default:
                    System.out.println("\u001B[31m[ERRORE] Opzione non valida. Riprova.\u001B[0m");
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Avvia l'interfaccia interattiva per la ricerca combinata (mix) di proiezioni.
     * Consente di filtrare contemporaneamente per titolo, genere, date e prezzi.
     */
    private void ricercaMixInterattiva() {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            String titolo = null;
            Genere genere = null;
            LocalDate dataIn = null;
            LocalDate dataFin = null;
            double prezzoMin = -1;
            double prezzoMax = -1;

            System.out.println("\n-----------------------------------------");
            System.out.println("            RICERCA COMBINATA            ");
            System.out.println("-----------------------------------------");

            System.out.print("Inserisci il titolo (vuoto per ignorare): ");
            String titoloInput = sc.nextLine().trim();
            if (!titoloInput.isEmpty()) {
                titolo = titoloInput;
            }

            System.out.print("Generi disponibili: ");
            for (Genere g : Genere.values()) {
                System.out.print(g + " ");
            }
            System.out.print("\nInserisci il genere (vuoto per ignorare): ");
            String genereInput = sc.nextLine().trim();
            if (!genereInput.isEmpty()) {
                try {
                    genere = Genere.valueOf(genereInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("\u001B[31m[ERRORE] Genere inesistente: " + genereInput + "\u001B[0m");
                    return;
                }
            }

            System.out.print("Inserisci data inizio (AAAA-MM-DD) o vuoto per ignorare: ");
            String dataInStr = sc.nextLine().trim();
            if (!dataInStr.isEmpty()) {
                System.out.print("Inserisci data fine (AAAA-MM-DD): ");
                String dataFinStr = sc.nextLine().trim();
                if (dataFinStr.isEmpty()) {
                    System.out.println("\u001B[31m[ERRORE] La data di fine non può essere vuota se la data di inizio è presente.\u001B[0m");
                    return;
                }
                try {
                    dataIn = LocalDate.parse(dataInStr);
                    dataFin = LocalDate.parse(dataFinStr);
                } catch (DateTimeParseException e) {
                    System.out.println("\u001B[31m[ERRORE] Formato data non valido. Usa AAAA-MM-DD.\u001B[0m");
                    return;
                }
            }

            System.out.print("Inserisci prezzo minimo (vuoto per ignorare): ");
            String prezzoMinStr = sc.nextLine().trim();
            if (!prezzoMinStr.isEmpty()) {
                System.out.print("Inserisci prezzo massimo: ");
                String prezzoMaxStr = sc.nextLine().trim();
                if (prezzoMaxStr.isEmpty()) {
                    System.out.println("\u001B[31m[ERRORE] Il prezzo massimo non può essere vuoto se il minimo è presente.\u001B[0m");
                    return;
                }
                try {
                    prezzoMin = Double.parseDouble(prezzoMinStr.replace(',', '.'));
                    prezzoMax = Double.parseDouble(prezzoMaxStr.replace(',', '.'));
                } catch (NumberFormatException e) {
                    System.out.println("\u001B[31m[ERRORE] Prezzo non valido. Inserisci un numero.\u001B[0m");
                    return;
                }
            }

            try {
                cercaMix(titolo, genere, dataIn, dataFin, prezzoMin, prezzoMax);
                sel = "esci";
            } catch (RuntimeException e) {
                System.out.println("\u001B[31m[ERRORE] Errore nella ricerca mix: " + e.getMessage() + "\u001B[0m");
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Effettua la ricerca delle proiezioni per titolo e ne gestisce la visualizzazione paginata.
     *
     * @param titolo Il titolo o parte del titolo del film da cercare.
     */
    private void cercaTitolo(String titolo) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(titolo);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.size()) {
                    System.out.print("\n--> Mostrate " + index + " di " + valide.size() + " proiezioni. Premere INVIO per continuare...");
                    sc.nextLine();
                }
            } while (index < valide.size());
        } else {
            System.out.println("[INFO] Non ci sono proiezioni con questo titolo: " + titolo);
        }
    }

    /**
     * Effettua la ricerca delle proiezioni per genere e ne gestisce la visualizzazione paginata.
     *
     * @param genere Il genere cinematografico da filtrare.
     */
    private void cercaGenere(Genere genere) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(genere);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.size()) {
                    System.out.print("\n--> Mostrate " + index + " di " + valide.size() + " proiezioni. Premere INVIO per continuare...");
                    sc.nextLine();
                }
            } while (index < valide.size());
        } else {
            System.out.println("[INFO] Non ci sono proiezioni con questo genere: " + genere);
        }
    }

    /**
     * Effettua la ricerca delle proiezioni per range di prezzo e ne gestisce la visualizzazione paginata.
     *
     * @param min Prezzo minimo (inclusivo).
     * @param max Prezzo massimo (inclusivo).
     */
    private void cercaPrezzo(double min, double max) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(min, max);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.size()) {
                    System.out.print("\n--> Mostrate " + index + " di " + valide.size() + " proiezioni. Premere INVIO per continuare...");
                    sc.nextLine();
                }
            } while (index < valide.size());
        } else {
            if (min == max) {
                System.out.println("[INFO] Non ci sono proiezioni con questo prezzo: " + min);
            } else {
                System.out.println("[INFO] Non ci sono proiezioni in questo range: " + min + " - " + max);
            }
        }
    }

    /**
     * Effettua la ricerca delle proiezioni per range di date e ne gestisce la visualizzazione paginata.
     *
     * @param dataIn Data di inizio (inclusiva).
     * @param dataFin Data di fine (inclusiva).
     */
    private void cercaData(LocalDate dataIn, LocalDate dataFin) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(dataIn, dataFin);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.size()) {
                    System.out.print("\n--> Mostrate " + index + " di " + valide.size() + " proiezioni. Premere INVIO per continuare...");
                    sc.nextLine();
                }
            } while (index < valide.size());
        } else if (dataIn.isEqual(dataFin)) {
            System.out.println("[INFO] Non ci sono proiezioni in questa data: " + dataIn);
        } else {
            System.out.println("[INFO] Non ci sono proiezioni in questo range: " + dataIn + " - " + dataFin);
        }
    }

    /**
     * Effettua la ricerca multi-filtro delle proiezioni e ne gestisce la visualizzazione paginata.
     *
     * @param titolo    Titolo o parte del titolo (opzionale).
     * @param genere    Genere cinematografico (opzionale).
     * @param dataIn    Data inizio intervallo (opzionale).
     * @param dataFin   Data fine intervallo (opzionale).
     * @param prezzoMin Prezzo minimo (opzionale, -1 per ignorare).
     * @param prezzoMax Prezzo massimo (opzionale, -1 per ignorare).
     */
    private void cercaMix(String titolo, Genere genere, LocalDate dataIn, LocalDate dataFin, double prezzoMin, double prezzoMax) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(titolo, genere, dataIn, dataFin, prezzoMin, prezzoMax);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.size()) {
                    System.out.print("\n--> Mostrate " + index + " di " + valide.size() + " proiezioni. Premere INVIO per continuare...");
                    sc.nextLine();
                }
            } while (index < valide.size());
        } else {
            System.out.println("[INFO] Non ci sono proiezioni con i filtri selezionati.");
        }
    }
}
