package cinemax.gestori;

import cinemax.modelli.Film;
import cinemax.modelli.Proiezione;
import cinemax.modelli.Genere; // Assumendo che esista l'enum
import eccezioni.CostoNonValidoExeption;
import eccezioni.DataNonValidaExeption;
import eccezioni.NumeroCampiErratoExeption;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestoreProiezioni {

    private List<Proiezione> palinsesto;
    private final String FILE_PATH = "data" + File.separator + "proiezioni.csv";

    public GestoreProiezioni() {
        this.palinsesto = new ArrayList<>();
    }

    // --- METODI PER IL PROIEZIONISTA ---

    /**
     * Agggiunge una proiezione al palinsesto se non è già presente
     * @param proiezione
     * @throws NumeroCampiErratoExeption se la proiezione è null
     * @return true se l'aggiunta è andata a buon fine altrimenti false
     */
    public boolean aggiungiProiezione(Proiezione proiezione) {
        if (proiezione == null) {
            throw new NullPointerException("La proiezione è null");
        }
        if (palinsesto.contains(proiezione)) {
            return false;
        }
        return palinsesto.add(proiezione);
    }

    /**
     * Modifica la data di una proiezione
     * @param proiezione
     * @param dataOra
     * @throws NumeroCampiErratoExeption se dataOra è null
     * @return ritorna true se la proiezione esite ed e stata modificata altrimenti false
     */
    public boolean modificaProiezione(Proiezione proiezione, LocalDateTime dataOra) {
        if (dataOra == null) {
            throw new NullPointerException("la nova data e ora non devono essere null");
        }
        if (palinsesto.contains(proiezione)) {
            for(Proiezione p : palinsesto){
                if (p.equals(proiezione)) {
                    p.setDataOra(dataOra);
                    palinsesto.sort(null);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Rimuove una proiezione dal palinsesto
     * @param proiezione
     * @return true se la proiezione era presente ed è stata rimossa altrimenti false
     */
    public boolean eliminaProiezione(Proiezione proiezione) {
        return palinsesto.remove(proiezione);
    }

    // --- METODI DI RICERCA (Guest, Cliente, Bigliettaio) ---

    /**
     * Cerca tutte le proiezioni di un determinato titolo di un film senza contare le maiuscole o minuscole
     * @param titoloFilm
     * @return lista delle prenotazioni con quel determinato film
     */
    public List<Proiezione> cercaProiezione(String titoloFilm) {
        if (titoloFilm == null) {
            throw new NullPointerException("Il titolo del film non può essere null");
        }
        if (titoloFilm.isEmpty()) {
            return null;
        }
        List<Proiezione> proiezioni = new ArrayList<>();
        for (Proiezione p : palinsesto) {
            if (p.getFilm().getTitolo().toLowerCase().contains(titoloFilm.toLowerCase())) {
                proiezioni.add(p);
            }
        }
        return proiezioni;
    }

    /**
     * Cerca tutte le proiezioni di un determinato genere di film
     * @param genere
     * @throws NumeroCampiErratoExeption se il genere è null
     * @return lista delle proiezioni valide
     */
    public List<Proiezione> cercaProiezione(Genere genere) {
        if (genere == null) {
            throw new NullPointerException("Il genere è null");
        }
        List<Proiezione> proiezioni = new ArrayList<>();
        for (Proiezione p : palinsesto) {
            if (p.getFilm().getGenere().equals(genere)) {
                proiezioni.add(p);
            }
        }
        return proiezioni;
    }

    /**
     * Cerca tutte le proiezioni programmate in un determinato intervallo di tempo
     * Se la data iniziale è uguale alla data finale cerca tutte le proiezioni programmate in quella data specifica
     * @param dataInizio
     * @param dataFine
     * @throws NullPointerException se la data inizio e/o la data di fine sono null
     * @throws DataNonValidaExeption se la data inizio viene dopo la data di fine
     * @return lista delle proiezioni valide
     */
    public List<Proiezione> cercaProiezione(LocalDate dataInizio, LocalDate dataFine) {
        List<Proiezione> proiezioni = new ArrayList<>();
        if (dataInizio == null || dataFine == null) {
            throw new NullPointerException("dataInizio e dataFine non devono essere null!");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new DataNonValidaExeption("La data d'inizio è maggiore della data di fine: dataIn = " + dataInizio + ", dataFine = " + dataFine);
        }
        for (Proiezione p : palinsesto) {
            if (p.getDataOra().toLocalDate().isAfter(dataInizio.minusDays(1)) && p.getDataOra().toLocalDate().isBefore(dataFine.plusDays(1))) {
                proiezioni.add(p);
            }else if (dataInizio.isEqual(dataFine)) {
                if (p.getDataOra().toLocalDate().isEqual(dataInizio)) {
                    proiezioni.add(p);
                }
            }
        }
        return proiezioni;
    }

    /**
     * Cerca tutte le proiezioni che hanno un prezzo compreso tra prezzoMin e prezzoMax
     * Se prezzoMin e prezzoMax sono uguali cerca le poiezioni con un costo specifico
     * @param prezzoMin
     * @param prezzoMax
     * @throws CostoNonValidoExeption se il prezzoMin < 0  o prezzoMax < 0
     * @throws CostoNonValidoExeption se il prezzoMin > prezzoMax
     * @return lista di proiezioni valide
     */
    public List<Proiezione> cercaProiezione(double prezzoMin,  double prezzoMax) {
        List<Proiezione> proiezioni = new ArrayList<>();
        if (prezzoMin < 0 || prezzoMax < 0) {
            throw new CostoNonValidoExeption("Il prezzo minimo e massimo devono essere >= 0: prezzoMin = " + prezzoMin + ", prezzoMax = " + prezzoMax);
        }
        if (prezzoMin > prezzoMax) {
            throw new CostoNonValidoExeption("Il prezzo minimo deve essere minore o uguale del prezzo massimo: prezzoMin = " + prezzoMin + ", prezzoMax = " + prezzoMax);
        }

        for (Proiezione p : palinsesto) {
            if (p.getCostoBiglietto() >= prezzoMin && p.getCostoBiglietto() <= prezzoMax) {
                proiezioni.add(p);
            }
        }

        return proiezioni;
    }

    /**
     *
     * @param titolo
     * @param genere
     * @param dataInizio
     * @param dataFine
     * @param prezzoMin
     * @param prezzoMax
     * @return
     */
    public List<Proiezione> cercaProiezione(String titolo, Genere genere, LocalDate dataInizio, LocalDate dataFine, double prezzoMin, double prezzoMax) {
        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
            throw new DataNonValidaExeption("La data d'inizio è maggiore della data di fine: dataIn = " + dataInizio + ", dataFine = " + dataFine);
        }
        if (prezzoMin >= 0 && prezzoMax >= 0 && prezzoMin > prezzoMax) {
            throw new CostoNonValidoExeption("Il prezzo minimo deve essere minore o uguale del prezzo massimo: prezzoMin = " + prezzoMin + ", prezzoMax = " + prezzoMax);
        }

        List<Proiezione> proiezioni = new ArrayList<>();

        for (Proiezione p : palinsesto) {
            // Filtro titolo
            if (titolo != null && !titolo.trim().isEmpty()) {
                if (!p.getFilm().getTitolo().toLowerCase().contains(titolo.toLowerCase())) {
                    continue; // Scarta e passa alla prossima
                }
            }
            // Filtro genere
            if (genere != null && !p.getFilm().getGenere().equals(genere)) {
                continue;
            }
            // Filtro date
            if (dataInizio != null && dataFine != null) {
                LocalDate dataP = p.getDataOra().toLocalDate();
                if (dataP.isBefore(dataInizio) || dataP.isAfter(dataFine)) {
                    continue;
                }
            }
            // Filtro prezzo
            if (prezzoMin >= 0 && prezzoMax >= 0) {
                if (p.getCostoBiglietto() < prezzoMin || p.getCostoBiglietto() > prezzoMax) {
                    continue;
                }
            }
            proiezioni.add(p);
        }
        return proiezioni;
    }


    // --- GESTIONE FILE ---
    public void caricaDaFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            reader.readLine();
            String line = reader.readLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (line != null) {

                String[] dati = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for(int i = 0; i < dati.length; i++){
                    dati[i] = dati[i].replace("\"", "");
                }

                if (dati.length != 8) {
                    throw new NumeroCampiErratoExeption("Campi richiesti 8, ricevuti: " + dati.length);
                }

                palinsesto.add(
                        new Proiezione(
                                LocalDateTime.parse(dati[0], formatter),        //data e ora proiezione
                                new Film(
                                        dati[1],                                //titolo
                                        Genere.valueOf(dati[2].toUpperCase().replace('-', '_' )),  //genere
                                        dati[3],                                //regista
                                        Integer.parseInt(dati[4]),              //anno
                                        Integer.parseInt(dati[5]),              //durata
                                        Integer.parseInt(dati[6])               //eta minima
                                        ),
                                Double.parseDouble(dati[7])                     //prezzo biglietto
                        )
                );

                line = reader.readLine();

            }

            fileReader.close();
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void salvaSuFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(file, false);
            PrintStream ps = new PrintStream(fos);

            ps.println("data_ora_proiezione,titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto");

            for (Proiezione p : this.palinsesto) {
                ps.println(p.toCsv());
            }
            ps.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}