package cinemax.modelli;

import java.time.format.DateTimeFormatter;

public class Prenotazione {
    private String codiceUnivoco; // Richiesto univoco dalle specifiche
    private Cliente cliente;
    private Proiezione proiezione;
    private int numeroPosti;

    public Prenotazione(String codiceUnivoco, Cliente cliente, Proiezione proiezione, int numeroPosti) {
        this.codiceUnivoco = codiceUnivoco;
        this.cliente = cliente;
        this.proiezione = proiezione;
        this.numeroPosti = numeroPosti;
    }

    // Getters
    public String getCodiceUnivoco() { return codiceUnivoco; }
    public Cliente getCliente() { return cliente; }
    public Proiezione getProiezione() { return proiezione; }
    public int getNumeroPosti() { return numeroPosti; }

    /**
     * Calcola il costo totale della prenotazione moltiplicando il numero di posti
     * per il costo del singolo biglietto della proiezione associata.
     * @return Il costo totale in euro.
     */
    public double calcolaCostoTotale() {
        return this.numeroPosti * this.proiezione.getCostoBiglietto();
    }

    @Override
    public String toString() {
        return String.format("Prenotazione #%s\nCliente: %s %s\nSpettacolo: %s\nPosti: %d | Totale: %.2f€",
                codiceUnivoco, cliente.getNome(), cliente.getCognome(),
                proiezione.getFilm().getTitolo(), numeroPosti, calcolaCostoTotale());
    }

    public String toCsv() {
        // Creo il formatter per la data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Formatto la data
        String dataFormattata = this.proiezione.getDataOra().format(formatter);

        // Stringa finale
        return String.format("%s,%s,\"%s\",\"%s\",%d",
                this.codiceUnivoco,
                this.cliente.getUsername(),
                this.proiezione.getFilm().getTitolo(),
                dataFormattata,
                this.numeroPosti);
    }
}
