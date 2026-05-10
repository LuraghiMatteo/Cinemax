package cinemax.modelli;

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

    // Firme dei metodi di calcolo (Logica)
    public double calcolaCostoTotale() {
        // Sarà: numeroPosti * proiezione.getCostoBiglietto()
        return 0.0;
    }

    @Override
    public String toString() {
        return String.format("Prenotazione #%s\nCliente: %s %s\nSpettacolo: %s\nPosti: %d | Totale: %.2f€",
                codiceUnivoco, cliente.getNome(), cliente.getCognome(),
                proiezione.getFilm().getTitolo(), numeroPosti, calcolaCostoTotale());
    }
}
