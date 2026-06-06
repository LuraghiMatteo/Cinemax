package cinemax.menu;

import cinemax.eccezioni.CostoNonValidoExeption;
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
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class MenuGuest {

    /**
     * Visualizza il menu principale per l'utente Guest e gestisce la navigazione
     * interattiva.
     * Rimane attivo in un ciclo finché l'utente non seleziona l'opzione di uscita
     * (X).
     *
     * @param gestoreUtenti     il gestore per la registrazione dei clienti
     * @param gestoreProiezioni il gestore per la ricerca delle proiezioni
     * @return true al termine dell'esecuzione (uscita effettuata)
     */
    public static boolean menu(GestoreUtenti gestoreUtenti, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;

        do {
            System.out.println("\nBenvenuto nel menu Guest");
            System.out.println("[0] Registrati");
            System.out.println("[1] Ricerca proiezione per titolo o sotto-stringa del titolo");
            System.out.println("[2] Ricerca proiezione per genere");
            System.out.println("[3] Ricerca proiezione per prezzo");
            System.out.println("[4] Ricerca proiezione per data");
            System.out.println("[5] Ricerca proiezione mix");
            System.out.println("[X] Esci");
            System.out.print("Scegli un'opzione: ");
            sel = sc.nextLine();

            switch (sel.toUpperCase()) {
                case "0":
                    System.out.println("Inserimento dati per la registrazione");
                    registraGuest(gestoreUtenti);
                    break;
                case "1":
                    ricercaPerTitolo(gestoreProiezioni);
                    break;
                case "2":
                    ricercaPerGenere(gestoreProiezioni);
                    break;
                case "3":
                    ricercaPerPrezzo(gestoreProiezioni);
                    break;
                case "4":
                    ricercaPerData(gestoreProiezioni);
                    break;
                case "5":
                    ricercaMixInterattiva(gestoreProiezioni);
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
     * Gestisce l'interazione per la ricerca di una proiezione tramite il titolo del
     * film.
     * Richiede un titolo non vuoto e avvia la ricerca.
     *
     * @param gestoreProiezioni il gestore per effettuare la ricerca
     */
    private static void ricercaPerTitolo(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            System.out.print("Inserisci il titolo del film: ");
            String titolo = sc.nextLine();
            if (titolo.isBlank()) {
                System.err.println("Il titolo non può essere vuoto");
            } else {
                MenuGuest.cercaTitolo(titolo, gestoreProiezioni);
                sel = "esci";
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Gestisce l'interazione per la ricerca di una proiezione per genere
     * cinematografico.
     * Mostra l'elenco dei generi disponibili e valida la scelta dell'utente.
     *
     * @param gestoreProiezioni il gestore per effettuare la ricerca
     */
    private static void ricercaPerGenere(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            for (Genere g : Genere.values()) {
                System.out.print(g + " ");
            }
            System.out.print("\nInserisci il genere del film tra questi: ");
            String genere = sc.nextLine().toUpperCase().trim();
            if (genere.isBlank()) {
                System.err.println("Il genere non può essere vuoto");
            } else {
                try {
                    Genere g = Genere.valueOf(genere);
                    MenuGuest.cercaGenere(g, gestoreProiezioni);
                    sel = "esci";
                } catch (IllegalArgumentException e) {
                    System.err.println("Il genere inserito non esiste. Riprova");
                }
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Gestisce l'interazione per la ricerca di proiezioni tramite prezzo specifico
     * o range di prezzo.
     * Valida i valori numerici inseriti per prevenire inserimenti non validi.
     *
     * @param gestoreProiezioni il gestore per effettuare la ricerca
     */
    private static void ricercaPerPrezzo(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            System.out.println("[1] Prezzo preciso");
            System.out.println("[2] Range prezzo");
            System.out.print("Scegli cosa vuoi fare: ");
            sel = sc.nextLine();
            switch (sel) {
                case "1":
                    System.out.print("Inserisci il prezzo del biglietto: ");
                    try {
                        double prezzo = Double.parseDouble(sc.next().replace(',', '.'));
                        if (prezzo <= 0) {
                            System.err.println("Il prezzo deve essere maggiore di 0");
                        } else {
                            cercaPrezzo(prezzo, prezzo, gestoreProiezioni);
                            sel = "esci";
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Formato non valido.");
                    }
                    break;
                case "2":
                    try {
                        double min, max;
                        System.out.print("Inserisci il prezzo minimo: ");
                        min = Double.parseDouble(sc.next().replace(',', '.'));
                        System.out.print("Inserisci il prezzo massimo: ");
                        max = Double.parseDouble(sc.next().replace(',', '.'));
                        if (min < 0 || min > max) {
                            System.err.println("Il prezzo minimo deve essere >= 0 e minore o uguale al prezzo massimo");
                        } else {
                            cercaPrezzo(min, max, gestoreProiezioni);
                            sel = "esci";
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Formato non valido.");
                    } catch (CostoNonValidoExeption e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                default:
                    System.err.println("Opzione non valida. Riprova");
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Gestisce l'interazione per la ricerca di proiezioni tramite data specifica o
     * range temporale.
     * Valida le date inserite nel formato corretto AAAA-MM-DD.
     *
     * @param gestoreProiezioni il gestore per effettuare la ricerca
     */
    private static void ricercaPerData(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            System.out.println("[1] Data precisa");
            System.out.println("[2] Range data");
            System.out.print("Scegli cosa vuoi fare: ");
            sel = sc.nextLine();
            switch (sel) {
                case "1":
                    System.out.print("Inserisci la data (formato AAAA-MM-DD, es. 2026-05-24): ");
                    String dataString = sc.nextLine().trim();
                    try {
                        LocalDate data = LocalDate.parse(dataString);
                        cercaData(data, data, gestoreProiezioni);
                        sel = "esci";
                    } catch (DateTimeParseException e) {
                        System.out.println(
                                "Errore: Formato data non valido. Utilizzare rigorosamente il pattern AAAA-MM-DD.");
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
                            cercaData(data1, data2, gestoreProiezioni);
                            sel = "esci";
                        } else {
                            System.err.println("La data inizio deve essere minore o uguale della data di fine");
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println(
                                "Errore: Formato data non valido. Utilizzare rigorosamente il pattern AAAA-MM-DD.");
                    }
                    break;
                default:
                    System.err.println("Opzione non valida. Riprova");
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Gestisce la ricerca avanzata combinando diversi filtri inseriti dall'utente.
     * Consente di specificare titolo, genere, date e prezzi in modo facoltativo.
     *
     * @param gestoreProiezioni il gestore per effettuare la ricerca avanzata
     */
    private static void ricercaMixInterattiva(GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel = "";
        do {
            String titolo = null;
            Genere genere = null;
            LocalDate dataIn = null;
            LocalDate dataFin = null;
            double prezzoMin = -1;
            double prezzoMax = -1;

            System.out.print("Inserisci il titolo (vuoto per ignorare): ");
            String titoloInput = sc.nextLine().trim();
            if (!titoloInput.isEmpty()) {
                titolo = titoloInput;
            }

            for (Genere g : Genere.values()) {
                System.out.print(g + " ");
            }
            System.out.print("\nInserisci il genere (vuoto per ignorare): ");
            String genereInput = sc.nextLine().trim();
            if (!genereInput.isEmpty()) {
                try {
                    genere = Genere.valueOf(genereInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.err.println("Genere inesistente: " + genereInput);
                    return;
                }
            }

            System.out.print("Inserisci data inizio (AAAA-MM-DD) o vuoto per ignorare: ");
            String dataInStr = sc.nextLine().trim();
            if (!dataInStr.isEmpty()) {
                System.out.print("Inserisci data fine (AAAA-MM-DD): ");
                String dataFinStr = sc.nextLine().trim();
                if (dataFinStr.isEmpty()) {
                    System.err.println("La data di fine non può essere vuota se la data di inizio è presente.");
                    return;
                }
                try {
                    dataIn = LocalDate.parse(dataInStr);
                    dataFin = LocalDate.parse(dataFinStr);
                } catch (DateTimeParseException e) {
                    System.err.println("Formato data non valido. Usa AAAA-MM-DD.");
                    return;
                }
            }

            System.out.print("Inserisci prezzo minimo (vuoto per ignorare): ");
            String prezzoMinStr = sc.nextLine().trim();
            if (!prezzoMinStr.isEmpty()) {
                System.out.print("Inserisci prezzo massimo: ");
                String prezzoMaxStr = sc.nextLine().trim();
                if (prezzoMaxStr.isEmpty()) {
                    System.err.println("Il prezzo massimo non può essere vuoto se il minimo è presente.");
                    return;
                }
                try {
                    prezzoMin = Double.parseDouble(prezzoMinStr.replace(',', '.'));
                    prezzoMax = Double.parseDouble(prezzoMaxStr.replace(',', '.'));
                } catch (NumberFormatException e) {
                    System.err.println("Prezzo non valido. Inserisci un numero.");
                    return;
                }
            }

            try {
                cercaMix(titolo, genere, dataIn, dataFin, prezzoMin, prezzoMax, gestoreProiezioni);
                sel = "esci";
            } catch (RuntimeException e) {
                System.err.println("Errore nella ricerca mix: " + e.getMessage());
            }
        } while (!sel.equals("esci"));
    }

    /**
     * Gestisce la procedura guidata di registrazione per un nuovo utente Guest
     * (Cliente).
     * Richiede i dati anagrafici e le credenziali, validando l'input dell'utente.
     *
     * @param gestoreUtenti il gestore da utilizzare per registrare il nuovo utente
     */
    public static void registraGuest(GestoreUtenti gestoreUtenti) {// FIXME la data deve essere facoltativa ma se non
                                                                   // inserita da errore
        Scanner sc = new Scanner(System.in);
        String nome, cognome, username, password, luogoDomicilio;
        LocalDate dataNascita = null;
        boolean datiValidi = false;

        do {
            System.out.println("Inserisci il nome: ");
            nome = sc.nextLine().trim();

            System.out.println("Inserisci il cognome: ");
            cognome = sc.nextLine().trim();

            System.out.println("Inserisci lo username: ");
            username = sc.nextLine().trim();

            System.out.println("Inserisci la password: ");
            password = sc.nextLine().trim();

            System.out.println("Inserisci il luogo domicilio: ");
            luogoDomicilio = sc.nextLine().trim();

            if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty()
                    || luogoDomicilio.isEmpty()) {
                System.out.println("Errore: Tutti i campi testuali sono obbligatori. Ricomincia la compilazione.");
                continue;
            }

            System.out.println("Inserisci la data di nascita (formato AAAA-MM-DD, es. 1995-05-24)");
            System.out.print("Campo facoltativo, se non vuoi inserirlo premi invio: ");
            String dataString = sc.nextLine().trim();
            try {
                dataNascita = LocalDate.parse(dataString);

                if (dataNascita.isAfter(LocalDate.now())) {
                    System.out.println("Errore: La data di nascita non può essere nel futuro.");
                    continue;
                }

                datiValidi = true;

            } catch (DateTimeParseException e) {
                System.out.println("Errore: Formato data non valido. Utilizzare rigorosamente il pattern AAAA-MM-DD.");
            }
        } while (!datiValidi);

        // Creo Cliente
        Cliente nuovoCliente = new Cliente(nome, cognome, username, password, dataNascita, Ruolo.CLIENTE,
                luogoDomicilio);

        try {
            gestoreUtenti.registraCliente(nuovoCliente);
            System.out.println(
                    "\n[Successo] Registrazione completata! Ora puoi effettuare il login con lo username: " + username);

        } catch (UtenteUsernameException e) {
            System.out.println("\n[Registrazione Fallita]: " + e.getMessage());

        } catch (Exception e) {
            // Gestione di sicurezza per errori imprevisti generici
            System.out.println("\n[Errore di Sistema]: Impossibile completare l'operazione. Riprova più tardi.");
        }
    }

    /**
     * Effettua la ricerca delle proiezioni per titolo o sottostringa del titolo e
     * le mostra.
     * Mostra i risultati con impaginazione di 25 elementi alla volta.
     *
     * @param titolo            il titolo o parte del titolo del film da cercare
     * @param gestoreProiezioni il gestore delle proiezioni
     */
    public static void cercaTitolo(String titolo, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(titolo);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.toArray().length) {
                    System.out.print("Proiezioni " + index + " su " + valide.toArray().length + ". ");
                    System.out.print("Premere invio per continuare");
                    sc.nextLine();
                }
            } while (index < valide.toArray().length);
        } else {
            System.err.println("non ci sono proiezioni con questo titolo: " + titolo);
        }
    }

    /**
     * Effettua la ricerca delle proiezioni per genere cinematografico e le mostra.
     * Mostra i risultati con impaginazione di 25 elementi alla volta.
     *
     * @param genere            il genere cinematografico da cercare
     * @param gestoreProiezioni il gestore delle proiezioni
     */
    public static void cercaGenere(Genere genere, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(genere);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.toArray().length) {
                    System.out.print("Proiezioni " + index + " su " + valide.toArray().length + ". ");
                    System.out.print("Premere invio per continuare");
                    sc.nextLine();
                }
            } while (index < valide.toArray().length);

        } else {
            System.err.println("non ci sono proiezioni con questo genere: " + genere);
        }
    }

    /**
     * Effettua la ricerca delle proiezioni filtrando per prezzo esatto o range di
     * prezzo e le mostra.
     * Mostra i risultati con impaginazione di 25 elementi alla volta.
     *
     * @param min               il prezzo minimo (o esatto)
     * @param max               il prezzo massimo (o esatto)
     * @param gestoreProiezioni il gestore delle proiezioni
     */
    public static void cercaPrezzo(double min, double max, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(min, max);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.toArray().length) {
                    System.out.print("Proiezioni " + index + " su " + valide.toArray().length + ". ");
                    System.out.print("Premere invio per continuare");
                    sc.nextLine();
                }
            } while (index < valide.toArray().length);
        } else {
            if (min == max) {
                System.err.println("non ci sono proiezioni con questo prezzo: " + min);
            } else {
                System.err.println("non ci sono proiezioni in questo range: " + min + ", " + max);
            }
        }
    }

    /**
     * Effettua la ricerca delle proiezioni filtrando per data esatta o range
     * temporale e le mostra.
     * Mostra i risultati con impaginazione di 25 elementi alla volta.
     *
     * @param dataIn            la data di inizio (o data precisa)
     * @param dataFin           la data di fine (o data precisa)
     * @param gestoreProiezioni il gestore delle proiezioni
     */
    public static void cercaData(LocalDate dataIn, LocalDate dataFin, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(dataIn, dataFin);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.toArray().length) {
                    System.out.print("Proiezioni " + index + " su " + valide.toArray().length + ". ");
                    System.out.print("Premere invio per continuare");
                    sc.nextLine();
                }
            } while (index < valide.toArray().length);
        } else if (dataIn.isEqual(dataFin)) {
            System.err.println("Non ci sono proiezioni in questa data: " + dataIn);
        } else {
            System.err.println("Non ci sono proiezioni in questo range: " + dataIn + ", " + dataFin);
        }
    }

    /**
     * Esegue una ricerca avanzata nel sistema applicando contemporaneamente i
     * filtri compilati.
     * Mostra i risultati con impaginazione di 25 elementi alla volta.
     *
     * @param titolo            il titolo o parte del titolo da filtrare (null se
     *                          ignorato)
     * @param genere            il genere cinematografico da filtrare (null se
     *                          ignorato)
     * @param dataIn            la data di inizio da filtrare (null se ignorato)
     * @param dataFin           la data di fine da filtrare (null se ignorato)
     * @param prezzoMin         il prezzo minimo da filtrare (-1 se ignorato)
     * @param prezzoMax         il prezzo massimo da filtrare (-1 se ignorato)
     * @param gestoreProiezioni il gestore delle proiezioni
     */
    public static void cercaMix(String titolo, Genere genere, LocalDate dataIn, LocalDate dataFin, double prezzoMin,
            double prezzoMax, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(titolo, genere, dataIn, dataFin, prezzoMin,
                prezzoMax);
        if (!valide.isEmpty()) {
            int index = 0;
            do {
                gestoreProiezioni.visualizzaProiezioni(valide, index);
                index += 25;
                if (index < valide.toArray().length) {
                    System.out.print("Proiezioni " + index + " su " + valide.toArray().length + ". ");
                    System.out.print("Premere invio per continuare");
                    sc.nextLine();
                }
            } while (index < valide.toArray().length);
        } else {
            System.err.println("Non ci sono proiezioni con i filtri selezionati.");
        }
    }
}
