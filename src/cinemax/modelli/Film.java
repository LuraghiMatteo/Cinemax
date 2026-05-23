package cinemax.modelli;

import java.util.Objects;

public class Film {

    private String titolo;
    private Genere genere;
    private String regista;
    private int anno;
    private int durata; // in minuti
    private int etaMinima;

    public Film(String titolo, Genere genere, String regista, int anno, int durata, int etaMinima) {
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata = durata;
        this.etaMinima = etaMinima;
    }

    // Getters e Setters
    public String getTitolo() { return titolo; }
    public Genere getGenere() { return genere; }
    public String getRegista() { return regista; }
    public int getAnno() { return anno; }
    public int getDurata() { return durata; }
    public int getEtaMinima() { return etaMinima; }

    public String toCSV() {
        return "\"" + titolo + "\"," +
                genere + "," +
                "\"" + regista + "\"," +
                anno + "," +
                durata + "," +
                etaMinima;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Film film)) return false;
        return anno == film.anno && durata == film.durata && etaMinima == film.etaMinima && Objects.equals(titolo, film.titolo) && genere == film.genere && Objects.equals(regista, film.regista);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titolo, genere, regista, anno, durata, etaMinima);
    }

    @Override
    public String toString() {
        // Gestione per l'età minima
        String etaFormattata = (etaMinima > 0) ? etaMinima + "+" : "Per tutti";

        // String.format per incolonnare bene i dati
        return String.format("Titolo: %-25s | Anno: %4d | Regista: %-20s | Genere: %-12s | Durata: %3d min | Età: %s",
                titolo, anno, regista, genere, durata, etaFormattata);
    }
}