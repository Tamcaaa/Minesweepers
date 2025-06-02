package org.example.minesweeper;

class HernyZaznam {
    private long trvanie; // v sekundách
    private int riadky;
    private int stlpce;
    private int pocetMin;
    private boolean vyhral;
    private int pocetTahov;
    private long casSpustenia;

    public HernyZaznam(int riadky, int stlpce, int pocetMin) {
        this.riadky = riadky;
        this.stlpce = stlpce;
        this.pocetMin = pocetMin;
        this.casSpustenia = System.currentTimeMillis();
        this.pocetTahov = 0;
        this.vyhral = false;
    }

    public void ukoncHru(boolean vyhral, int pocetTahov) {
        this.vyhral = vyhral;
        this.pocetTahov = pocetTahov;
        this.trvanie = (System.currentTimeMillis() - casSpustenia) / 1000;
    }

    // Gettery
    public long getTrvanie() { return trvanie; }
    public int getRiadky() { return riadky; }
    public int getStlpce() { return stlpce; }
    public int getPocetMin() { return pocetMin; }
    public boolean isVyhral() { return vyhral; }
    public int getPocetTahov() { return pocetTahov; }

    @Override
    public String toString() {
        return String.format("Hra %dx%d, %d mín, %ds, %d ťahov, %s",
                riadky, stlpce, pocetMin, trvanie, pocetTahov,
                vyhral ? "Výhra" : "Prehra");
    }
}
