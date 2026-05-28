package cinemax.menu;

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

public class MenuGuest {

    public static void menu(GestoreUtenti gestoreUtenti, GestoreProiezioni gestoreProiezioni) {
        Scanner sc = new Scanner(System.in);
        String sel;

        do {
            System.out.println("Benvenuto nel menu Guest");
            System.out.println("[0] Registrati");
            System.out.println("[1] Ricerca proiezione per titolo o sotto-stringa del titolo");
            System.out.println("[2] Ricerca proiezione per genere");
            System.out.println("[3] Ricerca proiezione per prezzo"); //caso prezzo preciso o range
            System.out.println("[4] Ricerca proiezione per data"); //caso data precisa o range
            System.out.println("[5] Ricerca proiezione mix");
            System.out.println("[X] esci");
            sel = sc.nextLine();

            switch (sel) {
                case "0":
                    System.out.println("Inserimento dati per la registrazione");
                    registraGuest(gestoreUtenti);

                    break;
                case "1":
                    System.out.printf("Inserisci il titolo del film: ");
                    String titolo = sc.nextLine();
                    if (titolo.isBlank()) {
                        System.err.println("Il titolo non può essere vuoto");
                    }else {
                        MenuGuest.cercaTitolo(titolo, gestoreProiezioni);
                    }
                    break;
                case "2":

                    for(Genere g: Genere.values()) {
                        System.out.print(g + " ");
                    }
                    System.out.printf("\nInserisci il genere del film tra questi: ");
                    String genere = sc.nextLine();
                    if (genere.isBlank()) {
                        System.err.println("Il genere non può essere vuoto");
                    }else {
                        MenuGuest.cercaGenere(genere, gestoreProiezioni);
                    }
                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
                case "X":
                    break;
                default:

            }

        } while ();
    }

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

    public static void cercaTitolo(String titolo, GestoreProiezioni gestoreProiezioni) {
        List<Proiezione> valide = gestoreProiezioni.cercaProiezione(titolo);
        if (!valide.isEmpty()) {
            //todo fare metodo in GestorePoriezioni visualizzaProiezione(List, int indice) visualizza da quel indice le successive 25 prenotazioni
            //devo fire whilw per continuare nel vedere le proiezioni
        }else {
            System.err.println("non ci sono proiezioni con questo titolo: " + titolo);
        }
    }

    public static void cercaGenere(String genere, GestoreProiezioni gestoreProiezioni) {
        try {
            Genere.valueOf(genere.toUpperCase());
            List<Proiezione> valide = gestoreProiezioni.cercaProiezione(genere);
            if (!valide.isEmpty()) {
                //todo fare metodo in GestorePoriezioni visualizzaProiezione(List, int indice) visualizza da quel indice le successive 25 prenotazioni
                //devo fire whilw per continuare nel vedere le proiezioni

            }else {
                System.err.println("non ci sono proiezioni con questo genere: " + genere);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Genere inseistente: " + genere);
        }

    }
}

/*
è possibile proseguire come utente guest indicando solo il nome
(anche parziale) di un film ed accedere alla funzionalità che non richiedono di
essere autenticati presso il sistema
L’applicazione deve almeno consentire di

Visualizzare l’elenco delle proiezioni per il film specificato (se esistenti)

Cercare proiezioni

Visualizzare i dettagli di una proiezione

Registrarsi all’applicazione come cliente
 */
