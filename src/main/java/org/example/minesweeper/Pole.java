package org.example.minesweeper;

class Pole {
    private boolean jeMina;
    private StavPola stav;
    private int pocetSusednychMin;
    private int riadok;
    private int stlpec;

    public Pole(int riadok, int stlpec) {
        this.riadok = riadok;
        this.stlpec = stlpec;
        this.jeMina = false;
        this.stav = StavPola.SKRYTE;
        this.pocetSusednychMin = 0;
    }

    // Gettery a settery
    public boolean jeMina() { return jeMina; }
    public void setMina(boolean mina) { this.jeMina = mina; }

    public StavPola getStav() { return stav; }
    public void setStav(StavPola stav) { this.stav = stav; }

    public int getPocetSusednychMin() { return pocetSusednychMin; }
    public void setPocetSusednychMin(int pocet) { this.pocetSusednychMin = pocet; }

    public int getRiadok() { return riadok; }
    public int getStlpec() { return stlpec; }

    public boolean jeOdkryte() { return stav == StavPola.ODKRYTE; }
    public boolean jeOznacene() { return stav == StavPola.OZNACENE; }
}
