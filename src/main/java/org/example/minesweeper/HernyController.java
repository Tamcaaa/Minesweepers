package org.example.minesweeper;

import java.util.ArrayList;
import java.util.List;

class HernyController {
    private MinyMapa mapa;
    private StavHry stavHry;
    private int pocetTahov;
    private List<HernyZaznam> historiHier;
    private HernyZaznam aktualnaHra;

    public HernyController() {
        this.historiHier = new ArrayList<>();
        this.stavHry = StavHry.HRANIE;
        this.pocetTahov = 0;
    }

    public void novaHra(int riadky, int stlpce, int pocetMin) {
        // Ukončenie predchádzajúcej hry
        if (aktualnaHra != null) {
            aktualnaHra.ukoncHru(stavHry == StavHry.VYHRA, pocetTahov);
            historiHier.add(aktualnaHra);
        }

        // Spustenie novej hry
        this.mapa = new MinyMapa(riadky, stlpce, pocetMin);
        this.stavHry = StavHry.HRANIE;
        this.pocetTahov = 0;
        this.aktualnaHra = new HernyZaznam(riadky, stlpce, pocetMin);
    }

    public RezultatTahu odkryPole(int riadok, int stlpec) {
        if (stavHry != StavHry.HRANIE) {
            return new RezultatTahu(false, "Hra už skončila!");
        }

        Pole pole = mapa.getPole(riadok, stlpec);
        if (pole == null || pole.jeOdkryte() || pole.jeOznacene()) {
            return new RezultatTahu(false, "Pole nie je možné odkryť!");
        }

        pocetTahov++;

        if (pole.jeMina()) {
            pole.setStav(StavPola.ODKRYTE);
            stavHry = StavHry.PREHRA;
            odkryVsetkyMiny();
            return new RezultatTahu(true, "Prehra - narazili ste na mínu!");
        }

        // Rekurzívne odkrývanie prázdnych polí
        odkryPrazdnePolia(riadok, stlpec);

        // Kontrola výhry
        if (skontrolujVyhru()) {
            stavHry = StavHry.VYHRA;
            return new RezultatTahu(true, "Gratulujeme! Vyhráli ste!");
        }

        return new RezultatTahu(true, "Pole odkryté");
    }

    private void odkryPrazdnePolia(int riadok, int stlpec) {
        Pole pole = mapa.getPole(riadok, stlpec);
        if (pole == null || pole.jeOdkryte() || pole.jeMina()) {
            return;
        }

        pole.setStav(StavPola.ODKRYTE);

        // Ak má pole 0 susedných mín, odkryjeme aj susedné polia
        if (pole.getPocetSusednychMin() == 0) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di != 0 || dj != 0) {
                        odkryPrazdnePolia(riadok + di, stlpec + dj);
                    }
                }
            }
        }
    }

    public RezultatTahu oznacPole(int riadok, int stlpec) {
        if (stavHry != StavHry.HRANIE) {
            return new RezultatTahu(false, "Hra už skončila!");
        }

        Pole pole = mapa.getPole(riadok, stlpec);
        if (pole == null || pole.jeOdkryte()) {
            return new RezultatTahu(false, "Pole nie je možné označiť!");
        }

        if (pole.jeOznacene()) {
            pole.setStav(StavPola.SKRYTE);
        } else {
            pole.setStav(StavPola.OZNACENE);
        }

        return new RezultatTahu(true, "Pole označené/odznačené");
    }

    private boolean skontrolujVyhru() {
        for (int i = 0; i < mapa.getRiadky(); i++) {
            for (int j = 0; j < mapa.getStlpce(); j++) {
                Pole pole = mapa.getPole(i, j);
                if (!pole.jeMina() && !pole.jeOdkryte()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void odkryVsetkyMiny() {
        for (int i = 0; i < mapa.getRiadky(); i++) {
            for (int j = 0; j < mapa.getStlpce(); j++) {
                Pole pole = mapa.getPole(i, j);
                if (pole.jeMina()) {
                    pole.setStav(StavPola.ODKRYTE);
                }
            }
        }
    }

    // Gettery
    public MinyMapa getMapa() { return mapa; }
    public StavHry getStavHry() { return stavHry; }
    public int getPocetTahov() { return pocetTahov; }
    public List<HernyZaznam> getHistoriaHier() { return new ArrayList<>(historiHier); }
}

