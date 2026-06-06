package cinemax;

import cinemax.gestori.GestoreProiezioni;
import cinemax.gestori.GestoreUtenti;
import cinemax.menu.MenuBigliettaio;
import cinemax.menu.MenuCliente;
import cinemax.menu.MenuGuest;
import cinemax.menu.MenuProiezionista;
import cinemax.modelli.Utente;
import java.util.Scanner;

public class Cinemax {
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        String sel;
        GestoreUtenti gestoreUtenti = new GestoreUtenti();
        gestoreUtenti.caricaDaFile();
        GestoreProiezioni gestoreProiezioni = new GestoreProiezioni();
        gestoreProiezioni.caricaDaFile();

        boolean chiudi = false;

        do {
            System.out.println("Benvenuto in Cinemax!! ");
            System.out.println("Vuoi proseguire con l'autenticazione oppure come guest?");
            System.out.println("[1] Login");
            System.out.println("[2] Guest");
            System.out.println("[3] Registrati");
            System.out.println("[X] esci");
            System.out.printf("Scegli opzione: ");
            sel = sc.next().toUpperCase();

            switch (sel) {
                case "1":
                    Utente utente = gestoreUtenti.login("", "");
                    if (utente != null) {
                        switch (utente.getRuolo()) {
                            case CLIENTE:
                                MenuCliente.menu();
                                break;
                            case BIGLIETTAIO:
                                MenuBigliettaio.menu();
                                break;
                            case PROIEZIONISTA:
                                MenuProiezionista.menu();
                                break;
                            default:
                                System.err.println("credenziali non valide. Se non sei registrato registrati");
                        }
                    }
                    break;
                case "2":
                    chiudi = MenuGuest.menu(gestoreUtenti, gestoreProiezioni);
                    break;
                case "3":
                    MenuGuest.registraGuest(gestoreUtenti);
                    MenuCliente.menu();
                    break;
                case "X":
                    System.out.println("Uscita dal programma");
                    break;
                default:
                    System.err.println("Comando non valido. Riprova");
            }

        }while (!sel.equals("X") && !chiudi);

    }
}
