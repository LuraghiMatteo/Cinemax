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
 * Gestisce l'interfaccia utente testuale per gli utenti non autenticati (Guest).
 * Permette la registrazione di nuovi clienti e la ricerca di proiezioni cinematografiche
 * attraverso diversi filtri (titolo, genere, prezzo, data).
 *
 * @author Matteo Luraghi - Matr: 765632 - Sede: VA
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */
public class MenuGuest {

    /**
     * Visualizza il menu principale per l'utente Guest e gestisce la navigazione tra le sotto-funzioni.
     *
     * @param gestoreUtenti
     * @param gestoreProiezioni
     * @return true se l'operazione è completata correttamente.
     */
    public static boolean menu(GestoreUtenti gestoreUtenti, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel;
        boolean chiudi = false;
        do {
            System.out.println("Benvenuto nel menu Guest");
            System.out.println("[0] Registrati");
            System.out.println("[1] Ricerca proiezione per titolo o sotto-stringa del titolo");
            System.out.println("[2] Ricerca proiezione per genere");
            System.out.println("[3] Ricerca proiezione per prezzo");
            System.out.println("[4] Ricerca proiezione per data");
            System.out.println("[5] Ricerca proiezione mix");
            System.out.println("[X] esci");
            sel = sc.nextLine();

            switch (sel) {
                case "0":
                    System.out.println("Inserimento dati per la registrazione");
                    registraGuest(gestoreUtenti);
                    break;
                case "1": //titolo
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
                    break;
                case "2": //genere
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
                                Genere g =  Genere.valueOf(genere.toUpperCase());
                                MenuGuest.cercaGenere(g, gestoreProiezioni);
                                sel = "esci";
                            }catch(IllegalArgumentException e) {
                                System.err.println("Il genere inserito non esiste. Riprova");
                            }
                        }
                    } while (!sel.equals("esci"));
                    break;
                case "3"://prezzo
                    do {
                        System.out.println("[1] prezzo preciso");
                        System.out.println("[2] range prezzo");
                        System.out.print("Scaegli cosa vuoi fare: ");
                        sel = sc.nextLine();
                        switch (sel) {
                            case "1":
                                System.out.print("Inserisci il prezzo del biglietto: ");
                                double prezzo = Double.parseDouble(sc.next().replace(',', '.'));
                                if (prezzo <= 0)
                                    System.err.println("Il prezzo deve essere maggiore di 0");
                                else {
                                    cercaPrezzo(prezzo, prezzo, gestoreProiezioni);
                                    sel = "esci";
                                }
                                break;
                            case "2":
                                try{
                                    double min, max;
                                    System.out.print("Inserisci il prezzo minimo: ");
                                    min = Double.parseDouble(sc.next().replace(',', '.'));
                                    System.out.print("Inserisci il prezzo massimo: ");
                                    max = Double.parseDouble(sc.next().replace(',', '.'));
                                    if (min >= 0 && max >= min)
                                        System.err.println("Il prezzo deve essere maggiore di 0 e il prezzo minimo deve essere minore del prezzo massimo");
                                    else {
                                        cercaPrezzo(min, max, gestoreProiezioni);
                                        sel = "esci";
                                    }
                                }catch (CostoNonValidoExeption e){
                                    System.err.println(e.getMessage());
                                }
                                break;
                            default:
                                System.err.println("opzione non valida. Riprova");
                        }
                    } while (!sel.equals("esci"));
                    break;
                case "4"://data
                    do {
                        System.out.println("[1] data precisa");
                        System.out.println("[2] range data");
                        System.out.print("Scaegli cosa vuoi fare: ");
                        sel = sc.nextLine();
                        switch (sel) {
                            case "1":
                                System.out.print("Inserisci la data (formato AAAA-MM-DD, es. 2026-05-24): ");
                                String dataString = sc.nextLine().trim();
                                LocalDate data;
                                try {
                                    data = LocalDate.parse(dataString);
                                    cercaData(data, data, gestoreProiezioni);
                                    sel = "esci";

                                } catch (DateTimeParseException e) {
                                    System.out.println("Errore: Formato data non valido. Utilizzare rigorosamente il pattern AAAA-MM-DD.");
                                }
                                break;
                            case "2":
                                String dataIn, dataFin;
                                System.out.print("Inserisci data inizio (formato AAAA-MM-DD, es. 2026-05-24): ");
                                dataIn = sc.nextLine().trim();
                                System.out.print("Inserisci data fine (formato AAAA-MM-DD, es. 2026-05-24): ");
                                dataFin = sc.nextLine().trim();
                                LocalDate data1, data2;
                                try {
                                    data1 = LocalDate.parse(dataIn);
                                    data2 = LocalDate.parse(dataFin);
                                    if (data1.isBefore(data2)) {
                                        cercaData(data1, data2, gestoreProiezioni);
                                        sel = "esci";
                                    } else {
                                        System.err.println("La data inizio deve essere minore della data di fine");
                                    }
                                } catch (DateTimeParseException e) {
                                    System.out.println("Errore: Formato data non valido. Utilizzare rigorosamente il pattern AAAA-MM-DD.");
                                }
                            default:
                                System.err.println("opzione non valida. Riprova");
                        }
                    } while (!sel.equals("esci"));
                    break;
                case "5":
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
                                break;
                            }
                        }

                        System.out.print("Inserisci data inizio (AAAA-MM-DD) o vuoto per ignorare: ");
                        String dataInStr = sc.nextLine().trim();
                        if (!dataInStr.isEmpty()) {
                            System.out.print("Inserisci data fine (AAAA-MM-DD): ");
                            String dataFinStr = sc.nextLine().trim();
                            if (dataFinStr.isEmpty()) {
                                System.err.println("La data di fine non può essere vuota se la data di inizio è presente.");
                                break;
                            }
                            try {
                                dataIn = LocalDate.parse(dataInStr);
                                dataFin = LocalDate.parse(dataFinStr);
                            } catch (DateTimeParseException e) {
                                System.err.println("Formato data non valido. Usa AAAA-MM-DD.");
                                break;
                            }
                        }

                        System.out.print("Inserisci prezzo minimo (vuoto per ignorare): ");
                        String prezzoMinStr = sc.nextLine().trim();
                        if (!prezzoMinStr.isEmpty()) {
                            System.out.print("Inserisci prezzo massimo: ");
                            String prezzoMaxStr = sc.nextLine().trim();
                            if (prezzoMaxStr.isEmpty()) {
                                System.err.println("Il prezzo massimo non può essere vuoto se il minimo è presente.");
                                break;
                            }
                            try {
                                prezzoMin = Double.parseDouble(prezzoMinStr);
                                prezzoMax = Double.parseDouble(prezzoMaxStr);
                            } catch (NumberFormatException e) {
                                System.err.println("Prezzo non valido. Inserisci un numero.");
                                break;
                            }
                        }

                        try {
                            cercaMix(titolo, genere, dataIn, dataFin, prezzoMin, prezzoMax, gestoreProiezioni);
                            sel = "esci";
                        } catch (RuntimeException e) {
                            System.err.println("Errore nella ricerca mix: " + e.getMessage());
                        }
                    } while (!sel.equals("esci"));
                    break;
                case "X":
                    chiudi = true;
                    break;
                default:
                    System.err.println("opzione non valida. Riprova");

            }

        } while (!chiudi);

        return true;
    }

    /**
     * Gestisce la procedura di registrazione guidata per un nuovo cliente.
     * Richiede l'inserimento di dati anagrafici e credenziali attraverso lo standard input.
     *
     * @param gestoreUtenti
     */
    public static void registraGuest(GestoreUtenti gestoreUtenti) {
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

            if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty() || luogoDomicilio.isEmpty()) {
                System.out.println("Errore: Tutti i campi testuali sono obbligatori. Ricomincia la compilazione.");
                continue;
            }

            // Data Nascita
            System.out.print("Inserisci la data di nascita (formato AAAA-MM-DD, es. 1995-05-24): ");
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
        Cliente nuovoCliente = new Cliente(nome, cognome, username, password, dataNascita, Ruolo.CLIENTE, luogoDomicilio);

        try {
            gestoreUtenti.registraCliente(nuovoCliente);
            System.out.println("\n[Successo] Registrazione completata! Ora puoi effettuare il login con lo username: " + username);

        } catch (UtenteUsernameException e) {
            System.out.println("\n[Registrazione Fallita]: " + e.getMessage());

        } catch (Exception e) {
            // Gestione di sicurezza per errori imprevisti generici
            System.out.println("\n[Errore di Sistema]: Impossibile completare l'operazione. Riprova più tardi.");
        }
    }

    /**
     * Esegue una ricerca delle proiezioni per titolo o sotto-stringa del titolo.
     *
     * @param titolo
     * @param gestoreProiezioni
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
     * Esegue una ricerca delle proiezioni in base al genere cinematografico.
     *
     * @param genere
     * @param gestoreProiezioni
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
     * Esegue una ricerca delle proiezioni in base a un prezzo specifico o a un intervallo di prezzo.
     *
     * @param min
     * @param max
     * @param gestoreProiezioni
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
     * Esegue una ricerca delle proiezioni in base a una data specifica o a un intervallo temporale.
     *
     * @param dataIn
     * @param dataFin
     * @param gestoreProiezioni
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
     * Esegue una ricerca avanzata combinando diversi filtri (titolo, genere, data e prezzo).
     * Se un filtro è null o non impostato, non viene applicato alla ricerca.
     *
     * @param titolo
     * @param genere
     * @param dataIn
     * @param dataFin
     * @param prezzoMin
     * @param prezzoMax
     * @param gestoreProiezioni
     */
    public static void cercaMix(String titolo, Genere genere, LocalDate dataIn, LocalDate dataFin, double prezzoMin, double prezzoMax, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(titolo, genere, dataIn, dataFin, prezzoMin, prezzoMax);
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

